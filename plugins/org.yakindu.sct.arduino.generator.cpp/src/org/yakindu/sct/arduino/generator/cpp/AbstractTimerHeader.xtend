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

class AbstractTimerHeader {

	@Inject extension Naming

	def generateAbstractTimerHeader(ExecutionFlow flow, IFileSystemAccess fsa) {
		fsa.generateFile(abstractTimer.h, flow.generateContents())
	}

	def private generateContents(ExecutionFlow it) '''
		«header»
		
		#ifndef «abstractTimer.h.define»_H_
		#define «abstractTimer.h.define»_H_
		
		#include <stdio.h>
		#include "«typesModule.h»"
		#include "«timerInterface.h»"
		#include "«statemachineInterface.h»"
		#include "«timedStatemachineInterface.h»"
		#include "«timeEvent.h»"
		#include "«hardwareConnector.h»"
		
		class «abstractTimer»: public «timerInterface» {
		
		public:
			/* period in milliseconds */
			«abstractTimer»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
					unsigned short maxParallelTimeEvents, unsigned int period);
		
			virtual ~«abstractTimer»();
		
			void start();
		
			void setTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId, sc_integer time,
					sc_boolean isPeriodic);
		
			void unsetTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId);
		
			void runCycle();
		
			void cancel();
		
		protected:
			«statemachineInterface»* statemachine;
		
			«hardwareConnector»* hardware;
		
			unsigned int period;
		
			virtual void init() = 0;
		
			virtual void sleep() = 0;
		
			virtual void stop() = 0;
		
		private:
			unsigned short maxParallelTimeEvents;
		
			«timeEvent»* events;
		
			void raiseTimeEvents();
		};
		
		#endif /* «abstractTimer.h.define»_H_ */
	'''

}
