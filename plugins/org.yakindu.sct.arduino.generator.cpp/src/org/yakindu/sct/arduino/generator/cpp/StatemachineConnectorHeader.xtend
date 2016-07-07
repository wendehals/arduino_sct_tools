package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.arduino.generator.cpp.features.GenmodelEntriesExtension
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry

class StatemachineConnectorHeader {

	@Inject extension Naming
	@Inject extension GenmodelEntriesExtension

	def generateStatemachineConnectorHeader(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa) {
		if (getUserSrcFolder(entry) != null) {
			fsa.generateFile(getUserSrcFolder(entry) + "/" + flow.module.connector.h, flow.generateContents(entry))
		} else {
			fsa.generateFile(flow.module.connector.h, flow.generateContents(entry))
		}
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		«header»
		
		«entry.licenseText»
		
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
