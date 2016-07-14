/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicDispatcher<T> {

	private static final String INTERNAL_DISPATCH_METHOD = "internalDispatch"; //$NON-NLS-1$

	private final List<Class<?>> precedenceList;

	private final Map<Class<?>, Method> dispatchMethodsCache;

	public DynamicDispatcher(final Class<?>... precedenceClasses) {
		this.precedenceList = new ArrayList<Class<?>>();
		for (final Class<?> clazz : precedenceClasses) {
			this.precedenceList.add(clazz);
		}

		this.dispatchMethodsCache = discoverDispatchMethods(this.getClass());
	}

	@SuppressWarnings("unchecked")
	public T dispatch(final Object object) {
		final List<Class<?>> hierarchy = new ArrayList<Class<?>>();
		discoverHierarchy(object.getClass(), hierarchy);

		final List<Class<?>> sortedClasses = sortClasses(hierarchy);
		for (final Class<?> clazz : sortedClasses) {
			final Method dispatchMethod = this.dispatchMethodsCache.get(clazz);
			if (dispatchMethod != null) {
				try {
					final T result = (T) dispatchMethod.invoke(this, object);
					if (result != null) {
						return result;
					}
				} catch (final Exception exception) {
					ArduinoGeneratorPlugin.logError(exception);
				}
			}
		}

		return null;
	}

	protected T internalDispatch(final Object object) {
		return null;
	}

	private Map<Class<?>, Method> discoverDispatchMethods(final Class<?> dispatcherClass) {
		final Map<Class<?>, Method> dispatchMethods = new HashMap<Class<?>, Method>();

		final List<Class<?>> hierarchy = new ArrayList<Class<?>>();
		discoverHierarchy(dispatcherClass, hierarchy);

		// get all dispatch methods from clazz
		for (final Class<?> superClass : hierarchy) {
			for (final Method method : superClass.getDeclaredMethods()) {
				if (INTERNAL_DISPATCH_METHOD.equals(method.getName()) && (method.getParameterTypes().length == 1)) {
					dispatchMethods.put(method.getParameterTypes()[0], method);
				}
			}
		}

		return dispatchMethods;
	}

	private void discoverHierarchy(final Class<?> clazz, final List<Class<?>> hierarchy) {
		hierarchy.add(clazz);

		for (final Class<?> intface : clazz.getInterfaces()) {
			discoverHierarchy(intface, hierarchy);
		}

		if (clazz.getSuperclass() != null) {
			discoverHierarchy(clazz.getSuperclass(), hierarchy);
		}
	}

	protected List<Class<?>> sortClasses(final List<Class<?>> unsortedClasses) {
		final List<Class<?>> sortedClasses = new ArrayList<Class<?>>(unsortedClasses.size());
		final List<Class<?>> tmpClasses = new ArrayList<Class<?>>(unsortedClasses);

		// first copy the classes from the unsorted to the sorted list by the precedence list's order
		for (final Class<?> classifier : this.precedenceList) {
			if (tmpClasses.contains(classifier)) {
				sortedClasses.add(classifier);
				tmpClasses.remove(classifier);
			}
		}

		// then copy all left classes from unsorted to sorted list
		for (final Class<?> classifier : tmpClasses) {
			sortedClasses.add(classifier);
		}

		return sortedClasses;
	}

}
