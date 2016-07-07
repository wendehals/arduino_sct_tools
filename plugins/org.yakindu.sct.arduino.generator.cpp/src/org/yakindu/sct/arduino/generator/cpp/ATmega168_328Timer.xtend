package org.yakindu.sct.arduino.generator.cpp

import org.eclipse.xtext.generator.IFileSystemAccess
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sgraph.Statechart
import com.google.inject.Inject

class ATmega168_328Timer {
	
	@Inject
	extension Naming

	def generateATmega168_328Timer(ExecutionFlow flow, Statechart sc, IFileSystemAccess fsa, GeneratorEntry entry) {
		fsa.generateFile(atMega168_328Timer.cpp, flow.generateContents(entry))
	}

	def private generateContents(ExecutionFlow it, GeneratorEntry entry) '''
		#include "«atMega168_328Timer.h»"
		
		extern bool runCycleFlag;
		
		ISR(TIMER1_COMPA_vect) {
			runCycleFlag = true;
		}
		
		«atMega168_328Timer»::«atMega168_328Timer»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
				unsigned short maxParallelTimeEvents, unsigned int period) :
				«abstractTimer.h»(statemachine, hardware, maxParallelTimeEvents, period) {
		}
		
		void «atMega168_328Timer»::init() {
			// initialize Timer1
			noInterrupts();
			TCCR1A = 0;     // set entire TCCR1A register to 0
			TCCR1B = 0;     // same for TCCR1B
		
			// set compare match register to desired timer count:
			// period in ms, Arduino runs at 16 MHz, prescaler at 1024
			OCR1A = (this->period * 0.001f * (16000000 / 1024)) - 1;
		
			// turn on CTC mode
			TCCR1B |= (1 << WGM12);
		
			// Set CS10 and CS12 bits for 1024 prescaler
			TCCR1B |= (1 << CS10);
			TCCR1B |= (1 << CS12);
		
			// enable timer compare interrupt
			TIMSK1 |= (1 << OCIE1A);
		
			// enable global interrupts
			interrupts();
		}
		
		void «atMega168_328Timer»::sleep() {
			set_sleep_mode(SLEEP_MODE_IDLE);
			noInterrupts();
			sleep_enable();
			interrupts();
			sleep_cpu();
			sleep_disable();
		}
		
		void «atMega168_328Timer»::stop() {
			TCCR1B = 0; // turn off the timer
		}
	'''
	
}