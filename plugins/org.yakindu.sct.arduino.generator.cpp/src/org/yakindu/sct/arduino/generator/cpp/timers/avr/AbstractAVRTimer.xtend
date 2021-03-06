/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers.avr

import com.google.inject.Inject
import org.yakindu.sct.arduino.generator.cpp.timers.AbstractTimer
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sexec.extensions.SExecExtensions
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.arduino.generator.cpp.ArduinoCPPNaming
import org.yakindu.sct.arduino.generator.cpp.ArduinoGenmodelEntries

abstract class AbstractAVRTimer extends AbstractTimer {

	@Inject extension ArduinoCPPNaming
	@Inject extension ArduinoGenmodelEntries
	@Inject extension SExecExtensions

	override generateTimer(GeneratorEntry it, ExecutionFlow flow) '''
		«licenseText»
		
		#include "«timerName.h»"
		
		«variableDeclarations(flow)»
		
		«ISR»
		
		«constructor(flow)»
		
		«start»
		
		«init»
		
		«IF flow.timed»
			«setTimer»
			
			«unsetTimer»

		«ENDIF»
		«runCycle(flow)»
		
		«IF flow.timed»
			«raiseTimeEvents»
		«ENDIF»
		
		«sleep»
		
		«cancel»
	'''

	override protected headerIncludes(GeneratorEntry it, ExecutionFlow flow) '''
		#include <avr/sleep.h>
		«super.headerIncludes(it, flow)»
	'''

	override protected privateHeaderPart(GeneratorEntry it, ExecutionFlow flow) '''
		«super.privateHeaderPart(it, flow)»
		
		void sleep();
	'''

	protected def CharSequence ISR(GeneratorEntry it)

	override protected runCycleBody(GeneratorEntry it, ExecutionFlow flow) '''
		if (runCycleFlag) {
			«super.runCycleBody(it, flow)»
			runCycleFlag = false;
		}
		sleep();
	'''

	protected def CharSequence sleep(GeneratorEntry it) '''
		void «timerName»::sleep() {
			«sleepBody»
		}
	'''

	protected def CharSequence sleepBody(GeneratorEntry it) '''
		hardware->prepareSleepMode();
		
		set_sleep_mode(SLEEP_MODE_IDLE);
		noInterrupts();
		sleep_enable();
		interrupts();
		sleep_cpu();
		sleep_disable();
	'''

	protected def int maxPeriod()

	protected def boolean useOverflows(GeneratorEntry entry) {
		entry.cyclePeriod > maxPeriod;
	}

}
