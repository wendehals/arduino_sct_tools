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

class ATmega168_328TimerHeader {

	@Inject extension Naming

	def generateATmega168_328TimerHeader(ExecutionFlow flow, IFileSystemAccess fsa) {
		fsa.generateFile(atMega168_328Timer.h, flow.generateContents())
	}

	def private generateContents(ExecutionFlow it) '''
		«header»
		
		#ifndef «atMega168_328Timer.h.define»
		#define «atMega168_328Timer.h.define»
		
		#include <Arduino.h>
		#include <avr/sleep.h>
		#include "«abstractTimer.h»"
		
		class «atMega168_328Timer»: public «abstractTimer» {
		public:
			/* period in milliseconds */
			«atMega168_328Timer»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
					unsigned short maxParallelTimeEvents, unsigned int period);
		
			inline virtual ~«atMega168_328Timer»();
		
		protected:
			virtual void init();
		
			virtual void sleep();
		
			virtual void stop();
		};
		
		«atMega168_328Timer»::~«atMega168_328Timer»() {
		}
		
		#endif /* «atMega168_328Timer.h.define» */
	'''

}
