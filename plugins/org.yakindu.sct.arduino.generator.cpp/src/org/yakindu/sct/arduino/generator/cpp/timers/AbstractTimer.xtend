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
import org.yakindu.sct.model.sgen.GeneratorEntry

abstract class AbstractTimer {

	@Inject extension Naming

	public def String timerName()

	public def generateTimerHeader(GeneratorEntry it, ExecutionFlow flow) '''
		«header»
		
		#ifndef «timerName.h.define»
		#define «timerName.h.define»
		
		«headerIncludes(flow)»
		
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

	public def generateTimer(GeneratorEntry it, ExecutionFlow flow) '''
		«header»
		
		#include "«timerName.h»"

		«constructor»
		
		«start»
		
		«init»

		«setTimer»
		
		«unsetTimer»

		«runCycle»

		«raiseTimeEvents»

		«cancel»
	'''

	protected def headerIncludes(GeneratorEntry it, ExecutionFlow flow) '''
		#include <Arduino.h>
		#include <stdio.h>
		
		#include "«flow.typesModule.h»"
		#include "«timerInterface.h»"
		#include "«statemachineInterface.h»"
		#include "«timedStatemachineInterface.h»"
		#include "«timeEvent.h»"
		#include "«hardwareConnector.h»"
	'''

	protected def publicHeaderPart(GeneratorEntry it) '''
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

	protected def privateHeaderPart(GeneratorEntry it) '''
		«statemachineInterface»* statemachine;
		
		«hardwareConnector»* hardware;
		
		«timeEvent»* events;
		
		unsigned int period;
		
		unsigned char maxParallelTimeEvents;
		
		void init();
		
		void raiseTimeEvents();
	'''

	protected def constructor(GeneratorEntry it) '''
		«timerName»::«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware,
				unsigned char maxParallelTimeEvents, unsigned int period) {
			«constructorBody»
		}
	'''

	protected def constructorBody(GeneratorEntry it) '''
		this->statemachine = statemachine;
		this->hardware = hardware;
		this->maxParallelTimeEvents = maxParallelTimeEvents;
		this->period = period;
		
		this->events = new «timeEvent»[this->maxParallelTimeEvents];
		for (unsigned char i = 0; i < this->maxParallelTimeEvents; i++) {
			this->events[i].eventId = NULL;
		}
	'''

	protected def start(GeneratorEntry it) '''
		void «timerName»::start() {
			«startBody»
		}
	'''

	protected def startBody(GeneratorEntry it) '''
		this->statemachine->init();
		this->statemachine->enter();
		this->hardware->init();
		this->init();
	'''

	protected def init(GeneratorEntry it) '''
		void «timerName»::init() {
			«initBody»
		}
	'''

	protected def initBody(GeneratorEntry it) '''
	'''

	protected def setTimer(GeneratorEntry it) '''
		void «timerName»::setTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId, sc_integer duration,
				sc_boolean isPeriodic) {
			«setTimerBody»
		}
	'''

	protected def setTimerBody(GeneratorEntry it) '''
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

	protected def unsetTimer(GeneratorEntry it) '''
		void «timerName»::unsetTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId) {
			«unsetTimerBody»
		}
	'''

	protected def unsetTimerBody(GeneratorEntry it) '''
		for (unsigned char i = 0; i < this->maxParallelTimeEvents; i++) {
			if (events[i].eventId == eventId) {
				events[i].eventId = NULL;
				break;
			}
		}
	'''

	protected def runCycle(GeneratorEntry it) '''
		void «timerName»::runCycle() {
			«runCycleBody»
		}
	'''

	protected def runCycleBody(GeneratorEntry it) '''
		this->raiseTimeEvents();
		this->statemachine->runCycle();
		this->hardware->runCycle();
	'''

	protected def raiseTimeEvents(GeneratorEntry it) '''
		void «timerName»::raiseTimeEvents() {
			«raiseTimeEventsBody»
		}
	'''

	protected def raiseTimeEventsBody(GeneratorEntry it) '''
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

	protected def cancel(GeneratorEntry it) '''
		void «timerName»::cancel() {
			«cancelBody»
		}
	'''

	protected def cancelBody(GeneratorEntry it) '''
	'''

}
