/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.wizards;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.yakindu.sct.arduino.generator.cpp.extensions.AbstractNamedElement;

public class NamedExtensionElementsProvider extends LabelProvider implements IStructuredContentProvider {

	private final Collection<AbstractNamedElement> namedExtensionElements = new TreeSet<>(
			new Comparator<AbstractNamedElement>() {

				/**
				 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
				 */
				@Override
				public int compare(AbstractNamedElement element1, AbstractNamedElement element2) {
					return element1.getName().compareTo(element2.getName());
				}
			});

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.namedExtensionElements.clear();
		if (newInput != null) {
			this.namedExtensionElements.addAll((Collection<AbstractNamedElement>) newInput);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		return ((AbstractNamedElement) element).getName();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public AbstractNamedElement[] getElements(Object inputElement) {
		return this.namedExtensionElements.toArray(new AbstractNamedElement[this.namedExtensionElements.size()]);
	}

	public int getIndex(String id) {
		final AbstractNamedElement[] elements = getElements(null);
		for (int i = 0; i < this.namedExtensionElements.size(); i++) {
			final AbstractNamedElement namedElement = elements[i];
			if (namedElement.getId().equals(id)) {
				return i;
			}
		}

		return -1;
	}

}