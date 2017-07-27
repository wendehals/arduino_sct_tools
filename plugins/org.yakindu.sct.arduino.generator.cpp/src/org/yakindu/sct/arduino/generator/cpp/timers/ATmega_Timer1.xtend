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

class ATmega_Timer1 extends AbstractAVR16BitTimer {

	override timerName() {
		"ATmega_Timer1"
	}

	override protected ISR(GeneratorEntry it) '''
		ISR(TIMER1_COMPA_vect) {
			«IF useOverflows»
				overflowCounter++;
				
				if (overflowCounter == overflows && moduloRest != 0) {
					noInterrupts();
					OCR1A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
					interrupts();
				} else if (overflowCounter >= overflows) {
					noInterrupts();
					OCR1A = OVERFLOW_COMPARE_VALUE;
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
		// initialize Timer1
		noInterrupts();
		TCCR1A = 0;     // set entire TCCR1A register to 0
		TCCR1B = 0;     // same for TCCR1B
		
		«IF useOverflows»
			overflows = CYCLE_PERIOD / MAX_PERIOD;
			moduloRest = CYCLE_PERIOD % MAX_PERIOD;
			
			OCR1A = OVERFLOW_COMPARE_VALUE;
		«ELSE»
			// set compare match register to desired timer count
			// period in ms, Arduino runs at 16 MHz, prescaler at 1024
			OCR1A = (CYCLE_PERIOD * 0.001f * (16000000 / 1024)) - 1;
		«ENDIF»
		
		// turn on CTC mode
		TCCR1B |= (1 << WGM12);
		
		// Set CS12 and CS10 bits for 1024 prescaler
		TCCR1B |= (1 << CS12);
		TCCR1B |= (1 << CS10);
		
		// enable timer compare interrupt
		TIMSK1 |= (1 << OCIE1A);
		
		// enable global interrupts
		interrupts();
	'''

	override protected cancelBody(GeneratorEntry it) '''
		TCCR1B = 0; // turn off the timer
	'''

}
