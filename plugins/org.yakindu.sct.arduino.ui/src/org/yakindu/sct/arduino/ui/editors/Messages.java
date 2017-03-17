package org.yakindu.sct.arduino.ui.editors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.yakindu.sct.arduino.ui.editors.messages"; //$NON-NLS-1$

	public static String ArduinoSGenEditor_sourcePageText;
	public static String ArduinoSGenFormPage_architectureLabel;
	public static String ArduinoSGenFormPage_cyclePeriodLabel;
	public static String ArduinoSGenFormPage_cyclePeriodToolTip;
	public static String ArduinoSGenFormPage_formHeader;
	public static String ArduinoSGenFormPage_formPageName;
	public static String ArduinoSGenFormPage_generateActionTooltip;
	public static String ArduinoSGenFormPage_timerLabel;
	public static String ArduinoSGenFormPage_timerSectionHeader;
	public static String GeneratorEntryFormPage_formPageHeaderPrefix;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
