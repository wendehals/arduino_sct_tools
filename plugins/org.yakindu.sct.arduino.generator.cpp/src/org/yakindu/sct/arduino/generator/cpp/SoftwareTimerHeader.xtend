/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp

import org.yakindu.sct.model.sexec.ExecutionFlow
import org.eclipse.xtext.generator.IFileSystemAccess
import com.google.inject.Inject

class SoftwareTimerHeader {

	@Inject extension Naming

	def generateSoftwareTimerHeader(ExecutionFlow flow, IFileSystemAccess fsa) {
		fsa.generateFile(softwareTimer.h, flow.generateContents())
	}

	def private generateContents(ExecutionFlow it) '''
		«header»
		
		#ifndef «softwareTimer.h.define»_H_
		#define «softwareTimer.h.define»_H_
		
		#include <Arduino.h>
		#include "«abstractTimer.h»"
		#include "«hardwareConnector.h»"
		
		class «softwareTimer»: public «abstractTimer» {
		public:
			/* period in milliseconds */
			«softwareTimer»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
					unsigned short maxParallelTimeEvents, unsigned int period);
		
			inline virtual ~«softwareTimer»();

			virtual void runCycle();
		
		protected:
			virtual void init();
		
			virtual void sleep();
		
			virtual void stop();

		private:
			unsigned long lastCycle;
		};
		
		«softwareTimer»::~«softwareTimer»() {
		}
		
		#endif /* «softwareTimer.h.define»_H_ */
	'''

}
