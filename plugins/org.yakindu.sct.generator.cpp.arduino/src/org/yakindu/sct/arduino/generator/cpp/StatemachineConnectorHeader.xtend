package org.yakindu.sct.arduino.generator.cpp

import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgraph.Statechart
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sgen.GeneratorEntry
import com.google.inject.Inject

class StatemachineConnectorHeader {

	@Inject
	extension Naming

	def generateStatemachineConnectorHeader(ExecutionFlow flow, Statechart sc, IFileSystemAccess fsa,
		GeneratorEntry entry) {
		fsa.generateFile(flow.module.connector.h, flow.generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		#ifndef «module.connector.define»_H_
		#define «module.connector.define»_H_
		
		#include "../src-gen/«abstractTimer.h»"
		#include "../src-gen/«module.h»"
		#include "../src-gen/«atMega168_328Timer.h»"
		
		class «module.connector»: public «hardwareConnector» {
		public:
			«module.connector»(«module»* statemachine);
		
			inline virtual ~«module.connector»();
		
			virtual void init();
		
			virtual void runCycle();
		
		private:
			«module»* statemachine;
		};
		
		«module.connector»::~«module.connector»() {
		}
		#endif /* «module.connector.define»_H_ */
	'''

}
