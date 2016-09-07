/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

class ATmega328P_Timer2 extends AbstractAVRTimer {

	override timerName() {
		"ATmega328P_Timer2"
	}

	override protected ISR() '''
		ISR(TIMER2_COMPA_vect) {
			«IF useOverflows»
			overflowCounter++;
			
			if (overflowCounter == overflows && moduloRest != 0) {
				noInterrupts();
				OCR2A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
				interrupts();
			} else {
				noInterrupts();
				OCR2A = MAX_OVERFLOW_COUNTER;
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
		TCCR2A = 0;     // set entire TCCR2A register to 0
		TCCR2B = 0;     // same for TCCR2B
		
		«IF useOverflows»
		overflows = this->period / MAX_PERIOD;
		moduloRest = this->period % MAX_PERIOD;
		
		OCR2A = MAX_OVERFLOW_COUNTER;
		«ELSE»
		// set compare match register to desired timer count
		// period in ms, Arduino runs at 16 MHz, prescaler at 1024
		OCR2A = (this->period * 0.001f * (16000000 / 1024)) - 1;
		«ENDIF»
		
		// turn on CTC mode
		TCCR2B |= (1 << WGM22);
		
		// Set CS20, CS21, and CS22 bits for 1024 prescaler
		TCCR2B |= (1 << CS20);
		TCCR2B |= (1 << CS21);
		TCCR2B |= (1 << CS22);
		
		// enable timer compare interrupt
		TIMSK2 |= (1 << OCIE2A);
		
		// enable global interrupts
		interrupts();
	'''

	override protected cancelBody() '''
		TCCR2B = 0; // turn off the timer
	'''
	
	override protected maxPeriod() {
		16
	}
	
	override protected maxOverflowCounter() {
		249
	}
	
}
