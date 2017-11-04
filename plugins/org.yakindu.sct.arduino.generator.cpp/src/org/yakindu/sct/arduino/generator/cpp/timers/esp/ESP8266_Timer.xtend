package org.yakindu.sct.arduino.generator.cpp.timers.esp

import com.google.inject.Inject
import org.yakindu.sct.arduino.generator.cpp.timers.AbstractTimer
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.arduino.generator.cpp.ArduinoCPPNaming
import org.yakindu.sct.arduino.generator.cpp.ArduinoGenmodelEntries

class ESP8266_Timer extends AbstractTimer {

	@Inject extension ArduinoCPPNaming
	@Inject extension ArduinoGenmodelEntries

	override timerName() {
		"ESP8266_Timer"
	}

	override generateTimer(GeneratorEntry it, ExecutionFlow flow) '''
		«licenseText»
		
		#include "«timerName.h»"
		
		«variableDeclarations(flow)»
		
		«timerCallback»
		
		«constructor(flow)»
		
		«start»
		
		«init»
		
		«setTimer»
		
		«unsetTimer»
		
		«runCycle(flow)»
		
		«raiseTimeEvents»
		
		«cancel»
	'''

	override protected headerIncludes(GeneratorEntry it, ExecutionFlow flow) '''
		«super.headerIncludes(it, flow)»
		#include "user_interface.h"
	'''

	override protected CharSequence variableDeclarations(GeneratorEntry it, ExecutionFlow flow) '''
		«super.variableDeclarations(it, flow)»

		bool runCycleFlag = false;
		
		os_timer_t osTimer;
	'''

	protected def CharSequence timerCallback(GeneratorEntry it) '''
		void timerCallback(void *pArg) {
			runCycleFlag = true;
		}
	'''

	override protected initBody(GeneratorEntry it) '''
		os_timer_setfn(&osTimer, timerCallback, NULL);
		os_timer_arm(&osTimer, CYCLE_PERIOD, true);
	'''

	override protected runCycleBody(GeneratorEntry it, ExecutionFlow flow) '''
		if (runCycleFlag) {
			«super.runCycleBody(it, flow)»
			runCycleFlag = false;
		}
		yield();
	'''

	override protected cancelBody(GeneratorEntry it) '''
		os_timer_disarm (&osTimer);
	'''

}
