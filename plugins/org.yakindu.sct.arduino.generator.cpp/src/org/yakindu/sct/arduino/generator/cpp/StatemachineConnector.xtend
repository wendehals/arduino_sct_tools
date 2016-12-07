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

	@Inject extension Naming
	@Inject extension GenmodelEntries

	override content(ExecutionFlow it, GeneratorEntry entry, IGenArtifactConfigurations locations) '''
		«entry.licenseText»
		
		#include "«module.connector.h»"
		// #include <avr/power.h>
		
		«module.connector»::«module.connector»(«module»* statemachine) {
			this->statemachine = statemachine;
		}
		
		void «module.connector»::prepareSleepMode() {
			// Put your code here to optimize power consumption by turning off
			// microprocessor modules that you don't need. Also, some of the
			// functions of <avr/power.h> may not be supported by the actual
			// microprocessor you are using.
			// This method is only called in case you are using a hardware timer.
			// e.g.
			//	power_adc_disable();
			//	power_spi_disable();
			//	power_timer0_disable();
			//	power_timer1_disable() ;
			//	power_timer2_disable() ;
			//	power_timer3_disable() ;
			//	power_twi_disable();
			//	power_usart0_disable();
			// power_usb_disable();
		}
		
		void «module.connector»::init() {
			// Put your code here to initialize the hardware.
			// pinMode(LED_BUILTIN, OUTPUT);
		
			// The state machine has already been initialized and started before
			// this method is called. Until runCycle() is called the first time
			// by the state machine, the hardware is not in sync with the state
			// machine. If the cycle period is very high (let's say >> 1s), it
			// might be better to call runCycle() once manually, to get in sync
			// with the initial state of the state machine.
			// runCycle();
		}
		
		void «module.connector»::runCycle() {
			// Put your code here to update the hardware depending on the state machine's state.
			// digitalWrite(LED_BUILTIN, statemachine->getXXX());
		}
	'''

}
