/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class SCTArduinoPlugin extends Plugin {

	/**
	 * The shared instance
	 */
	private static SCTArduinoPlugin plugin;

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
	public static SCTArduinoPlugin getDefault() {
		return plugin;
	}

	/**
	 * @return the plugin's id.
	 */
	public static String getPluginId() {
		return plugin.getBundle().getSymbolicName();
	}

}
