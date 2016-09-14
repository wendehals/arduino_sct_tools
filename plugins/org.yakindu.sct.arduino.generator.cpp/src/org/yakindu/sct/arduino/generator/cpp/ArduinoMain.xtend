/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.yakindu.sct.generator.c.IContentTemplate
import org.yakindu.sct.generator.c.IGenArtifactConfigurations
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sgraph.Statechart

class ArduinoMain implements IContentTemplate {

	@Inject extension Naming
	@Inject extension GenmodelEntries
	@Inject extension MaxParallelTimers

	override content(ExecutionFlow it, GeneratorEntry entry, IGenArtifactConfigurations locations) '''
		«entry.licenseText»
		
		#include "«arduinoMain.h»"
		#include "«entry.timerClassName.h»"
		#include "«entry.userSrcFolderRelativeToSrcGen»«module.connector.h»"
		
		#define CYCLE_PERIOD «cyclePeriod(entry)»
		#define MAX_PARALLEL_TIMERS «maxParallelTimers(it.sourceElement as Statechart)»
		
		«module»* statemachine;
		«module.connector»* connector;
		«entry.timerClassName»* timer;
		
		«module»* getStatemachine(){
			return statemachine;
		}
		
		void setup() {
			statemachine = new «module»();
			connector = new «module.connector»(statemachine);
			timer = new «entry.timerClassName»(statemachine, connector, MAX_PARALLEL_TIMERS, CYCLE_PERIOD);
		
			statemachine->setTimer(timer);
			timer->start();
		}
		
		void loop() {
			timer->runCycle();
		}
	'''

	def timerClassName(GeneratorEntry it) {
		timer.codeGenerator.timerName
	}

}
