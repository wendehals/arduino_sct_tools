/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.extensions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class ArchitectureElement extends AbstractNamedElement {

	final Collection<TimerElement> timers = new HashSet<>();

	public ArchitectureElement(String id, String name) {
		super(id, name);
	}

	protected void addTimer(TimerElement timer) {
		this.timers.add(timer);
	}

	public Collection<TimerElement> getTimers() {
		return Collections.unmodifiableCollection(this.timers);
	}

}