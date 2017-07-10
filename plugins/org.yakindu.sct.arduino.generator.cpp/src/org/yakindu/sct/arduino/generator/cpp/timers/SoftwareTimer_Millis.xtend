/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sexec.ExecutionFlow

class SoftwareTimer_Millis extends AbstractTimer {

	override timerName() {
		"SoftwareTimer_Millis"
	}

	override protected privateHeaderPart(GeneratorEntry it, ExecutionFlow flow) '''
		«super.privateHeaderPart(it, flow)»
		
		unsigned long lastCycle;
	'''

	override protected constructorBody(GeneratorEntry it, ExecutionFlow flow) '''
		«super.constructorBody(it, flow)»
		
		lastCycle = 0;
	'''

	override protected initBody(GeneratorEntry it) '''
		lastCycle = millis();
	'''

	override protected runCycleBody(GeneratorEntry it, ExecutionFlow flow) '''
		unsigned long current = millis();
		if (current >= lastCycle + CYCLE_PERIOD){
			«super.runCycleBody(it, flow)»
			lastCycle = current;
		}
	'''

}
