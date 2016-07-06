package org.yakindu.sct.generator.cpp.arduino

import com.google.inject.Inject
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgraph.Statechart
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sgen.GeneratorEntry

class Main {

	@Inject
	extension Naming

	def generateMain(ExecutionFlow flow, Statechart sc, IFileSystemAccess fsa, GeneratorEntry entry) {
		fsa.generateFile(main.cpp, flow.generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		#include <Arduino.h>
		#include "src-gen/«module.h»"
		#include "src-gen/«atMega168_328Timer.h»"
		#include "src/«module.connector.h»"
		
		#define PERIOD 10
		#define MAX_PARALLEL_TIMERS 2
		
		«module»* statemachine;
		«module.connector»* connector;
		«atMega168_328Timer»* timer;
		
		void setup() {
			statemachine = new «module»();
			connector = new «module.connector»(statemachine);
			timer = new «atMega168_328Timer»(statemachine, connector, MAX_PARALLEL_TIMERS, PERIOD);
		
			statemachine->setTimer(timer);
			timer->start();
		}
		
		void loop() {
			timer->runCycle();
		}
	'''

}
