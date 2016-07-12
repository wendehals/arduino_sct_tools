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
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.eclipse.xtext.generator.IFileSystemAccess

class SoftwareTimer {

	@Inject extension Naming

	def generateSoftwareTimer(ExecutionFlow flow, IFileSystemAccess fsa) {
		fsa.generateFile(softwareTimer.cpp, flow.generateContents())
	}

	def private generateContents(ExecutionFlow it) '''
		«header»
		
		#include "«softwareTimer.h»"
		
		extern bool runCycleFlag;
		
		«softwareTimer»::«softwareTimer»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
				unsigned short maxParallelTimeEvents, unsigned int period) :
				«abstractTimer»(statemachine, hardware, maxParallelTimeEvents, period) {
			lastCycle = 0;
		}
		
		void «softwareTimer»::init() {
			lastCycle = millis();
		}

		void «softwareTimer»::runCycle() {
			unsigned long current = millis();
			if (current>=lastCycle+this->period){
				runCycleFlag = true;
				«abstractTimer»::runCycle();
				lastCycle = current;
			}
		}
		
		void «softwareTimer»::sleep() {
		}
		
		void «softwareTimer»::stop() {
		}
	'''
	
}