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
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry

class ArduinoMainHeader {

	@Inject extension NamingExtension

	def generateArduinoMainHeader(ExecutionFlow it, GeneratorEntry entry, IFileSystemAccess fsa) {
		fsa.generateFile(arduinoMain.h, generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		«header»
		
		#ifndef «arduinoMain.h.define»
		#define «arduinoMain.h.define»
		
		#include <Arduino.h>
		#include "«module.h»"
		
		«module»* getStatemachine();
		
		#endif /* «arduinoMain.h.define» */
	'''

}
