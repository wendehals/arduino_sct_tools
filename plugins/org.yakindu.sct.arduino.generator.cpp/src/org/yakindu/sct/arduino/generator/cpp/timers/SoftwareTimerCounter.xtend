/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

import com.google.inject.Inject
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.arduino.generator.cpp.Naming
import org.yakindu.sct.model.sgen.GeneratorEntry

class SoftwareTimerCounter extends AbstractTimerCodeGenerator {

	@Inject extension Naming

	override timerName() {
		"SoftwareTimerCounter"
	}

	override generateTimer(ExecutionFlow it, GeneratorEntry entry) '''
		«header»
		
		#include "«timerName.h»"

		«constructor»
		
		«start»
		
		«init»

		«setTimer»
		
		«unsetTimer»

		«runCycle»

		«raiseTimeEvents»

		«cancel»
	'''

	override protected headerIncludes(ExecutionFlow it) '''
		#include <Arduino.h>
		#include <stdio.h>
		
		#include "«typesModule.h»"
		#include "«timerInterface.h»"
		#include "«statemachineInterface.h»"
		#include "«timedStatemachineInterface.h»"
		#include "«timeEvent.h»"
		#include "«hardwareConnector.h»"
	'''

	override protected privateHeaderPart() '''
		«super.privateHeaderPart()»
		
		unsigned long lastCycle;
	'''

	override protected constructorBody() '''
		«super.constructorBody»
		
		lastCycle = 0;
	'''

	override protected initBody() '''
		lastCycle = millis();
	'''

	override protected runCycleBody() '''
		unsigned long current = millis();
		if (current>=lastCycle+this->period){
			«super.runCycleBody»
			lastCycle = current;
		}
	'''

}
