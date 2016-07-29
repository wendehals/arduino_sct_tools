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

class TimeEventHeader {

	@Inject extension NamingExtension

	def generateTimeEventHeader(ExecutionFlow flow, IFileSystemAccess fsa) {
		fsa.generateFile(timeEvent.h, flow.generateContents())
	}

	def private generateContents(ExecutionFlow it) '''
		«header»
		
		#ifndef «timeEvent.h.define»
		#define «timeEvent.h.define»
		
		#include "«timedStatemachineInterface.h»"
		
		class «timeEvent» {
		
		public:
			«timedStatemachineInterface» *timedStatemachine;
			sc_eventid eventId;
			sc_boolean periodic;
			unsigned int overflows;
			unsigned int overflowCounter;
			bool eventRaised;
		};
		
		#endif /* «timeEvent.h.define» */
	'''

}
