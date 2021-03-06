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
import org.yakindu.sct.model.sexec.extensions.SExecExtensions
import org.yakindu.sct.model.sgen.GeneratorEntry

class ArduinoMain implements IContentTemplate {

	@Inject extension ArduinoCPPNaming
	@Inject extension ArduinoGenmodelEntries
	@Inject extension SExecExtensions

	override content(ExecutionFlow it, GeneratorEntry entry, IGenArtifactConfigurations locations) '''
		«entry.licenseText»

		#include "«arduinoMain.h»"
		#include "«entry.timerClassName.h»"
		#include "«entry.userSrcFolderRelativeToSrcGen»«module.connector.h»"

		«module»* statemachine;
		«module.connector»* connector;
		«entry.timerClassName»* timer;

		«module»* getStatemachine() {
			return statemachine;
		}

		void setup() {
			statemachine = new «module»();
			connector = new «module.connector»(statemachine);
			timer = new «entry.timerClassName»(statemachine, connector);

			«IF timed»statemachine->setTimer(timer);«ENDIF»
			timer->start();
		}

		void loop() {
			timer->runCycle();
		}
	'''

	def String timerClassName(GeneratorEntry it) {
		timer.codeGenerator.timerName
	}

}
