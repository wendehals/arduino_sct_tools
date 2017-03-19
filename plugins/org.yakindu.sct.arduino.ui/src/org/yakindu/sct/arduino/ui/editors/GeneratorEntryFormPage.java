/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import static org.yakindu.sct.arduino.ui.editors.SGenModelUtil.createFeatureConfiguration;
import static org.yakindu.sct.arduino.ui.editors.SGenModelUtil.createFeatureParameterValue;
import static org.yakindu.sct.arduino.ui.editors.SGenModelUtil.getFeatureConfiguration;
import static org.yakindu.sct.arduino.ui.editors.SGenModelUtil.getFeatureTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.yakindu.sct.model.sgen.FeatureConfiguration;
import org.yakindu.sct.model.sgen.FeatureParameter;
import org.yakindu.sct.model.sgen.FeatureParameterValue;
import org.yakindu.sct.model.sgen.FeatureType;

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
		scrolledForm.setText(Messages.GeneratorEntryFormPage_formPageHeaderPrefix + getStatechartName());

		final Form form = scrolledForm.getForm();
		toolkit.decorateFormHeading(form);

		final IToolBarManager toolBarManager = form.getToolBarManager();
		toolBarManager.add(createGenerateAction());
		form.updateToolBar();

		final Composite body = scrolledForm.getBody();
		body.setLayout(new TableWrapLayout());

		final Collection<FeatureType> featureTypes = getFeatureTypes(
				GeneratorExtensions.getGeneratorDescriptor(this.generatorId));
		final Collection<FeatureConfiguration> featureConfigurations = getFeatureConfigurations();

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

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#getEditor()
	 */
	@Override
	public SGenMultiPageEditor getEditor() {
		return (SGenMultiPageEditor) super.getEditor();
	}

	public String getStatechartName() {
		return this.statechartName;
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
						FeatureConfiguration featureConfiguration = getFeatureConfiguration(resource,
								getStatechartName(), parameter.getFeatureType());
						if (featureConfiguration == null) {
							featureConfiguration = createFeatureConfiguration(resource, getStatechartName(),
									parameter.getFeatureType());
						}
						FeatureParameterValue parameterValue = featureConfiguration
								.getParameterValue(parameter.getName());
						if (parameterValue == null) {
							parameterValue = createFeatureParameterValue(resource, featureConfiguration, parameter);
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
						FeatureConfiguration featureConfiguration = getFeatureConfiguration(resource,
								getStatechartName(), parameter.getFeatureType());
						if (featureConfiguration == null) {
							featureConfiguration = createFeatureConfiguration(resource, getStatechartName(),
									parameter.getFeatureType());
						}
						FeatureParameterValue parameterValue = featureConfiguration
								.getParameterValue(parameter.getName());
						if (parameterValue == null) {
							parameterValue = createFeatureParameterValue(resource, featureConfiguration, parameter);
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

	private Collection<FeatureConfiguration> getFeatureConfigurations() {
		final IXtextDocument xtextDocument = getEditor().getXtextDocument();
		return xtextDocument.readOnly(new IUnitOfWork<Collection<FeatureConfiguration>, XtextResource>() {
			@Override
			public Collection<FeatureConfiguration> exec(final XtextResource resource) throws Exception {
				return SGenModelUtil.getFeatureConfigurations(resource,
						GeneratorEntryFormPage.this.getStatechartName());
			}
		});
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
