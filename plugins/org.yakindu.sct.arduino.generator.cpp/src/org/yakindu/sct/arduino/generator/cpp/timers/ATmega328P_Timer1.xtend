/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

import com.google.inject.Inject
import org.yakindu.sct.arduino.generator.cpp.Naming
import org.yakindu.sct.model.sexec.ExecutionFlow

class ATmega328P_Timer1 extends AbstractTimerCodeGenerator {

	@Inject extension Naming

	override timerName() {
		"ATmega328P_Timer1"
	}

	override generateTimer(ExecutionFlow it) '''
		«header»

		#include "«timerName.h»"

		«variableDeclarations»
		
		«ISR»

		«constructor»
		
		«start»
		
		«init»

		«setTimer»
		
		«unsetTimer»

		«runCycle»

		«raiseTimeEvents»
		
		«sleep»

		«cancel»
	'''

	protected def variableDeclarations() '''
		const int MAX_PERIOD = 4192;
		const int MAX_OVERFLOW_COUNTER = 65499;

		bool runCycleFlag = false;
		unsigned int overflows = 0;
		unsigned int overflowCounter = 0;
		unsigned int moduloRest = 0;
	'''

	override protected privateHeaderPart() '''
		«super.privateHeaderPart()»

		void sleep();
	'''

	protected def ISR() '''
		ISR(TIMER1_COMPA_vect) {
			if (overflowCounter < (overflows - 1)) {
				overflowCounter++;
			} else if (overflowCounter == (overflows - 1)) {
				noInterrupts();
				OCR1A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
				interrupts();

				overflowCounter++;
			} else {
				noInterrupts();
				OCR1A = MAX_OVERFLOW_COUNTER;
				interrupts();

				runCycleFlag = true;
				overflowCounter = 0;
			}
		}
	'''

	override protected initBody() '''
		// initialize Timer1
		noInterrupts();
		TCCR1A = 0;     // set entire TCCR1A register to 0
		TCCR1B = 0;     // same for TCCR1B
		
		// set compare match register to desired timer count:
		// period in ms, Arduino runs at 16 MHz, prescaler at 1024
		overflows = this->period / MAX_PERIOD;
		moduloRest = this->period % MAX_PERIOD;
		
		if (overflows < 1) {
			OCR1A = (this->period * 0.001f * (16000000 / 1024)) - 1;
		} else {
			OCR1A = MAX_OVERFLOW_COUNTER;
		}
		
		// turn on CTC mode
		TCCR1B |= (1 << WGM12);
		
		// Set CS10 and CS12 bits for 1024 prescaler
		TCCR1B |= (1 << CS10);
		TCCR1B |= (1 << CS12);
		
		// enable timer compare interrupt
		TIMSK1 |= (1 << OCIE1A);
		
		// enable global interrupts
		interrupts();
	'''
	
	override protected runCycleBody() '''
		if (runCycleFlag) {
			«super.runCycleBody()»
			runCycleFlag = false;
		}
		this->sleep();
	'''

	protected def sleep() '''
		void «timerName»::sleep() {
			set_sleep_mode(SLEEP_MODE_IDLE);
			noInterrupts();
			sleep_enable();
			interrupts();
			sleep_cpu();
			sleep_disable();
		}
	'''

	override protected cancelBody() '''
		TCCR1B = 0; // turn off the timer
	'''

}
