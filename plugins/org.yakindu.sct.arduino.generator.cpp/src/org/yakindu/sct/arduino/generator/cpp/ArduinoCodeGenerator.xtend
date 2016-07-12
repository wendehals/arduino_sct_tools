package org.yakindu.sct.arduino.generator.cpp

import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.generator.core.AbstractWorkspaceGenerator
import org.yakindu.sct.generator.core.impl.IExecutionFlowGenerator
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import com.google.inject.Inject
import org.yakindu.sct.model.sgraph.Statechart
import org.yakindu.sct.generator.cpp.Types
import org.yakindu.sct.generator.cpp.TimedStatemachineInterface
import org.yakindu.sct.generator.cpp.TimerInterface
import org.yakindu.sct.generator.cpp.StatemachineInterface
import org.yakindu.sct.generator.cpp.StatemachineHeader
import org.yakindu.sct.generator.cpp.StatemachineImplementation

class ArduinoCodeGenerator extends AbstractWorkspaceGenerator implements IExecutionFlowGenerator {

	@Inject extension Types
	@Inject extension TimedStatemachineInterface
	@Inject extension TimerInterface
	@Inject extension StatemachineInterface
	@Inject extension StatemachineHeader
	@Inject extension StatemachineImplementation

	@Inject extension Naming
	@Inject extension GenmodelEntriesExtension
	@Inject extension ArduinoMain
	@Inject extension StatemachineConnectorHeader
	@Inject extension StatemachineConnector
	@Inject extension TimeEventHeader
	@Inject extension AbstractTimerHeader
	@Inject extension AbstractTimer
	@Inject extension SoftwareTimerHeader
	@Inject extension SoftwareTimer
	@Inject extension ATmega168_328TimerHeader
	@Inject extension ATmega168_328Timer
	@Inject extension HardwareConnectorHeader

	override generate(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa) {
		// C++ code generation for statechart
		flow.generateTypesHeader(entry, fsa)
		flow.generateIStatemachine(entry, fsa);
		flow.generateITimedStatemachine(entry, fsa);
		flow.generateITimerService(entry, fsa);
		flow.generateStatemachineHeader(flow.sourceElement as Statechart, fsa, entry)
		flow.generateStatemachineImplemenation(flow.sourceElement as Statechart, fsa, entry)

		// Arduino specific sources
		// output folder
		flow.generateMain(entry, fsa);
		flow.generateHardwareConnectorHeader(fsa);
		flow.generateTimeEventHeader(fsa);
		flow.generateAbstractTimerHeader(fsa);
		flow.generateAbstractTimer(fsa);

		if (isSoftwareTimer(entry)) {
			flow.generateSoftwareTimerHeader(fsa);
			flow.generateSoftwareTimer(fsa);
		} else if (isATmega168_328Timer(entry)) {
			flow.generateATmega168_328TimerHeader(fsa);
			flow.generateATmega168_328Timer(fsa);
		}

		// userSrcFolder
		flow.generateStatemachineConnectorHeader(entry, fsa);
		flow.generateStatemachineConnector(entry, fsa);
	}

	def generateTypesHeader(ExecutionFlow it, GeneratorEntry entry, IFileSystemAccess fsa) {
		fsa.generateFile(typesModule.h, typesHContent(entry))
	}

}
