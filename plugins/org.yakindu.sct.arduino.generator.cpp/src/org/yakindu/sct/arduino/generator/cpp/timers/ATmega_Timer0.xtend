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

class ATmega_Timer0 extends AbstractAVR8BitTimer {

	override timerName() {
		"ATmega_Timer0"
	}

	override protected ISR(GeneratorEntry it) '''
		ISR(TIMER0_COMPA_vect) {
			«IF useOverflows»
				overflowCounter++;
				
				if (overflowCounter == overflows && moduloRest != 0) {
					noInterrupts();
					OCR0A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
					interrupts();
				} else if (overflowCounter >= overflows) {
					noInterrupts();
					OCR0A = OVERFLOW_COMPARE_VALUE;
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
		// initialize Timer0
		noInterrupts();
		TCCR0A = 0;     // set entire TCCR0A register to 0
		TCCR0B = 0;     // same for TCCR0B
		
		«IF useOverflows»
			overflows = CYCLE_PERIOD / MAX_PERIOD;
			moduloRest = CYCLE_PERIOD % MAX_PERIOD;
			
			OCR0A = OVERFLOW_COMPARE_VALUE;
		«ELSE»
			// set compare match register to desired timer count
			// period in ms, Arduino runs at 16 MHz, prescaler at 1024
			OCR0A = (CYCLE_PERIOD * 0.001f * (16000000 / 1024)) - 1;
		«ENDIF»
		
		// turn on CTC mode
		TCCR0A |= (1 << WGM01);
		
		// Set CS02 and CS00 bits for 1024 prescaler
		TCCR0B |= (1 << CS02);
		TCCR0B |= (1 << CS00);
		
		// enable timer compare interrupt
		TIMSK0 |= (1 << OCIE0A);
		
		// enable global interrupts
		interrupts();
	'''

	override protected cancelBody(GeneratorEntry it) '''
		TCCR0B = 0; // turn off the timer
	'''

}
