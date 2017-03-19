/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.yakindu.sct.arduino.ui.SCTArduinoUIImages;
import org.yakindu.sct.generator.builder.action.GenerateModelAction;
import org.yakindu.sct.generator.core.extensions.GeneratorExtensions;
import org.yakindu.sct.generator.core.extensions.IGeneratorDescriptor;
import org.yakindu.sct.generator.core.extensions.ILibraryDescriptor;
import org.yakindu.sct.generator.core.extensions.LibraryExtensions;
import org.yakindu.sct.model.sgen.FeatureConfiguration;
import org.yakindu.sct.model.sgen.FeatureParameter;
import org.yakindu.sct.model.sgen.FeatureParameterValue;
import org.yakindu.sct.model.sgen.FeatureType;
import org.yakindu.sct.model.sgen.FeatureTypeLibrary;
import org.yakindu.sct.model.sgen.GeneratorEntry;
import org.yakindu.sct.model.sgen.GeneratorModel;
import org.yakindu.sct.model.sgen.SGenFactory;
import org.yakindu.sct.model.sgraph.Statechart;

import com.google.common.collect.Lists;

public class GeneratorEntryFormPage extends FormPage implements IXtextModelListener {

	private final String generatorId;

	private final String statechartName;

	protected final Map<String, IFeatureConfigurationSection> sections = new HashMap<>();

	public GeneratorEntryFormPage(final SGenMultiPageEditor editor, final String generatorId,
			final String statechartName) {
		super(editor, "org.yakindu.sct.arduino.ui.editors.GeneratorEntryFormPage", statechartName); //$NON-NLS-1$
		this.generatorId = generatorId;
		this.statechartName = statechartName;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		final FormToolkit toolkit = managedForm.getToolkit();

		final ScrolledForm scrolledForm = managedForm.getForm();
		scrolledForm.setText(Messages.GeneratorEntryFormPage_formPageHeaderPrefix + this.statechartName);

		final Form form = scrolledForm.getForm();
		toolkit.decorateFormHeading(form);

		final IToolBarManager toolBarManager = form.getToolBarManager();
		toolBarManager.add(createGenerateAction());
		form.updateToolBar();

		final Composite body = scrolledForm.getBody();
		body.setLayout(new TableWrapLayout());

		final Collection<FeatureType> featureTypes = getFeatureTypes(
				GeneratorExtensions.getGeneratorDescriptor(this.generatorId));
		final Collection<FeatureConfiguration> featureConfigurations = getFeatureConfigurations(
				getEditor().getXtextDocument(), this.statechartName);

		for (final FeatureType featureType : featureTypes) {
			final IFeatureConfigurationSection featureConfigurationSection = new GenericFeatureConfigurationSection(
					this, featureType);
			final Section section = featureConfigurationSection.createSection(toolkit, body);
			if (configurationsContainsType(featureConfigurations, featureType)) {
				section.setExpanded(true);
			}
			this.sections.put(featureType.getName(), featureConfigurationSection);
		}

		initialize();
		startListeningToModelChanges();
	}

	private static List<FeatureType> getFeatureTypes(final IGeneratorDescriptor descriptor) {
		final ArrayList<FeatureType> featureTypes = Lists.newArrayList();
		final Iterable<ILibraryDescriptor> libraryDescriptors = LibraryExtensions
				.getLibraryDescriptors(descriptor.getLibraryIDs());
		for (final ILibraryDescriptor libraryDescriptor : libraryDescriptors) {
			final ResourceSet set = new ResourceSetImpl();
			final Resource resource = set.getResource(libraryDescriptor.getURI(), true);
			final FeatureTypeLibrary featureTypeLibrary = (FeatureTypeLibrary) resource.getContents().get(0);
			featureTypes.addAll(featureTypeLibrary.getTypes());
		}

		return featureTypes;
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#getEditor()
	 */
	@Override
	public SGenMultiPageEditor getEditor() {
		return (SGenMultiPageEditor) super.getEditor();
	}

	/**
	 * @see org.eclipse.xtext.ui.editor.model.IXtextModelListener#modelChanged(org.eclipse.xtext.resource.XtextResource)
	 */
	@Override
	public void modelChanged(final XtextResource resource) {
		Display.getDefault().asyncExec(new Runnable() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				updateSectionsVisibility();
				GeneratorEntryFormPage.this.sections.values()
						.forEach(section -> section.modelChanged(getEditor().getXtextDocument()));
			}
		});
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose() {
		this.sections.values().forEach(section -> section.dispose());
		stopListeningToModelChanges();
		super.dispose();
	}

	private void initialize() {
		this.sections.values().forEach(section -> section.initialize(getEditor().getXtextDocument()));
	}

