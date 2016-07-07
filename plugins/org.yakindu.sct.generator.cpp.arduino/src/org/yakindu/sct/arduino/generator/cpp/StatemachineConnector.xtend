package org.yakindu.sct.arduino.generator.cpp

import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgraph.Statechart
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sgen.GeneratorEntry
import com.google.inject.Inject

class StatemachineConnector {

	@Inject
	extension Naming

	def generateStatemachineConnector(ExecutionFlow flow, Statechart sc, IFileSystemAccess fsa, GeneratorEntry entry) {
		fsa.generateFile(flow.module.connector.cpp, flow.generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		#include "«module.connector.h»"
		
		«module.connector»::«module.connector»(«module»* statemachine) {
			this->statemachine = statemachine;
		}
		
		void «module.connector»::init() {
		}
		
		void «module.connector»::runCycle() {
		}
	'''

}
