/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

import com.google.inject.Inject
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.arduino.generator.cpp.NamingExtension

class SoftwareTimerCounter implements AbstractTimerCodeGenerator {

	@Inject extension NamingExtension

	override timerName(){
		"SoftwareTimerCounter"
	}

	override generateTimerHeader(ExecutionFlow it) '''
		«header»
		
		#ifndef «timerName.h.define»
		#define «timerName.h.define»
		
		#include <Arduino.h>
		#include "«abstractTimer.h»"
		
		class «timerName»: public «abstractTimer» {
		public:
			/* period in milliseconds */
			«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
					unsigned short maxParallelTimeEvents, unsigned int period);
		
			inline virtual ~«timerName»();

			virtual void runCycle();
		
		protected:
			virtual void init();
		
			virtual void sleep();
		
			virtual void stop();

		private:
			unsigned long lastCycle;
		};
		
		«timerName»::~«timerName»() {
		}
		
		#endif /* «timerName.h.define» */
	'''

	override generateTimer(ExecutionFlow it) '''
		«header»
		
		#include "«timerName.h»"
		
		extern bool runCycleFlag;
		
		«timerName»::«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
				unsigned short maxParallelTimeEvents, unsigned int period) :
				«abstractTimer»(statemachine, hardware, maxParallelTimeEvents, period) {
			lastCycle = 0;
		}
		
		void «timerName»::init() {
			lastCycle = millis();
		}

		void «timerName»::runCycle() {
			unsigned long current = millis();
			if (current>=lastCycle+this->period){
				runCycleFlag = true;
				«abstractTimer»::runCycle();
				lastCycle = current;
			}
		}
		
		void «timerName»::sleep() {
		}
		
		void «timerName»::stop() {
		}
	'''
	
}
