package org.yakindu.sct.arduino.generator.cpp;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class ArduinoGeneratorPlugin extends Plugin {

	/**
	 * The shared instance
	 */
	private static ArduinoGeneratorPlugin plugin;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		plugin = this;
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		plugin = null;
		super.stop(bundleContext);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ArduinoGeneratorPlugin getDefault() {
		return plugin;
	}

	/**
	 * @return the plugin's id.
	 */
	public static String getPluginId() {
		return plugin.getBundle().getSymbolicName();
	}

	/**
	 * Logs an exception.
	 *
	 * @param exception
	 *            the exception that caused that error.
	 * @return the {@link IStatus} that was logged.
	 */
	public static IStatus logError(final Throwable exception) {
		final Status status = new Status(IStatus.ERROR, getPluginId(), exception.getLocalizedMessage(), exception);
		plugin.getLog().log(status);

		return status;
	}

}
