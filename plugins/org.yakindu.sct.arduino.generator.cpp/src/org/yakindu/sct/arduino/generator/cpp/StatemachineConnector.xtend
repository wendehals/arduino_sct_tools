/**
 * O * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.yakindu.sct.generator.c.IContentTemplate
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.generator.c.IGenArtifactConfigurations

class StatemachineConnector implements IContentTemplate {

	@Inject extension ArduinoCPPNaming
	@Inject extension ArduinoGenmodelEntries

	override content(ExecutionFlow it, GeneratorEntry entry, IGenArtifactConfigurations locations) '''
		«entry.licenseText»
		
		#include "«module.connector.h»"
		«IF entry.timer.architecture.kind.equals("atmega")»
		// #include <avr/power.h>
		«ENDIF»
		
		«module.connector»::«module.connector»(«module»* statemachine) {
			this->statemachine = statemachine;
		}
		
		/*
		 * Initialize the hardware.
		 */
		void «module.connector»::init() {
			// pinMode(LED_BUILTIN, OUTPUT);
		
			// The state machine has already been initialized and started before
			// this method is called. Until syncState() is called the first time
			// by the state machine, the hardware is not in sync with the state
			// machine. If the cycle period is very high (let's say >> 1s), it
			// might be better to call syncState() once manually, to get in sync
			// with the initial state of the state machine.
			// syncState();
		}
		
		/*
		 * Raise state machine events before processing them in the state machine's runCycle().
		 */
		void «module.connector»::raiseEvents() {
			// e.g.
			// if (buttonPressed) {
			//     statemachine->raiseXYZEvent();
			// }
		}
		
		/*
		 * Update the hardware depending on the state machine's state.
		 */
		void «module.connector»::syncState() {
			// digitalWrite(LED_BUILTIN, statemachine->get_XYZ());
		}
		
		/*
		 * Optimize power consumption by turning off hardware modules that are not needed.
		 */
		uint8_t «module.connector»::prepareSleepMode() {
			«IF entry.timer.architecture.kind.equals("atmega")»
			// Some of the functions of <avr/power.h> may not be supported by the
			// actual microprocessor you are using.
			// This method is only called in case you are using an AVR hardware timer.
			// e.g.
			// power_adc_disable();
			// power_spi_disable();
			// power_timer0_disable();
			// power_timer1_disable() ;
			// power_timer2_disable() ;
			// power_timer3_disable() ;
			// power_twi_disable();
			// power_usart0_disable();
			// power_usb_disable();
			
			return SLEEP_MODE_IDLE;
			«ELSE»
			return 0;
			«ENDIF»
		}
	'''

}