	protected void updateModel(final FeatureParameter parameter, final String value) {
		stopListeningToModelChanges();

		Display.getDefault().syncExec(new Runnable() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				final IXtextDocument xtextDocument = getEditor().getXtextDocument();
				xtextDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					/**
					 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork.Void#process(java.lang.Object)
					 */
					@Override
					public void process(final XtextResource resource) throws Exception {
						FeatureParameterValue parameterValue = findFeatureParameterValue(resource, parameter);
						if (parameterValue == null) {
							parameterValue = createFeatureParameterValue(resource, parameter);
						}
						parameterValue.setValue(value);
					}
				});
			}
		});

		startListeningToModelChanges();
	}

	protected void updateModel(final FeatureParameter parameter, final boolean value) {
		stopListeningToModelChanges();

		Display.getDefault().syncExec(new Runnable() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				final IXtextDocument xtextDocument = getEditor().getXtextDocument();
				xtextDocument.modify(new IUnitOfWork.Void<XtextResource>() {
					/**
					 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork.Void#process(java.lang.Object)
					 */
					@Override
					public void process(final XtextResource resource) throws Exception {
						FeatureParameterValue parameterValue = findFeatureParameterValue(resource, parameter);
						if (parameterValue == null) {
							parameterValue = createFeatureParameterValue(resource, parameter);
						}
						parameterValue.setValue(value);
					}
				});
			}
		});

		startListeningToModelChanges();
	}

	protected void startListeningToModelChanges() {
		getEditor().getXtextDocument().addModelListener(this);
	}

	protected void stopListeningToModelChanges() {
		final IXtextDocument xtextDocument = getEditor().getXtextDocument();
		if (xtextDocument != null) {
			xtextDocument.removeModelListener(this);
		}
	}

	protected static Collection<FeatureConfiguration> getFeatureConfigurations(final IXtextDocument xtextDocument,
			final String statechartName) {
		return xtextDocument.readOnly(new IUnitOfWork<Collection<FeatureConfiguration>, XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork#exec(java.lang.Object)
			 */
			@Override
			public Collection<FeatureConfiguration> exec(final XtextResource resource) throws Exception {
				final GeneratorEntry generatorEntry = getGeneratorEntry(resource, statechartName);
				if (generatorEntry != null) {
					return generatorEntry.getFeatures();
				}

				return Collections.emptyList();
			}
		});
	}

	protected static String getStringParameterValue(final IXtextDocument xtextDocument,
			final FeatureParameter parameter) {
		return xtextDocument.readOnly(new IUnitOfWork<String, XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork#exec(java.lang.Object)
			 */
			@Override
			public String exec(final XtextResource resource) throws Exception {
				final FeatureParameterValue parameterValue = findFeatureParameterValue(resource, parameter);
				if (parameterValue != null) {
					return parameterValue.getStringValue();
				}
				return ""; //$NON-NLS-1$
			}
		});
	}

	protected static boolean getBooleanParameterValue(final IXtextDocument xtextDocument,
			final FeatureParameter parameter) {
		return xtextDocument.readOnly(new IUnitOfWork<Boolean, XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork#exec(java.lang.Object)
			 */
			@Override
			public Boolean exec(final XtextResource resource) throws Exception {
				final FeatureParameterValue parameterValue = findFeatureParameterValue(resource, parameter);
				if (parameterValue != null) {
					return Boolean.valueOf(parameterValue.getBooleanValue());
				}
				return Boolean.FALSE;
			}
		});
	}

	protected static GeneratorEntry getGeneratorEntry(final XtextResource resource, final String name) {
		final GeneratorModel generatorModel = (GeneratorModel) resource.getContents().get(0);
		for (final GeneratorEntry generatorEntry : generatorModel.getEntries()) {
			if (generatorEntry.getElementRef() instanceof Statechart) {
				final Statechart statechart = (Statechart) generatorEntry.getElementRef();
				if (name.equals(statechart.getName())) {
					return generatorEntry;
				}
			}
		}

		return null;
	}

	protected static FeatureParameterValue findFeatureParameterValue(final XtextResource resource,
			final FeatureParameter parameter) {
		final GeneratorModel generatorModel = (GeneratorModel) resource.getParseResult().getRootASTElement();
		final TreeIterator<EObject> iterator = generatorModel.eResource().getAllContents();
		while (iterator.hasNext()) {
			final EObject eObject = iterator.next();
			if (eObject instanceof FeatureConfiguration) {
				final FeatureConfiguration featureConfiguration = (FeatureConfiguration) eObject;
				if (parameter.getFeatureType().equals(featureConfiguration.getType())) {
					for (final FeatureParameterValue parameterValue : featureConfiguration.getParameterValues()) {
						if (parameter.equals(parameterValue.getParameter())) {
							return parameterValue;
						}
					}
				}
			}
		}

		return null;
	}

	protected static FeatureParameterValue createFeatureParameterValue(final XtextResource resource,
			final FeatureParameter parameter) {
		final GeneratorModel generatorModel = (GeneratorModel) resource.getParseResult().getRootASTElement();
		final TreeIterator<EObject> iterator = generatorModel.eResource().getAllContents();
		while (iterator.hasNext()) {
			final EObject eObject = iterator.next();
			if (eObject instanceof FeatureConfiguration) {
				final FeatureConfiguration featureConfiguration = (FeatureConfiguration) eObject;
				if (parameter.getFeatureType().equals(featureConfiguration.getType())) {
					final FeatureParameterValue parameterValue = SGenFactory.eINSTANCE.createFeatureParameterValue();
					parameterValue.setParameter(parameter);
					featureConfiguration.getParameterValues().add(parameterValue);

					return parameterValue;
				}
			}
		}

		return null;
	}

	protected void updateSectionsVisibility() {

	}

	private IAction createGenerateAction() {
		final IAction action = new Action() {
			/**
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				final GenerateModelAction generateModelAction = new GenerateModelAction();
				final ISelection selection = new StructuredSelection(
						getEditor().getEditorInput().getAdapter(IFile.class));
				generateModelAction.selectionChanged(this, selection);
				generateModelAction.run(this);
			}
		};
		action.setImageDescriptor(SCTArduinoUIImages.GENERATOR_MODEL.descriptor());
		action.setToolTipText(Messages.ArduinoSGenFormPage_generateActionTooltip);

		return action;
	}

	private boolean configurationsContainsType(final Collection<FeatureConfiguration> featureConfigurations,
			final FeatureType featureType) {
		for (final FeatureConfiguration featureConfiguration : featureConfigurations) {
			if (featureConfiguration.getType().getName().equals(featureType.getName())) {
				return true;
			}
		}
		return false;
	}

}
