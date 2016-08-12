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
import com.google.inject.Injector
import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.arduino.generator.cpp.timers.AbstractTimerCodeGenerator
import org.yakindu.sct.generator.c.GenArtifactConfigurations.GenArtifactConfiguration
import org.yakindu.sct.generator.c.IGenArtifactConfigurations
import org.yakindu.sct.generator.core.impl.IExecutionFlowGenerator
import org.yakindu.sct.generator.cpp.StatemachineHeader
import org.yakindu.sct.generator.cpp.StatemachineImplementation
import org.yakindu.sct.generator.cpp.StatemachineInterface
import org.yakindu.sct.generator.cpp.TimedStatemachineInterface
import org.yakindu.sct.generator.cpp.TimerInterface
import org.yakindu.sct.generator.cpp.Types
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry

class ArduinoCodeGenerator implements IExecutionFlowGenerator {

	@Inject Injector injector;
	@Inject Types typesContent
	@Inject TimedStatemachineInterface timedStatemachineInterfaceContent
	@Inject TimerInterface timerInterfaceContent
	@Inject StatemachineInterface statemachineInterfaceContent
	@Inject StatemachineHeader statemachineHeaderContent
	@Inject StatemachineImplementation statemachineImplementationContent

	@Inject extension Naming
	@Inject extension GenmodelEntries
	@Inject extension ArduinoMainHeader
	@Inject extension ArduinoMain
	@Inject extension StatemachineConnectorHeader
	@Inject extension StatemachineConnector
	@Inject extension TimeEventHeader
	@Inject extension AbstractTimerHeader
	@Inject extension AbstractTimer
	@Inject extension HardwareConnectorHeader
//
//	@Inject @Named(IGenArtifactConfigurations.DEFAULT)
//	IGenArtifactConfigurations defaultConfigs
//

	override generate(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa) {
		throw new UnsupportedOperationException("deprecated")
	}

//	/**
//	 * @Deprecated use {@link #generate(ExecutionFlow, GeneratorEntry, IFileSystemAccess, ArtifactLocationProvider)} instead
//	 */
//	@Deprecated
//	override generate(ExecutionFlow it, GeneratorEntry entry, IFileSystemAccess fsa) {
//		generate(entry, fsa, defaultConfigs)
//	}

	def generate(ExecutionFlow it, GeneratorEntry entry, IFileSystemAccess fsa, IGenArtifactConfigurations locations) {
		initGenerationArtifacts(entry, locations)
		generateArtifacts(entry, fsa, locations)

		// Arduino specific sources
		// output folder
		generateArduinoMainHeader(entry, fsa)
		generateArduinoMain(entry, fsa)
		generateHardwareConnectorHeader(fsa)
		generateTimeEventHeader(fsa)
		generateAbstractTimerHeader(fsa)
		generateAbstractTimer(fsa)

		injector.injectMembers(entry.timer.codeGenerator)
		generateTimer(fsa, entry.timer.codeGenerator)

		// userSrcFolder
		generateStatemachineConnectorHeader(entry, fsa)
		generateStatemachineConnector(entry, fsa)
	}

	def generateArtifacts(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa,
		IGenArtifactConfigurations locations) {
		for (GenArtifactConfiguration a : locations.configurations) {
			fsa.generateFile(a.getName, a.getOutputName, a.getContentTemplate.content(flow, entry, locations))
		}
	}

	def initGenerationArtifacts(ExecutionFlow flow, GeneratorEntry entry, IGenArtifactConfigurations locations) {
		locations.configure(flow.typesModule.h, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT, typesContent)
		locations.configure(statemachineInterface.h, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT,
			statemachineInterfaceContent)
		locations.configure(timedStatemachineInterface.h, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT,
			timedStatemachineInterfaceContent)
		locations.configure(timerInterface.h, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT, timerInterfaceContent)
		locations.configure(flow.module.h, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT, statemachineHeaderContent)
		locations.configure(flow.module.cpp, IExecutionFlowGenerator.TARGET_FOLDER_OUTPUT,
			statemachineImplementationContent)
	}

	def generateTimer(ExecutionFlow it, IFileSystemAccess fsa, AbstractTimerCodeGenerator generator) {
		fsa.generateFile(generator.timerName.h, generator.generateTimerHeader(it))
		fsa.generateFile(generator.timerName.cpp, generator.generateTimer(it))
	}

}
