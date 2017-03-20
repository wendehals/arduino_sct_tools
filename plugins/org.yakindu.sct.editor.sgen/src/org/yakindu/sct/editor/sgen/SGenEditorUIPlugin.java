package org.yakindu.sct.editor.sgen;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SGenEditorUIPlugin extends AbstractUIPlugin {

	/**
	 * The shared instance
	 */
	private static SGenEditorUIPlugin plugin;

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
	public static SGenEditorUIPlugin getDefault() {
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
