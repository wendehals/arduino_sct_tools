/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.yakindu.sct.generator.c.IContentTemplate
import org.yakindu.sct.generator.c.IGenArtifactConfigurations
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry

class StatemachineConnectorHeader implements IContentTemplate {

	@Inject extension Naming
	@Inject extension GenmodelEntries

	override content(ExecutionFlow it, GeneratorEntry entry, IGenArtifactConfigurations locations) '''
		«entry.licenseText»
		
		#ifndef «module.connector.h.define»
		#define «module.connector.h.define»
		
		#include <Arduino.h>
		#include "«entry.srcGenFolderRelativeToUserSrc»«hardwareConnector.h»"
		#include "«entry.srcGenFolderRelativeToUserSrc»«module.h»"
		
		class «module.connector»: public «hardwareConnector» {
		public:
			«module.connector»(«module»* statemachine);
		
			inline ~«module.connector»();
		
			/*
			 * Initialize the hardware.
			 */
			void init();
		
			/*
			 * Raise state machine events before processing them in the state machine's runCycle().
			 */
			void raiseEvents();
		
			/*
			 * Update the hardware depending on the state machine's state.
			 */
			void syncState();
		
			/*
			 * Optimize power consumption by turning off modules that are not needed.
			 * Return one of the following sleep states:
			 * SLEEP_MODE_IDLE, SLEEP_MODE_ADC, SLEEP_MODE_PWR_DOWN, SLEEP_MODE_PWR_SAVE,
			 * SLEEP_MODE_STANDBY, SLEEP_MODE_EXT_STANDBY
			 * The returned sleep mode is just a recommendation, the actual timer implementation
			 * may not support the given sleep mode.
			 */
			uint8_t prepareSleepMode();
			
		private:
			«module»* statemachine;
		};
			
		«module.connector»::~«module.connector»() {
		}
		
		#endif /* «module.connector.h.define» */
	'''

}
