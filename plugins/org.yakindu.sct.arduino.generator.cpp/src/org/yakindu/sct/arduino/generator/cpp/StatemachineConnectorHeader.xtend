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
