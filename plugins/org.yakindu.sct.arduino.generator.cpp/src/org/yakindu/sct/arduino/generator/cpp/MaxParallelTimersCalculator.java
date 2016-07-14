/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp;

import org.yakindu.sct.model.sgraph.Reaction;
import org.yakindu.sct.model.sgraph.Region;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Trigger;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.EventSpec;
import org.yakindu.sct.model.stext.stext.LocalReaction;
import org.yakindu.sct.model.stext.stext.ReactionTrigger;
import org.yakindu.sct.model.stext.stext.TimeEventSpec;

public class MaxParallelTimersCalculator extends DynamicDispatcher<Integer> {

	public static int calculate(Statechart statechart) {
		final MaxParallelTimersCalculator calculator = new MaxParallelTimersCalculator();
		return calculator.internalDispatch(statechart);
	}

	protected int internalDispatch(Statechart statechart) {
		int maximum = 0;

		for (final Region region : statechart.getRegions()) {
			maximum += dispatch(region).intValue();
		}

		return maximum;
	}

	protected Integer internalDispatch(Region region) {
		int maximum = 0;

		for (final Vertex vertex : region.getVertices()) {
			maximum = Math.max(maximum, dispatch(vertex).intValue());
		}

		return Integer.valueOf(maximum);
	}

	protected Integer internalDispatch(State state) {
		int maximum = 0;

		for (final Region region : state.getRegions()) {
			maximum += dispatch(region).intValue();
		}

		for (final Reaction reaction : state.getLocalReactions()) {
			maximum += dispatch(reaction).intValue();
		}

		for (final Transition transition : state.getOutgoingTransitions()) {
			maximum += dispatch(transition).intValue();
		}

		return Integer.valueOf(maximum);
	}

	protected Integer internalDispatch(Vertex vertex) {
		return Integer.valueOf(0);
	}

	protected Integer internalDispatch(Transition transition) {
		return dispatch(transition.getTrigger());
	}

	protected Integer internalDispatch(Reaction reaction) {
		return Integer.valueOf(0);
	}

	protected Integer internalDispatch(LocalReaction localReaction) {
		return dispatch(localReaction.getTrigger());
	}

	protected Integer internalDispatch(Trigger trigger) {
		return Integer.valueOf(0);
	}

	protected Integer internalDispatch(ReactionTrigger reactionTrigger) {
		int maximum = 0;

		for (final EventSpec eventSpec : reactionTrigger.getTriggers()) {
			maximum += dispatch(eventSpec).intValue();
		}

		return maximum;
	}

	protected Integer internalDispatch(EventSpec eventSpec) {
		return Integer.valueOf(0);
	}

	protected Integer internalDispatch(TimeEventSpec timeEventSpec) {
		return Integer.valueOf(1);
	}

}
