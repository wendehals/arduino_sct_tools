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


			// The state machine has already been initialized and started before.
			// If the cycle period is very high (let's say >> 1s), it takes some
			// time until runCycle() is called the first time. During that time,
			// the hardware is not in sync with the state machine. So it might be
			// better to call runCycle() once manually, to get in sync with the
			// initial state of the state machine.

			// runCycle();
		}
		
		void «module.connector»::runCycle() {
			// put your code here to update the hardware depending on the state
			// machine's state

			// digitalWrite(LED_BUILTIN, statemachine->getXXX());
		}
	'''

}
