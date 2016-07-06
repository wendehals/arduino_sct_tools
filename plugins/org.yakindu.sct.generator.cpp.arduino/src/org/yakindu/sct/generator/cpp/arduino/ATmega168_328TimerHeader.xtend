package org.yakindu.sct.generator.cpp.arduino

import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sgraph.Statechart
import com.google.inject.Inject

class ATmega168_328TimerHeader {

	@Inject
	extension Naming

	def generateATmega168_328TimerHeader(ExecutionFlow flow, Statechart sc, IFileSystemAccess fsa, GeneratorEntry entry) {
		fsa.generateFile(atMega168_328Timer.h, flow.generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		#ifndef «atMega168_328Timer.h.define»_H_
		#define «atMega168_328Timer.h.define»_H_
		
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
		
		#endif /* «atMega168_328Timer.h.define»_H_ */
	'''
	
}