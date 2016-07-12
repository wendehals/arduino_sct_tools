package org.yakindu.sct.arduino.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.yakindu.sct.arduino.core.messages"; //$NON-NLS-1$

	public static String ArduinoSCTProjectGenerator_creatingDiagram;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
