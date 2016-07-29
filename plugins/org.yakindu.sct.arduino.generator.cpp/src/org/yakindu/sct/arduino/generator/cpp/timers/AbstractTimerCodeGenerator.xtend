package org.yakindu.sct.arduino.generator.cpp.timers

import org.yakindu.sct.model.sexec.ExecutionFlow

abstract interface AbstractTimerCodeGenerator {
	
	def String timerName()

	def CharSequence generateTimerHeader(ExecutionFlow it)
	
	def CharSequence generateTimer(ExecutionFlow it)

}