/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers.avr

import org.yakindu.sct.model.sgen.GeneratorEntry

class ATmega_Timer4 extends AbstractAVR16BitTimer {

	override timerName() {
		"ATmega_Timer4"
	}

	override protected ISR(GeneratorEntry it) '''
		ISR(TIMER4_COMPA_vect) {
			«IF useOverflows»
				overflowCounter++;
				
				if (overflowCounter == overflows && moduloRest != 0) {
					noInterrupts();
					OCR4A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
					interrupts();
				} else if (overflowCounter >= overflows) {
					noInterrupts();
					OCR4A = OVERFLOW_COMPARE_VALUE;
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
		// initialize Timer4
		noInterrupts();
		TCCR4A = 0;     // set entire TCCR4A register to 0
		TCCR4B = 0;     // same for TCCR4B
		
		«IF useOverflows»
			overflows = CYCLE_PERIOD / MAX_PERIOD;
			moduloRest = CYCLE_PERIOD % MAX_PERIOD;
			
			OCR4A = OVERFLOW_COMPARE_VALUE;
		«ELSE»
			// set compare match register to desired timer count
			// period in ms, Arduino runs at 16 MHz, prescaler at 1024
			OCR4A = (CYCLE_PERIOD * 0.001f * (16000000 / 1024)) - 1;
		«ENDIF»
		
		// turn on CTC mode
		TCCR4B |= (1 << WGM42);
		
		// Set CS42 and CS40 bits for 1024 prescaler
		TCCR4B |= (1 << CS42);
		TCCR4B |= (1 << CS40);
		
		// enable timer compare interrupt
		TIMSK4 |= (1 << OCIE4A);
		
		// enable global interrupts
		interrupts();
	'''

	override protected cancelBody(GeneratorEntry it) '''
		TCCR4B = 0; // turn off the timer
	'''

}
