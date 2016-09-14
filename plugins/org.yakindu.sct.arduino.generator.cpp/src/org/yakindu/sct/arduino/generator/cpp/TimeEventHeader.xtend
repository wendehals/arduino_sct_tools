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

class TimeEventHeader implements IContentTemplate {

	@Inject extension Naming
	@Inject extension GenmodelEntries

	override content(ExecutionFlow it, GeneratorEntry entry, IGenArtifactConfigurations locations) '''
		«entry.licenseText»
		
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
