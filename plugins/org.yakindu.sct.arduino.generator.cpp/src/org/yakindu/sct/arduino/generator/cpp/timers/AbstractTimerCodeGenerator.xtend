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
import org.yakindu.sct.arduino.generator.cpp.Naming
import org.yakindu.sct.model.sexec.ExecutionFlow

abstract class AbstractTimerCodeGenerator {

	@Inject extension Naming

	public def String timerName()

	public def generateTimerHeader(ExecutionFlow it) '''
		«header»
		
		#ifndef «timerName.h.define»
		#define «timerName.h.define»
		
		«headerIncludes»
		
		class «timerName»: public «timerInterface» {
		public:
			«publicHeaderPart»
		
		private:
			«privateHeaderPart»
		};
		
		«timerName»::~«timerName»() {
			delete this->events;
		}
		
		#endif /* «timerName.h.define» */
	'''


	public def CharSequence generateTimer(ExecutionFlow it)

	protected def headerIncludes(ExecutionFlow it) '''
		#include <Arduino.h>
		#include <avr/sleep.h>
		#include <stdio.h>
		
		#include "«typesModule.h»"
		#include "«timerInterface.h»"
		#include "«statemachineInterface.h»"
		#include "«timedStatemachineInterface.h»"
		#include "«timeEvent.h»"
		#include "«hardwareConnector.h»"
	'''

	protected def publicHeaderPart() '''
		/* period in milliseconds */
		«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
				unsigned char maxParallelTimeEvents, unsigned int period);
		
		inline ~«timerName»();
		
		void start();
		
		void setTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId, sc_integer time,
				sc_boolean isPeriodic);
		
		void unsetTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId);
		
		void runCycle();
		
		void cancel();
	'''

	protected def privateHeaderPart() '''
		«statemachineInterface»* statemachine;
		
		«hardwareConnector»* hardware;
		
		«timeEvent»* events;
		
		unsigned int period;
		
		unsigned char maxParallelTimeEvents;
		
		void init();
		
		void raiseTimeEvents();
	'''

	protected def constructor() '''
		«timerName»::«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
				unsigned char maxParallelTimeEvents, unsigned int period) {
			«constructorBody»
		}
	'''

	protected def constructorBody() '''
		this->statemachine = statemachine;
		this->hardware = hardware;
		this->maxParallelTimeEvents = maxParallelTimeEvents;
		this->period = period;
	
		this->events = new «timeEvent»[this->maxParallelTimeEvents];
		for (unsigned char i = 0; i < this->maxParallelTimeEvents; i++) {
			this->events[i].eventId = NULL;
		}
	'''

	protected def start() '''
		void «timerName»::start() {
			«startBody»
		}
	'''

	protected def startBody() '''
		this->statemachine->init();
		this->statemachine->enter();
		this->hardware->init();
		this->init();
	'''

	protected def init() '''
		void «timerName»::init() {
			«initBody»
		}
	'''

	protected def initBody() '''
	'''

	protected def setTimer() '''
		void «timerName»::setTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId, sc_integer duration,
				sc_boolean isPeriodic) {
			«setTimerBody»
		}
	'''

	protected def setTimerBody() '''
		for (unsigned char i = 0; i < this->maxParallelTimeEvents; i++) {
			if (events[i].eventId == NULL) {
				events[i].timedStatemachine = timedStatemachine;
				events[i].eventId = eventId;
				events[i].overflows = duration / this->period;
				events[i].periodic = isPeriodic;
				events[i].overflowCounter = 0;
				events[i].eventRaised = false;
				break;
			}
		}
	'''

	protected def unsetTimer() '''
		void «timerName»::unsetTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId) {
			«unsetTimerBody»
		}
	'''
	
	protected def unsetTimerBody() '''
		for (unsigned char i = 0; i < this->maxParallelTimeEvents; i++) {
			if (events[i].eventId == eventId) {
				events[i].eventId = NULL;
				break;
			}
		}
	'''

	protected def runCycle() '''
		void «timerName»::runCycle() {
			«runCycleBody»
		}
	'''

	protected def runCycleBody() '''
		this->raiseTimeEvents();
		this->statemachine->runCycle();
		this->hardware->runCycle();
	'''

	protected def raiseTimeEvents() '''
		void «timerName»::raiseTimeEvents() {
			«raiseTimeEventsBody»
		}
	'''

	protected def raiseTimeEventsBody() '''
		for (unsigned char i = 0; i < this->maxParallelTimeEvents; i++) {
			if (events[i].eventId == NULL) {
				continue;
			}
		
			events[i].overflowCounter++;
		
			if ((events[i].overflowCounter >= events[i].overflows) && !events[i].eventRaised) {
				events[i].timedStatemachine->raiseTimeEvent(events[i].eventId);
				events[i].overflowCounter = 0;
		
				if (!events[i].periodic) {
					events[i].eventRaised = true;
				}
			}
		}
	'''

	protected def cancel() '''
		void «timerName»::cancel() {
			«cancelBody»
		}
	'''

	protected def cancelBody() '''
	'''

}
