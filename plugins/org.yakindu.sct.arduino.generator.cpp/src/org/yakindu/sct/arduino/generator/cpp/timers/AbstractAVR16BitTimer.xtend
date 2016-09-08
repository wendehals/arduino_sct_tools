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

abstract class AbstractAVR16BitTimer extends AbstractAVRTimer {

	protected final static int MAX_PERIOD = 4192;

	protected final static int OVERFLOW_COMPARE_VALUE = 65499;

	override protected variableDeclarations(GeneratorEntry it) '''
		«IF useOverflows»
			const unsigned int MAX_PERIOD = «MAX_PERIOD»;
			const unsigned int OVERFLOW_COMPARE_VALUE = «OVERFLOW_COMPARE_VALUE»;
			
			bool runCycleFlag = false;
			unsigned char overflows = 0;
			unsigned char overflowCounter = 0;
			unsigned int moduloRest = 0;
		«ELSE»
			bool runCycleFlag = false;
		«ENDIF»
	'''

	override protected maxPeriod() {
		MAX_PERIOD
	}

}
