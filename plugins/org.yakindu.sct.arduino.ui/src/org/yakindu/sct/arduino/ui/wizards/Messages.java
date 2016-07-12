/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.yakindu.sct.arduino.ui.wizards.messages"; //$NON-NLS-1$

	public static String ArduinoSCTWizardPage_description;
	public static String ArduinoSCTWizardPage_genSrcFolderLabel;
	public static String ArduinoSCTWizardPage_missingStatechartNameMessage;
	public static String ArduinoSCTWizardPage_srcFolderLabel;
	public static String ArduinoSCTWizardPage_statechartLabel;
	public static String ArduinoSCTWizardPage_timerLabel;

	public static String NewArduinoSCTProjectWizard_description;

	public static String NewArduinoSCTProjectWizard_title;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
