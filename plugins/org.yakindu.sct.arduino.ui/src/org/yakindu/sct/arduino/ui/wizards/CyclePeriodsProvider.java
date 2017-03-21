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
import java.util.TreeSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;

public class CyclePeriodsProvider extends LabelProvider implements IStructuredContentProvider {

	private final Collection<Integer> cyclePeriods = new TreeSet<>();

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		this.cyclePeriods.clear();
		if (newInput != null) {
			this.cyclePeriods.addAll((Collection<Integer>) newInput);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(final Object element) {
		return ((Integer) element).toString();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(final Object inputElement) {
		return this.cyclePeriods.toArray();
	}

	public int getIndex(final String element) {
		final Object[] array = getElements(null);
		for (int index = 0; index < array.length; index++) {
			if (array[index].toString().equals(element)) {
				return index;
			}
		}
		return -1;
	}

}