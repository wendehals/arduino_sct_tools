/**
O * Copyright (c) 2016 by Lothar Wendehals.
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
		
		«module.connector»::«module.connector»(«module»* statemachine) {
			this->statemachine = statemachine;
		}
		
		void «module.connector»::init() {
			// put your code here to initialize the hardware
			// pinMode(LED_BUILTIN, OUTPUT);
		}
		
		void «module.connector»::runCycle() {
			// put your code here to update the hardware depending on the statechart's state
			// digitalWrite(LED_BUILTIN, statemachine->getXXX());
		}
	'''

}
