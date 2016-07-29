/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers

import com.google.inject.Inject
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.arduino.generator.cpp.NamingExtension

class ATmega168_328Timer1 implements AbstractTimerCodeGenerator {

	@Inject extension NamingExtension

	override timerName() {
		"ATmega168_328Timer1"
	}

	override generateTimerHeader(ExecutionFlow it) '''
		«header»
		
		#ifndef «timerName.h.define»
		#define «timerName.h.define»
		
		#include <Arduino.h>
		#include <avr/sleep.h>
		#include "«abstractTimer.h»"
		
		class «timerName»: public «abstractTimer» {
		public:
			/* period in milliseconds */
			«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
					unsigned short maxParallelTimeEvents, unsigned int period);
		
			inline virtual ~«timerName»();
		
		protected:
			virtual void init();
		
			virtual void sleep();
		
			virtual void stop();
		};
		
		«timerName»::~«timerName»() {
		}
		
		#endif /* «timerName.h.define» */
	'''

	override generateTimer(ExecutionFlow it) '''
		«header»
		
		#include "«timerName.h»"
		
		extern bool runCycleFlag;
		
		ISR(TIMER1_COMPA_vect) {
			runCycleFlag = true;
		}
		
		«timerName»::«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
			unsigned short maxParallelTimeEvents, unsigned int period) :
			«abstractTimer»(statemachine, hardware, maxParallelTimeEvents, period) {
		}
		
		void «timerName»::init() {
			// initialize Timer1
			noInterrupts();
			TCCR1A = 0;     // set entire TCCR1A register to 0
			TCCR1B = 0;     // same for TCCR1B
		
			// set compare match register to desired timer count:
			// period in milliseconds, Arduino runs at 16 MHz, prescaler at 1024
			OCR1A = (int)((((float)this->period) * 0.001f * (16000000.0f / 1024.0f)) - 1);
		
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
		
		void «timerName»::sleep() {
			set_sleep_mode(SLEEP_MODE_IDLE);
			noInterrupts();
			sleep_enable();
			interrupts();
			sleep_cpu();
			sleep_disable();
		}
		
		void «timerName»::stop() {
			TCCR1B = 0; // turn off the timer
		}
	'''

}
