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
import org.yakindu.sct.arduino.generator.cpp.GenmodelEntries
import org.yakindu.sct.arduino.generator.cpp.Naming
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry

abstract class AbstractAVRTimer extends AbstractTimer {
	
	@Inject extension Naming
	@Inject extension GenmodelEntries

	protected boolean useOverflows;
	
	override protected privateHeaderPart() '''
		«super.privateHeaderPart()»
		
		void sleep();
	'''

	override generateTimer(ExecutionFlow it, GeneratorEntry entry) '''
		«initUseOverflows(entry)»«header»
		
		#include "«timerName.h»"
		
		«variableDeclarations»
		
		«ISR»
		
		«constructor»
		
		«start»
		
		«init»
		
		«setTimer»
		
		«unsetTimer»
		
		«runCycle»
		
		«raiseTimeEvents»
		
		«sleep»
		
		«cancel»
	'''

	protected def CharSequence variableDeclarations() '''
		«IF useOverflows»
		const int MAX_PERIOD = «maxPeriod»;
		const int MAX_OVERFLOW_COUNTER = 249;
		
		bool runCycleFlag = false;
		unsigned char overflows = 0;
		unsigned char overflowCounter = 0;
		unsigned int moduloRest = 0;
		«ELSE»
		bool runCycleFlag = false;
		«ENDIF»
	'''
	
	protected def CharSequence ISR()
	
	override protected runCycleBody() '''
		if (runCycleFlag) {
			«super.runCycleBody()»
			runCycleFlag = false;
		}
		this->sleep();
	'''

	protected def sleep() '''
		void «timerName»::sleep() {
			set_sleep_mode(SLEEP_MODE_IDLE);
			noInterrupts();
			sleep_enable();
			interrupts();
			sleep_cpu();
			sleep_disable();
		}
	'''
	
	protected def int maxPeriod()
	
	protected def int maxOverflowCounter()
	
	protected def void initUseOverflows(GeneratorEntry entry) {
		useOverflows = entry.cyclePeriod > maxPeriod;
	}

}