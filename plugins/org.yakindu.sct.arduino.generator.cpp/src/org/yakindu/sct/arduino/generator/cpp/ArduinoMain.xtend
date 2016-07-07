package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.arduino.generator.cpp.features.GenmodelEntriesExtension
import org.yakindu.sct.model.sgen.GeneratorEntry

class ArduinoMain {

	@Inject extension Naming
	@Inject extension GenmodelEntriesExtension

	def generateMain(ExecutionFlow it, GeneratorEntry entry, IFileSystemAccess fsa) {
		fsa.generateFile(main.cpp, generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		«header»
		
		#include <Arduino.h>
		#include "src-gen/«module.h»"
		#include "src-gen/«IF isSoftwareTimer(entry)»«softwareTimer.h»«ELSE»«atMega168_328Timer.h»«ENDIF»"
		#include "src/«module.connector.h»"
		
		#define PERIOD 10
		#define MAX_PARALLEL_TIMERS 2
		
		«module»* statemachine;
		«module.connector»* connector;
		«IF isSoftwareTimer(entry)»«softwareTimer»«ELSE»«atMega168_328Timer»«ENDIF»* timer;
		
		void setup() {
			statemachine = new «module»();
			connector = new «module.connector»(statemachine);
			timer = new «IF isSoftwareTimer(entry)»«softwareTimer»«ELSE»«atMega168_328Timer»«ENDIF»(statemachine, connector, MAX_PARALLEL_TIMERS, PERIOD);
		
			statemachine->setTimer(timer);
			timer->start();
		}
		
		void loop() {
			timer->runCycle();
		}
	'''

}
