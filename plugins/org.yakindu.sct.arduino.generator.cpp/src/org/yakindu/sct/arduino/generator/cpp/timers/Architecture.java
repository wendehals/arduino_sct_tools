/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Architecture extends AbstractNamedExtensionElement {

	final Collection<Timer> timers = new HashSet<>();

	public Architecture(String id, String name) {
		super(id, name);
	}

	protected void addTimer(Timer timer) {
		this.timers.add(timer);
	}

	public Collection<Timer> getTimers() {
		return Collections.unmodifiableCollection(this.timers);
	}

}