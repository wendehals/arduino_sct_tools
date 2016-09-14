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
		
			virtual void init() = 0;
		
			virtual void runCycle() = 0;
		};
		
		«hardwareConnector»::~«hardwareConnector»() {
		}
		
		#endif /* «hardwareConnector.h.define» */
	'''

}
