package org.yakindu.sct.arduino.generator.cpp

import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sgraph.Statechart
import com.google.inject.Inject

class HardwareConnectorHeader {
	
	@Inject
	extension Naming

	def generateHardwareConnectorHeader(ExecutionFlow flow, Statechart sc, IFileSystemAccess fsa, GeneratorEntry entry) {
		fsa.generateFile(hardwareConnector.h, flow.generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		#ifndef «hardwareConnector.h.define»_H_
		#define «hardwareConnector.h.define»_H_
		
		
		class «hardwareConnector» {
		public:
			inline virtual ~«hardwareConnector»();
		
			virtual void init() = 0;
		
			virtual void runCycle() = 0;
		};
		
		«hardwareConnector»::~«hardwareConnector»() {
		}
		
		#endif /* «hardwareConnector.h.define»_H_ */
	'''
	
}