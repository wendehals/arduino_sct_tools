package org.yakindu.sct.arduino.ui.editors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.yakindu.sct.arduino.ui.editors.messages"; //$NON-NLS-1$

	public static String ArduinoSGenEditor_formPageText;
	public static String ArduinoSGenEditor_xtextPageText;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
