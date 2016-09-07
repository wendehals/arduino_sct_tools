/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers;

public class Timer extends AbstractNamedExtensionElement {

	private final String description;

	private final long minCyclePeriod;

	private final long maxCyclePeriod;

	private final AbstractTimer codeGenerator;

	public Timer(String id, String name, String description, long minCyclePeriod, long maxCyclePeriod,
			AbstractTimer codeGenerator) {
		super(id, name);
		this.description = description;
		this.minCyclePeriod = minCyclePeriod;
		this.maxCyclePeriod = maxCyclePeriod;
		this.codeGenerator = codeGenerator;
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

}