/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

public class Log {

	/**
	 * Logs an info.
	 *
	 * @param message
	 *            the message to be displayed.
	 * @return the {@link IStatus} that was logged.
	 */
	public static IStatus logInfo(final Plugin plugin, final String message) {
		final Status status = new Status(IStatus.INFO, plugin.getBundle().getSymbolicName(), message);
		plugin.getLog().log(status);

		return status;
	}

	/**
	 * Logs a warning.
	 *
	 * @param message
	 *            the message to be displayed.
	 * @return the {@link IStatus} that was logged.
	 */
	public static IStatus logWarning(final Plugin plugin, final String message) {
		final Status status = new Status(IStatus.WARNING, plugin.getBundle().getSymbolicName(), message);
		plugin.getLog().log(status);

		return status;
	}

	/**
	 * Logs an error.
	 *
	 * @param message
	 *            the message to be displayed.
	 * @return the {@link IStatus} that was logged.
	 */
	public static IStatus logError(final Plugin plugin, final String message) {
		final Status status = new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(), message);
		plugin.getLog().log(status);

		return status;
	}

	/**
	 * Logs an exception.
	 *
	 * @param exception
	 *            the exception that caused that error.
	 * @return the {@link IStatus} that was logged.
	 */
	public static IStatus logError(final Plugin plugin, final Throwable exception) {
		final Status status = new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(),
				exception.getLocalizedMessage(), exception);
		plugin.getLog().log(status);

		return status;
	}

	/**
	 * Logs an error caused by an exception.
	 *
	 * @param message
	 *            the message to be displayed.
	 * @param exception
	 *            the exception that caused that error.
	 * @return the {@link IStatus} that was logged.
	 */
	public static IStatus logError(final Plugin plugin, final String message, final Throwable exception) {
		final Status status = new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(), message, exception);
		plugin.getLog().log(status);

		return status;
	}

	/**
	 * Logs a status.
	 *
	 * @param status
	 *            the status representing the error.
	 */
	public static void logStatus(final Plugin plugin, final IStatus status) {
		plugin.getLog().log(status);
	}

}
