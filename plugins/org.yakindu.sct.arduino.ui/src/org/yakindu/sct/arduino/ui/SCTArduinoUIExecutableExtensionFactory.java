package org.yakindu.sct.arduino.ui;

import org.osgi.framework.Bundle;
import org.yakindu.sct.generator.genmodel.ui.SGenExecutableExtensionFactory;

public class SCTArduinoUIExecutableExtensionFactory extends SGenExecutableExtensionFactory {

	/**
	 * @see org.yakindu.sct.generator.genmodel.ui.SGenExecutableExtensionFactory#getBundle()
	 */
	@Override
	protected Bundle getBundle() {
		return SCTArduinoUIPlugin.getDefault().getBundle();
	}

}
