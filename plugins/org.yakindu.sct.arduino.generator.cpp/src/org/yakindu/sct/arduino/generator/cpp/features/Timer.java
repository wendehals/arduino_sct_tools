/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.features;

public enum Timer {

	ATMEGA168328("atmega168_328", Messages.Timer_ATmega168328_title, Messages.Timer_ATmega168328_description), //$NON-NLS-1$

	SOFTWARE("software", Messages.Timer_software_title, Messages.Timer_software_description); //$NON-NLS-1$

	public final String literal;

	public final String title;

	public final String description;

	private Timer(String literal, String title, String description) {
		this.literal = literal;
		this.title = title;
		this.description = description;
	}

	public static Timer getTimer(String literal) {
		for (final Timer timer : values()) {
			if (timer.literal.equals(literal)) {
				return timer;
			}
		}
		return null;
	}

}
