/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Image registry to provide shared images.
 *
 * @author Lothar Wendehals
 */
public enum SCTArduinoUIImages {

	GENERATOR_MODEL("icons/GeneratorModel.gif"); //$NON-NLS-1$

	private final String path;

	private SCTArduinoUIImages(final String path) {
		this.path = path;
	}

	/**
	 * Returns an image. Clients do not need to dispose the image, it will be disposed automatically.
	 *
	 * @return an {@link Image}
	 */
	public Image image() {
		final ImageRegistry imageRegistry = SCTArduinoUIPlugin.getDefault().getImageRegistry();
		Image image = imageRegistry.get(this.path);
		if (image == null) {
			addImageDescriptor();
			image = imageRegistry.get(this.path);
		}

		return image;
	}

	/**
	 * Returns an image descriptor.
	 *
	 * @return an {@link ImageDescriptor}
	 */
	public ImageDescriptor descriptor() {
		final ImageRegistry imageRegistry = SCTArduinoUIPlugin.getDefault().getImageRegistry();
		ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(this.path);
		if (imageDescriptor == null) {
			addImageDescriptor();
			imageDescriptor = imageRegistry.getDescriptor(this.path);
		}

		return imageDescriptor;
	}

	private void addImageDescriptor() {
		final SCTArduinoUIPlugin plugin = SCTArduinoUIPlugin.getDefault();
		final ImageDescriptor id = ImageDescriptor.createFromURL(plugin.getBundle().getEntry(this.path));
		plugin.getImageRegistry().put(this.path, id);
	}

}
