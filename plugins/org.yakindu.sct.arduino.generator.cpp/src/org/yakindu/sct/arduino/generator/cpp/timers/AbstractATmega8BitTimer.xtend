/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry

abstract class AbstractATmega8BitTimer extends AbstractATmegaTimer {

	protected final static int MAX_PERIOD = 16;

	protected final static int OVERFLOW_COMPARE_VALUE = 249;

	override protected variableDeclarations(GeneratorEntry it, ExecutionFlow flow) '''
		«super.variableDeclarations(it, flow)»
		«IF useOverflows»
			const unsigned char MAX_PERIOD = «MAX_PERIOD»;
			const unsigned char OVERFLOW_COMPARE_VALUE = «OVERFLOW_COMPARE_VALUE»;
			
			bool runCycleFlag = false;
			unsigned int overflows = 0;
			unsigned int overflowCounter = 0;
			unsigned char moduloRest = 0;
		«ELSE»
			bool runCycleFlag = false;
		«ENDIF»
	'''

	override protected maxPeriod() {
		MAX_PERIOD
	}

}
