package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow

class HardwareConnectorHeader {

	@Inject	extension Naming

	def generateHardwareConnectorHeader(ExecutionFlow flow, IFileSystemAccess fsa) {
		fsa.generateFile(hardwareConnector.h, flow.generateContents())
	}

	def private generateContents(ExecutionFlow it) '''
		«header»
		
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
