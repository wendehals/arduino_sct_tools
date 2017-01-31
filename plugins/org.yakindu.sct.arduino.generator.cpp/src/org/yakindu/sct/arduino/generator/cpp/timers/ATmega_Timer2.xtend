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

class ATmega_Timer2 extends AbstractATmega8BitTimer {

	override timerName() {
		"ATmega_Timer2"
	}

	override protected ISR(GeneratorEntry it) '''
		ISR(TIMER2_COMPA_vect) {
			«IF useOverflows»
				overflowCounter++;
				
				if (overflowCounter == overflows && moduloRest != 0) {
					noInterrupts();
					OCR2A = (moduloRest * 0.001f * (16000000 / 1024)) - 1;
					interrupts();
				} else if (overflowCounter >= overflows) {
					noInterrupts();
					OCR2A = OVERFLOW_COMPARE_VALUE;
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
		// initialize Timer2
		noInterrupts();
		TCCR2A = 0;     // set entire TCCR2A register to 0
		TCCR2B = 0;     // same for TCCR2B
		
		«IF useOverflows»
			overflows = CYCLE_PERIOD / MAX_PERIOD;
			moduloRest = CYCLE_PERIOD % MAX_PERIOD;
			
			OCR2A = OVERFLOW_COMPARE_VALUE;
		«ELSE»
			// set compare match register to desired timer count
			// period in ms, Arduino runs at 16 MHz, prescaler at 1024
			OCR2A = (CYCLE_PERIOD * 0.001f * (16000000 / 1024)) - 1;
		«ENDIF»
		
		// turn on CTC mode
		TCCR2B |= (1 << WGM22);
		
		// Set CS22, CS21, and CS20 bits for 1024 prescaler
		TCCR2B |= (1 << CS22);
		TCCR2B |= (1 << CS21);
		TCCR2B |= (1 << CS20);
		
		// enable timer compare interrupt
		TIMSK2 |= (1 << OCIE2A);
		
		// enable global interrupts
		interrupts();
	'''

	override protected sleepBody(GeneratorEntry it) '''
		uint8_t sleepMode = hardware->prepareSleepMode();

		switch (sleepMode) {
		case SLEEP_MODE_IDLE:
		case SLEEP_MODE_ADC:
			set_sleep_mode(SLEEP_MODE_IDLE);
			break;
		case SLEEP_MODE_PWR_SAVE:
		case SLEEP_MODE_PWR_DOWN:
		case SLEEP_MODE_STANDBY:
		case SLEEP_MODE_EXT_STANDBY:
			set_sleep_mode(SLEEP_MODE_PWR_SAVE);
			break;
		}

		noInterrupts();
		sleep_enable();
		interrupts();

		if (sleepMode == SLEEP_MODE_PWR_DOWN || sleepMode == SLEEP_MODE_PWR_SAVE
				|| sleepMode == SLEEP_MODE_STANDBY
				|| sleepMode == SLEEP_MODE_EXT_STANDBY) {
			sleep_bod_disable();
		}

		sleep_cpu();
		sleep_disable();
	'''

	override protected cancelBody(GeneratorEntry it) '''
		TCCR2B = 0; // turn off the timer
	'''

}
