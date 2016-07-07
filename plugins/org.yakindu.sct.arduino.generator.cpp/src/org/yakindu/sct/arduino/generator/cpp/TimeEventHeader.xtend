package org.yakindu.sct.arduino.generator.cpp

import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sgraph.Statechart
import com.google.inject.Inject

class TimeEventHeader {

	@Inject
	extension Naming

	def generateTimeEventHeader(ExecutionFlow flow, Statechart sc, IFileSystemAccess fsa, GeneratorEntry entry) {
		fsa.generateFile(timeEvent.h, flow.generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
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
