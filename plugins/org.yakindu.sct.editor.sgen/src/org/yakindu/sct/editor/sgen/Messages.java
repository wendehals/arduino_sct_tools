package org.yakindu.sct.editor.sgen;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.yakindu.sct.editor.sgen.messages"; //$NON-NLS-1$

	public static String SGenMultiPageEditor_generateActionTooltip;
	public static String SGenMultiPageEditor_sourcePageText;
	public static String GeneratorEntryFormPage_formPageHeaderPrefix;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
