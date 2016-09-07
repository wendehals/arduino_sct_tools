/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

class ATmega328P_Timer1 extends AbstractAVRTimer {
	
	override timerName() {
		"ATmega328P_Timer1"
	}

	override protected ISR() '''
		ISR(TIMER1_COMPA_vect) {
			«IF useOverflows»
			overflowCounter++;
			
			if (overflowCounter == overflows && moduloRest != 0) {
				noInterrupts();
				OCR1A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
				interrupts();
			} else {
				noInterrupts();
				OCR1A = MAX_OVERFLOW_COUNTER;
				interrupts();
			
				runCycleFlag = true;
				overflowCounter = 0;
			}
			«ELSE»
			runCycleFlag = true;
			«ENDIF»
		}
	'''

	override protected initBody() '''
		// initialize Timer1
		noInterrupts();
		TCCR1A = 0;     // set entire TCCR1A register to 0
		TCCR1B = 0;     // same for TCCR1B
		
		«IF useOverflows»
		overflows = this->period / MAX_PERIOD;
		moduloRest = this->period % MAX_PERIOD;
		
		OCR1A = MAX_OVERFLOW_COUNTER;
		«ELSE»
		// set compare match register to desired timer count
		// period in ms, Arduino runs at 16 MHz, prescaler at 1024
		OCR1A = (this->period * 0.001f * (16000000 / 1024)) - 1;
		«ENDIF»
		
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

	override protected cancelBody() '''
		TCCR1B = 0; // turn off the timer
	'''
	
	override protected maxPeriod() {
		4192
	}
	
	override protected maxOverflowCounter() {
		65499
	}

}
