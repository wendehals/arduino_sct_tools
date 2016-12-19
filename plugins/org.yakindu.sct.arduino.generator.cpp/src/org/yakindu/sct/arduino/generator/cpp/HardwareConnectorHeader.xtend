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

class HardwareConnectorHeader implements IContentTemplate {

	@Inject extension Naming
	@Inject extension GenmodelEntries

	override content(ExecutionFlow flow, GeneratorEntry it, IGenArtifactConfigurations locations) '''
		«licenseText»
		
		#ifndef «hardwareConnector.h.define»
		#define «hardwareConnector.h.define»
		
		
		class «hardwareConnector» {
		public:
			inline virtual ~«hardwareConnector»();
		
			/*
			 * Initialize the hardware.
			 */
			virtual void init() = 0;
		
			/*
			 * Raise state machine events before processing them in the state machine's runCycle().
			 */
			virtual void raiseEvents() = 0;
		
			/*
			 * Update the hardware depending on the state machine's state.
			 */
			virtual void syncState() = 0;
		
			/*
			 * Optimize power consumption by turning off hardware modules that are not needed.
			 * Return one of the following sleep states:
			 * SLEEP_MODE_IDLE, SLEEP_MODE_ADC, SLEEP_MODE_PWR_DOWN, SLEEP_MODE_PWR_SAVE,
			 * SLEEP_MODE_STANDBY, SLEEP_MODE_EXT_STANDBY
			 * The returned sleep mode is just a recommendation, the actual timer implementation
			 * may not support the given sleep mode.
			 */
			virtual uint8_t prepareSleepMode() = 0;
		};
		
		«hardwareConnector»::~«hardwareConnector»() {
		}
		
		#endif /* «hardwareConnector.h.define» */
	'''

}
