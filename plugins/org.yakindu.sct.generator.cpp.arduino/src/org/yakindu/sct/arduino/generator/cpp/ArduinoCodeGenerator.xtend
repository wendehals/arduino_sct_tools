package org.yakindu.sct.arduino.generator.cpp

import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.generator.core.AbstractWorkspaceGenerator
import org.yakindu.sct.generator.core.impl.IExecutionFlowGenerator
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import com.google.inject.Inject
import org.yakindu.sct.model.sgraph.Statechart

class ArduinoCodeGenerator extends AbstractWorkspaceGenerator implements IExecutionFlowGenerator {

	@Inject
	extension Main

	@Inject
	extension StatemachineConnectorHeader

	@Inject
	extension StatemachineConnector

	@Inject
	extension TimeEventHeader

	@Inject
	extension AbstractTimerHeader

	@Inject
	extension AbstractTimer

	@Inject
	extension ATmega168_328TimerHeader

	@Inject
	extension ATmega168_328Timer

	@Inject
	extension HardwareConnectorHeader

	override generate(ExecutionFlow flow, GeneratorEntry entry, IFileSystemAccess fsa) {
		flow.generateMain(flow.sourceElement as Statechart, fsa, entry);
		flow.generateStatemachineConnectorHeader(flow.sourceElement as Statechart, fsa, entry);
		flow.generateStatemachineConnector(flow.sourceElement as Statechart, fsa, entry);
		flow.generateTimeEventHeader(flow.sourceElement as Statechart, fsa, entry);
		flow.generateAbstractTimerHeader(flow.sourceElement as Statechart, fsa, entry);
		flow.generateAbstractTimer(flow.sourceElement as Statechart, fsa, entry);
		flow.generateATmega168_328TimerHeader(flow.sourceElement as Statechart, fsa, entry);
		flow.generateATmega168_328Timer(flow.sourceElement as Statechart, fsa, entry);
		flow.generateHardwareConnectorHeader(flow.sourceElement as Statechart, fsa, entry);
	}

}
