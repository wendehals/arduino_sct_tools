package org.yakindu.sct.arduino.ui.editors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.yakindu.sct.arduino.ui.editors.messages"; //$NON-NLS-1$

	public static String ArduinoFeatureConfigurationSection_architectureLabel;
	public static String ArduinoFeatureConfigurationSection_cyclePeriodLabel;
	public static String ArduinoFeatureConfigurationSection_cyclePeriodToolTip;
	public static String ArduinoFeatureConfigurationSection_timerLabel;
	public static String ArduinoFeatureConfigurationSection_userSrcFolderLabel;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
