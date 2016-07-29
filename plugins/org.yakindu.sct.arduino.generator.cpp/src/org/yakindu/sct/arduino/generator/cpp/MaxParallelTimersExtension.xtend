/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp

import org.yakindu.sct.model.sgraph.Reaction
import org.yakindu.sct.model.sgraph.Region
import org.yakindu.sct.model.sgraph.State
import org.yakindu.sct.model.sgraph.Statechart
import org.yakindu.sct.model.sgraph.Transition
import org.yakindu.sct.model.sgraph.Trigger
import org.yakindu.sct.model.sgraph.Vertex
import org.yakindu.sct.model.stext.stext.EventSpec
import org.yakindu.sct.model.stext.stext.LocalReaction
import org.yakindu.sct.model.stext.stext.ReactionTrigger
import org.yakindu.sct.model.stext.stext.TimeEventSpec

class MaxParallelTimersExtension {

	def dispatch int maxParallelTimers(Statechart it) {
		var int maximum = 0

		for (region : regions) {
			maximum += maxParallelTimers(region)
		}

		maximum
	}

	def dispatch int maxParallelTimers(Region it) {
		var int maximum = 0

		for (vertex : vertices) {
			maximum = Math.max(maximum, maxParallelTimers(vertex))
		}

		maximum
	}

	def dispatch int maxParallelTimers(State it) {
		var int maximum = 0;

		for (region : regions) {
			maximum += maxParallelTimers(region)
		}

		for (reaction : localReactions) {
			maximum += maxParallelTimers(reaction)
		}

		for (transition : outgoingTransitions) {
			maximum += maxParallelTimers(transition)
		}

		maximum
	}

	def dispatch int maxParallelTimers(Vertex vertex) {
		0
	}

	def dispatch int maxParallelTimers(Transition it) {
		maxParallelTimers(trigger);
	}

	def dispatch int maxParallelTimers(Reaction reaction) {
		0
	}

	def dispatch int maxParallelTimers(LocalReaction it) {
		maxParallelTimers(trigger)
	}

	def dispatch int maxParallelTimers(Trigger it) {
		0
	}

	def dispatch int maxParallelTimers(ReactionTrigger it) {
		var maximum = 0;

		for (eventSpec : triggers) {
			maximum += maxParallelTimers(eventSpec)
		}

		maximum
	}

	def dispatch int maxParallelTimers(EventSpec it) {
		0
	}

	def dispatch int maxParallelTimers(TimeEventSpec it) {
		1
	}

}
