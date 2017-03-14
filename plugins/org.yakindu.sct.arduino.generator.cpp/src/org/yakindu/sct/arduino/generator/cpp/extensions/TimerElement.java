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
import java.util.TreeSet;

import org.yakindu.sct.arduino.generator.cpp.timers.AbstractTimer;

public class TimerElement extends AbstractNamedElement {

	private final ArchitectureElement architecture;

	private final String description;

	private final long minCyclePeriod;

	private final long maxCyclePeriod;

	private final Collection<Integer> preDefinedCyclePeriods = new TreeSet<>();

	private final AbstractTimer codeGenerator;

	public TimerElement(ArchitectureElement architecture, String id, String name, String description,
			long minCyclePeriod, long maxCyclePeriod, AbstractTimer codeGenerator,
			Collection<Integer> preDefinedCyclePeriods) {
		super(id, name);
		this.architecture = architecture;
		this.description = description;
		this.minCyclePeriod = minCyclePeriod;
		this.maxCyclePeriod = maxCyclePeriod;
		this.codeGenerator = codeGenerator;
		this.preDefinedCyclePeriods.addAll(preDefinedCyclePeriods);
	}

	public ArchitectureElement getArchitecture() {
		return this.architecture;
	}

	public String getDescription() {
		return this.description;
	}

	public long getMinCyclePeriod() {
		return this.minCyclePeriod;
	}

	public long getMaxCyclePeriod() {
		return this.maxCyclePeriod;
	}

	public AbstractTimer getCodeGenerator() {
		return this.codeGenerator;
	}

	public boolean isValidCyclePeriod(long cyclePeriod) {
		return (cyclePeriod >= this.minCyclePeriod) && (cyclePeriod <= this.maxCyclePeriod);
	}

	public Collection<Integer> getPreDefinedCyclePeriods() {
		return Collections.unmodifiableCollection(this.preDefinedCyclePeriods);
	}

}