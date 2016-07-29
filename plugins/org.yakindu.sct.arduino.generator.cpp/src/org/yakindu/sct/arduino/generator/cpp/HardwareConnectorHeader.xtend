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

class HardwareConnectorHeader {

	@Inject	extension NamingExtension

	def generateHardwareConnectorHeader(ExecutionFlow flow, IFileSystemAccess fsa) {
		fsa.generateFile(hardwareConnector.h, flow.generateContents())
	}

	def private generateContents(ExecutionFlow it) '''
		«header»
		
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
