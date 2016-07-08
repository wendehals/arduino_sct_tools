package org.yakindu.sct.arduino.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SCTArduinoUIPlugin extends AbstractUIPlugin {

	/**
	 * The shared instance
	 */
	private static SCTArduinoUIPlugin plugin;

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SCTArduinoUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * @return the plugin's id.
	 */
	public static String getPluginId() {
		return plugin.getBundle().getSymbolicName();
	}

}
