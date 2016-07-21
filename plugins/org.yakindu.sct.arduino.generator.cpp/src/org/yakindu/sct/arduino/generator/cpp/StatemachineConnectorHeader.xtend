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
import org.yakindu.sct.arduino.generator.cpp.features.IArduinoFeatureConstants
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry

class StatemachineConnectorHeader {

	@Inject extension Naming
	@Inject extension GenmodelEntriesExtension

	def generateStatemachineConnectorHeader(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa) {
		if (getUserSrcFolder(entry) != null) {
			fsa.generateFile(flow.module.connector.h, IArduinoFeatureConstants::PARAM_USER_SRC_FOLDER,
				flow.generateContents(entry))
		} else {
			fsa.generateFile(flow.module.connector.h, flow.generateContents(entry))
		}
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		«header»
		«entry.licenseText»
		
		#ifndef «module.connector.h.define»
		#define «module.connector.h.define»
		
		#include <Arduino.h>
		#include "«entry.srcGenFolderRelativeToUserSrc»«hardwareConnector.h»"
		#include "«entry.srcGenFolderRelativeToUserSrc»«module.h»"
		
		class «module.connector»: public «hardwareConnector» {
		public:
			«module.connector»(«module»* statemachine);
		
			inline virtual ~«module.connector»();
		
			virtual void init();
		
			virtual void runCycle();
		
		private:
			«module»* statemachine;
		};
		
		«module.connector»::~«module.connector»() {
		}
		
		#endif /* «module.connector.h.define» */
	'''

}
