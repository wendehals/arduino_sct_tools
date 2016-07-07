package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.arduino.generator.cpp.features.GenmodelEntriesExtension
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry

class StatemachineConnector {

	@Inject extension Naming
	@Inject extension GenmodelEntriesExtension
//	@Inject extension IFileSystemAccess2

	def generateStatemachineConnector(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa) {
//		if (!main.cpp.isFile()) {
		if (getUserSrcFolder(entry) != null) {
			fsa.generateFile(getUserSrcFolder(entry) + "/" + flow.module.connector.cpp, flow.generateContents(entry))
		} else {
			fsa.generateFile(flow.module.connector.cpp, flow.generateContents(entry))
		}
//		}
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		«entry.licenseText»
		
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
