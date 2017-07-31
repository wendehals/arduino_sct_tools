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
import org.yakindu.sct.arduino.generator.cpp.GenmodelEntries
import org.yakindu.sct.arduino.generator.cpp.MaxParallelTimers
import org.yakindu.sct.arduino.generator.cpp.Naming
import org.yakindu.sct.model.sexec.ExecutionFlow
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.model.sgraph.Statechart
import org.yakindu.sct.model.sexec.extensions.SExecExtensions

abstract class AbstractTimer {

	@Inject extension Naming
	@Inject extension GenmodelEntries
	@Inject extension MaxParallelTimers
	@Inject extension SExecExtensions

	public def String timerName()

	public def generateTimerHeader(GeneratorEntry it, ExecutionFlow flow) '''
		«licenseText»
		
		#ifndef «timerName.h.define»
		#define «timerName.h.define»
		
		«IF flow.timed»
			#define MAX_PARALLEL_TIME_EVENTS «maxParallelTimers(flow.sourceElement as Statechart)»

		«ENDIF»
		«headerIncludes(flow)»
		
		class «timerName»«IF flow.timed»: public «timerInterface»«ENDIF» {
		public:
			«publicHeaderPart(flow)»
		
		private:
			«privateHeaderPart(flow)»
		};
		
		«timerName»::~«timerName»() {
			«IF flow.timed»
				delete events;
			«ENDIF»
		}
		
		#endif /* «timerName.h.define» */
	'''

	public def generateTimer(GeneratorEntry it, ExecutionFlow flow) '''
		«licenseText»
		
		#include "«timerName.h»"
		
		«variableDeclarations(flow)»
		
		«constructor(flow)»
		
		«start»
		
		«init»
		
		«IF flow.timed»
			«setTimer»
			
			«unsetTimer»

		«ENDIF»
		«runCycle(flow)»
		
		«IF flow.timed»
			«raiseTimeEvents»
		«ENDIF»
		
		«cancel»
	'''

	protected def headerIncludes(GeneratorEntry it, ExecutionFlow flow) '''
		#include <Arduino.h>
		
		#include "«flow.typesModule.h»"
		#include "«statemachineInterface.h»"
		#include "«hardwareConnector.h»"
		«IF flow.timed»
			#include "«timerInterface.h»"
			#include "«timedStatemachineInterface.h»"
			#include "«timeEvent.h»"
		«ENDIF»
	'''

	protected def publicHeaderPart(GeneratorEntry it, ExecutionFlow flow) '''
		«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware);
		
		inline ~«timerName»();
		
		void start();
		
		«IF flow.timed»
			void setTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId, sc_integer time, sc_boolean isPeriodic);
			
			void unsetTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId);

		«ENDIF»
		void runCycle();
		
		void cancel();
	'''

	protected def privateHeaderPart(GeneratorEntry it, ExecutionFlow flow) '''
		«statemachineInterface»* statemachine;
		
		«hardwareConnector»* hardware;
		
		«IF flow.timed»
			«timeEvent» events[MAX_PARALLEL_TIME_EVENTS];
		«ENDIF»
		
		void init();
		
		«IF flow.timed»
			void raiseTimeEvents();
		«ENDIF»
	'''

	protected def CharSequence variableDeclarations(GeneratorEntry it, ExecutionFlow flow) '''
		const unsigned int CYCLE_PERIOD = «cyclePeriod»;
	'''

	protected def constructor(GeneratorEntry it, ExecutionFlow flow) '''
		«timerName»::«timerName»(«statemachineInterface»* statemachine, «hardwareConnector»* hardware) {
			«constructorBody(flow)»
		}
	'''

	protected def constructorBody(GeneratorEntry it, ExecutionFlow flow) '''
		this->statemachine = statemachine;
		this->hardware = hardware;
		
		«IF flow.timed»
			for (unsigned char i = 0; i < MAX_PARALLEL_TIME_EVENTS; i++) {
				events[i].eventId = NULL;
			}
		«ENDIF»
	'''

	protected def start(GeneratorEntry it) '''
		void «timerName»::start() {
			«startBody»
		}
	'''

	protected def startBody(GeneratorEntry it) '''
		statemachine->init();
		statemachine->enter();
		hardware->init();
		init();
	'''

	protected def init(GeneratorEntry it) '''
		void «timerName»::init() {
			«initBody»
		}
	'''

	protected def initBody(GeneratorEntry it) '''
	'''

	protected def setTimer(GeneratorEntry it) '''
		void «timerName»::setTimer(«timedStatemachineInterface»* timedStatemachine, sc_eventid eventId, sc_integer duration, sc_boolean isPeriodic) {
			«setTimerBody»
		}
	'''

	protected def setTimerBody(GeneratorEntry it) '''
		for (unsigned char i = 0; i < MAX_PARALLEL_TIME_EVENTS; i++) {
			if (events[i].eventId == NULL) {
				events[i].timedStatemachine = timedStatemachine;
				events[i].eventId = eventId;
				events[i].overflows = duration / CYCLE_PERIOD;
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
		for (unsigned char i = 0; i < MAX_PARALLEL_TIME_EVENTS; i++) {
			if (events[i].eventId == eventId) {
				events[i].eventId = NULL;
				break;
			}
		}
	'''

	protected def runCycle(GeneratorEntry it, ExecutionFlow flow) '''
		void «timerName»::runCycle() {
			«runCycleBody(flow)»
		}
	'''

	protected def runCycleBody(GeneratorEntry it, ExecutionFlow flow) '''
		«IF flow.timed»
			raiseTimeEvents();
		«ENDIF»
		hardware->raiseEvents();
		statemachine->runCycle();
		hardware->syncState();
	'''

	protected def raiseTimeEvents(GeneratorEntry it) '''
		void «timerName»::raiseTimeEvents() {
			«raiseTimeEventsBody»
		}
	'''

	protected def raiseTimeEventsBody(GeneratorEntry it) '''
		for (unsigned char i = 0; i < MAX_PARALLEL_TIME_EVENTS; i++) {
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
