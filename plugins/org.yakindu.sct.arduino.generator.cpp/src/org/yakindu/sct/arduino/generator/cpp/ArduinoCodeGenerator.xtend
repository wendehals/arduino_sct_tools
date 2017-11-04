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
import org.yakindu.sct.arduino.generator.cpp.features.IArduinoFeatureConstants
import org.yakindu.sct.arduino.generator.cpp.timers.AbstractTimer
import org.yakindu.sct.generator.c.IContentTemplate
import org.yakindu.sct.generator.c.IGenArtifactConfigurations
import org.yakindu.sct.generator.c.IGenArtifactConfigurations.GenArtifactConfiguration
import org.yakindu.sct.generator.core.IExecutionFlowGenerator
import org.yakindu.sct.generator.core.filesystem.ISCTFileSystemAccess
import org.yakindu.sct.generator.cpp.StatemachineHeader
import org.yakindu.sct.generator.cpp.StatemachineImplementation
import org.yakindu.sct.generator.cpp.StatemachineInterface
import org.yakindu.sct.generator.cpp.TimedStatemachineInterface
import org.yakindu.sct.generator.cpp.TimerInterface
import org.yakindu.sct.generator.cpp.Types
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sexec.extensions.SExecExtensions
import org.yakindu.sct.model.sgen.GeneratorEntry

class ArduinoCodeGenerator implements IExecutionFlowGenerator {

	@Inject Injector injector;
	@Inject Types typesContent
	@Inject TimedStatemachineInterface timedStatemachineInterfaceContent
	@Inject TimerInterface timerInterfaceContent
	@Inject StatemachineInterface statemachineInterfaceContent
	@Inject StatemachineHeader statemachineHeaderContent
	@Inject StatemachineImplementation statemachineImplementationContent

	@Inject ArduinoMainHeader arduinoMainHeaderContent
	@Inject ArduinoMain arduinoMainContent
	@Inject TimeEventHeader timeEventHeaderContent
	@Inject HardwareConnectorHeader hardwareConnectorHeaderContent
	@Inject StatemachineConnectorHeader statemachineConnectorHeaderContent
	@Inject StatemachineConnector statemachineConnectorContent

	@Inject IGenArtifactConfigurations locations

	@Inject extension ArduinoCPPNaming
	@Inject extension ArduinoGenmodelEntries
	@Inject extension SExecExtensions

	override generate(ExecutionFlow it, GeneratorEntry entry, IFileSystemAccess fsa) {
		initGenerationArtifacts(locations, it, entry)

		for (GenArtifactConfiguration genArtifactConfig : locations.configurations) {
			fsa.generateFile(genArtifactConfig.getName, genArtifactConfig.getOutputName,
				genArtifactConfig.getContentTemplate.content(it, entry, locations))
		}

		injector.injectMembers(entry.timer.codeGenerator)
		generateTimer(entry, fsa, entry.timer.codeGenerator)
	}

	def private initGenerationArtifacts(IGenArtifactConfigurations it, ExecutionFlow flow, GeneratorEntry entry) {
		configure(flow.typesModule.h, typesContent)
		configure(statemachineInterface.h, statemachineInterfaceContent)

		if (flow.timed) {
			configure(timedStatemachineInterface.h, timedStatemachineInterfaceContent)
			configure(timerInterface.h, timerInterfaceContent)
		}

		configure(flow.module.h, statemachineHeaderContent)
		configure(flow.module.cpp, statemachineImplementationContent)

		// Arduino specific sources
		// output folder
		configure(arduinoMain.h, arduinoMainHeaderContent);
		configure(arduinoMain.cpp, arduinoMainContent);
		configure(hardwareConnector.h, hardwareConnectorHeaderContent);

		if (flow.timed) {
			configure(timeEvent.h, timeEventHeaderContent);
		}

		// userSrcFolder
		if (getUserSrcFolder(entry) !== null) {
			configure(flow.module.connector.h, IArduinoFeatureConstants::PARAM_USER_SRC_FOLDER,
				statemachineConnectorHeaderContent)
			configure(flow.module.connector.cpp, IArduinoFeatureConstants::PARAM_USER_SRC_FOLDER,
				statemachineConnectorContent)
		} else {
			configure(flow.module.connector.h, statemachineConnectorHeaderContent)
			configure(flow.module.connector.cpp, statemachineConnectorContent)
		}
	}

	def private configure(IGenArtifactConfigurations it, String artifactName, IContentTemplate contentTemplate) {
		configure(artifactName, ISCTFileSystemAccess.TARGET_FOLDER_OUTPUT, contentTemplate)
	}

	def private generateTimer(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa, AbstractTimer it) {
		fsa.generateFile(timerName.h, generateTimerHeader(entry, flow))
		fsa.generateFile(timerName.cpp, generateTimer(entry, flow))
	}

}
