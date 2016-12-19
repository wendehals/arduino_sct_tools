/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

import org.yakindu.sct.model.sgen.GeneratorEntry

class ATmega_Timer5 extends AbstractATmega16BitTimer {

	override timerName() {
		"ATmega_Timer5"
	}

	override protected ISR(GeneratorEntry it) '''
		ISR(TIMER5_COMPA_vect) {
			«IF useOverflows»
				overflowCounter++;
				
				if (overflowCounter == overflows && moduloRest != 0) {
					noInterrupts();
					OCR5A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
					interrupts();
				} else if (overflowCounter >= overflows) {
					noInterrupts();
					OCR5A = OVERFLOW_COMPARE_VALUE;
					interrupts();
				
					runCycleFlag = true;
					overflowCounter = 0;
				}
			«ELSE»
				runCycleFlag = true;
			«ENDIF»
		}
	'''

	override protected initBody(GeneratorEntry it) '''
		// initialize Timer5
		noInterrupts();
		TCCR5A = 0;     // set entire TCCR5A register to 0
		TCCR5B = 0;     // same for TCCR5B
		
		«IF useOverflows»
			overflows = period / MAX_PERIOD;
			moduloRest = period % MAX_PERIOD;
			
			OCR5A = OVERFLOW_COMPARE_VALUE;
		«ELSE»
			// set compare match register to desired timer count
			// period in ms, Arduino runs at 16 MHz, prescaler at 1024
			OCR5A = (period * 0.001f * (16000000 / 1024)) - 1;
		«ENDIF»
		
		// turn on CTC mode
		TCCR5B |= (1 << WGM52);
		
		// Set CS52 and CS50 bits for 1024 prescaler
		TCCR5B |= (1 << CS52);
		TCCR5B |= (1 << CS50);
		
		// enable timer compare interrupt
		TIMSK5 |= (1 << OCIE5A);
		
		// enable global interrupts
		interrupts();
	'''

	override protected cancelBody(GeneratorEntry it) '''
		TCCR5B = 0; // turn off the timer
	'''

}
