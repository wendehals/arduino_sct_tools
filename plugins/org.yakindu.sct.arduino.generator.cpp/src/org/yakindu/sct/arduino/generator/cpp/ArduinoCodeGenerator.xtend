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
import org.yakindu.sct.arduino.generator.cpp.timers.AbstractTimerCodeGenerator
import org.yakindu.sct.generator.core.AbstractWorkspaceGenerator
import org.yakindu.sct.generator.core.impl.IExecutionFlowGenerator
import org.yakindu.sct.generator.cpp.StatemachineHeader
import org.yakindu.sct.generator.cpp.StatemachineImplementation
import org.yakindu.sct.generator.cpp.StatemachineInterface
import org.yakindu.sct.generator.cpp.TimedStatemachineInterface
import org.yakindu.sct.generator.cpp.TimerInterface
import org.yakindu.sct.generator.cpp.Types
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sgraph.Statechart
import com.google.inject.Injector

class ArduinoCodeGenerator extends AbstractWorkspaceGenerator implements IExecutionFlowGenerator {

	@Inject Injector injector;

	@Inject extension Types
	@Inject extension TimedStatemachineInterface
	@Inject extension TimerInterface
	@Inject extension StatemachineInterface
	@Inject extension StatemachineHeader
	@Inject extension StatemachineImplementation

	@Inject extension GenmodelEntriesExtension
	@Inject extension NamingExtension
	@Inject extension ArduinoMainHeader
	@Inject extension ArduinoMain
	@Inject extension StatemachineConnectorHeader
	@Inject extension StatemachineConnector
	@Inject extension TimeEventHeader
	@Inject extension AbstractTimerHeader
	@Inject extension AbstractTimer
	@Inject extension HardwareConnectorHeader

	override generate(ExecutionFlow it, GeneratorEntry entry, IFileSystemAccess fsa) {
		// C++ code generation for statechart
		generateTypesHeader(entry, fsa)
		generateIStatemachine(entry, fsa);
		generateITimedStatemachine(entry, fsa);
		generateITimerService(entry, fsa);
		generateStatemachineHeader(sourceElement as Statechart, fsa, entry)
		generateStatemachineImplemenation(sourceElement as Statechart, fsa, entry)

		// Arduino specific sources
		// output folder
		generateArduinoMainHeader(entry, fsa);
		generateArduinoMain(entry, fsa);
		generateHardwareConnectorHeader(fsa);
		generateTimeEventHeader(fsa);
		generateAbstractTimerHeader(fsa);
		generateAbstractTimer(fsa);
		
		injector.injectMembers(entry.timer.codeGenerator)
		generateTimer(fsa, entry.timer.codeGenerator)

		// userSrcFolder
		generateStatemachineConnectorHeader(entry, fsa);
		generateStatemachineConnector(entry, fsa);
	}

	def generateTypesHeader(ExecutionFlow it, GeneratorEntry entry, IFileSystemAccess fsa) {
		fsa.generateFile(typesModule.h, typesHContent(entry))
	}

	def generateTimer(ExecutionFlow it,IFileSystemAccess fsa, AbstractTimerCodeGenerator generator) {
		fsa.generateFile(generator.timerName.h, generator.generateTimerHeader(it))
		fsa.generateFile(generator.timerName.cpp, generator.generateTimer(it))
	}

}
