package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow

class TimeEventHeader {

	@Inject extension Naming

	def generateTimeEventHeader(ExecutionFlow flow, IFileSystemAccess fsa) {
		fsa.generateFile(timeEvent.h, flow.generateContents())
	}

	def private generateContents(ExecutionFlow it) '''
		«header»
		
		#ifndef «timeEvent.h.define»_H_
		#define «timeEvent.h.define»_H_
		
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
		
		#endif /* «timeEvent.h.define»_H_ */
	'''

}
