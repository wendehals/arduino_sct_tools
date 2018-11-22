/**
 * Copyright (c) 2018 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.wizards;

import org.yakindu.sct.model.stext.lib.StatechartAnnotations;
import org.yakindu.sct.ui.editor.wizards.DefaultModelCreator;

public class ArduinoModelCreator extends DefaultModelCreator {

	private final int cyclePeriod;

	public ArduinoModelCreator(final int cyclePeriod) {
		this.cyclePeriod = cyclePeriod;
	}

	@Override
	protected String getInitialSpecification() {
		return "@" + StatechartAnnotations.CYCLE_BASED_ANNOTATION + "(" + this.cyclePeriod + ")\n" //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				+ "\n" //$NON-NLS-1$
				+ "interface:\n" //$NON-NLS-1$
				+ "// Define events and variables here. \n" //$NON-NLS-1$
				+ "// Use CTRL+Space for content assist."; //$NON-NLS-1$
	}

}
