package org.yakindu.sct.arduino.generator.cpp.features;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.yakindu.sct.arduino.generator.cpp.features.messages"; //$NON-NLS-1$
	public static String Timer_ATmega168328_description;
	public static String Timer_ATmega168328_title;
	public static String Timer_software_description;
	public static String Timer_software_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
