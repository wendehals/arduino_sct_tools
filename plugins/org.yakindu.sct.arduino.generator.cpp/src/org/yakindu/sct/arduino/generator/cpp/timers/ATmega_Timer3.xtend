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

class ATmega_Timer3 extends AbstractATmega16BitTimer {

	override timerName() {
		"ATmega_Timer3"
	}

	override protected ISR(GeneratorEntry it) '''
		ISR(TIMER3_COMPA_vect) {
			«IF useOverflows»
				overflowCounter++;
				
				if (overflowCounter == overflows && moduloRest != 0) {
					noInterrupts();
					OCR3A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
					interrupts();
				} else if (overflowCounter >= overflows) {
					noInterrupts();
					OCR3A = OVERFLOW_COMPARE_VALUE;
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
		// initialize Timer3
		noInterrupts();
		TCCR3A = 0;     // set entire TCCR3A register to 0
		TCCR3B = 0;     // same for TCCR3B
		
		«IF useOverflows»
			overflows = this->period / MAX_PERIOD;
			moduloRest = this->period % MAX_PERIOD;
			
			OCR3A = OVERFLOW_COMPARE_VALUE;
		«ELSE»
			// set compare match register to desired timer count
			// period in ms, Arduino runs at 16 MHz, prescaler at 1024
			OCR3A = (this->period * 0.001f * (16000000 / 1024)) - 1;
		«ENDIF»
		
		// turn on CTC mode
		TCCR3B |= (1 << WGM32);
		
		// Set CS32 and CS30 bits for 1024 prescaler
		TCCR3B |= (1 << CS32);
		TCCR3B |= (1 << CS30);
		
		// enable timer compare interrupt
		TIMSK3 |= (1 << OCIE3A);
		
		// enable global interrupts
		interrupts();
	'''

	override protected cancelBody(GeneratorEntry it) '''
		TCCR3B = 0; // turn off the timer
	'''

}
