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
import com.google.inject.Inject
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.arduino.generator.cpp.ArduinoGenmodelEntries

class ATmega_WDT extends AbstractAVRTimer {

	@Inject extension ArduinoGenmodelEntries

	override timerName() {
		"ATmega_WDT"
	}

	override protected variableDeclarations(GeneratorEntry it, ExecutionFlow flow) '''
		«super.variableDeclarations(it, flow)»
		
		bool runCycleFlag = false;
	'''

	override protected ISR(GeneratorEntry it) '''
		ISR(WDT_vect) {
			runCycleFlag = true;
		}
	'''

	override protected initBody(GeneratorEntry it) '''
		// disable global interrupts
		noInterrupts();
		
		// Clear the reset flag
		MCUSR &= ~(1 << WDRF);
		
		«IF cyclePeriod == 16»
			// enable watchdog interrupt with 16ms period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR |= (1 << WDIE);
		«ELSEIF cyclePeriod == 32»
			// enable watchdog interrupt with 32ms period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP0);
			WDTCSR |= (1 << WDIE);
		«ELSEIF cyclePeriod == 64»
			// enable watchdog interrupt with 64ms period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP1);
			WDTCSR |= (1 << WDIE);
		«ELSEIF cyclePeriod == 125»
			// enable watchdog interrupt with 125ms period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP1) | (1 << WDP0);
			WDTCSR |= (1 << WDIE);
		«ELSEIF cyclePeriod == 250»
			// enable watchdog interrupt with 250ms period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP2);
			WDTCSR |= (1 << WDIE);
		«ELSEIF cyclePeriod == 500»
			// enable watchdog interrupt with 500ms period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP2) | (1 << WDP0);
			WDTCSR |= (1 << WDIE);
		«ELSEIF cyclePeriod == 1000»
			// enable watchdog interrupt with 1s period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP2) | (1 << WDP1);
			WDTCSR |= (1 << WDIE);
		«ELSEIF cyclePeriod == 2000»
			// enable watchdog interrupt with 2s period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP2) | (1 << WDP1) | (1 << WDP0);
			WDTCSR |= (1 << WDIE);
		«ELSEIF cyclePeriod == 4000»
			// enable watchdog interrupt with 4s period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP3);
			WDTCSR |= (1 << WDIE);
		«ELSE»
			// enable watchdog interrupt with 8s period
			WDTCSR |= (1 << WDCE) | (1 << WDE);
			WDTCSR = (1 << WDP3) | (1 << WDP0);
			WDTCSR |= (1 << WDIE);
		«ENDIF»		
		
		// enable global interrupts
		interrupts();
	'''

	override def sleepBody(GeneratorEntry it) '''
		set_sleep_mode(hardware->prepareSleepMode());
		noInterrupts();
		sleep_enable();
		interrupts();
		sleep_cpu();
		sleep_disable();
	'''

	override protected maxPeriod() {
		8000
	}

}
