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
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.arduino.generator.cpp.features.IArduinoFeatureConstants

class StatemachineConnector {

	@Inject extension Naming
	@Inject extension GenmodelEntriesExtension

	def generateStatemachineConnector(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa) {
		if (getUserSrcFolder(entry) != null) {
			fsa.generateFile(flow.module.connector.cpp, IArduinoFeatureConstants::PARAM_USER_SRC_FOLDER, flow.generateContents(entry))
		} else {
			fsa.generateFile(flow.module.connector.cpp, flow.generateContents(entry))
		}
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
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
