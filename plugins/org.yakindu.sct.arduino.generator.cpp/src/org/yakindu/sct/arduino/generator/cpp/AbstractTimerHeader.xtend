package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgraph.Statechart
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sgen.GeneratorEntry

class AbstractTimerHeader {

	@Inject
	extension Naming

	def generateAbstractTimerHeader(ExecutionFlow flow, Statechart sc, IFileSystemAccess fsa, GeneratorEntry entry) {
		fsa.generateFile(abstractTimer.h, flow.generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
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
