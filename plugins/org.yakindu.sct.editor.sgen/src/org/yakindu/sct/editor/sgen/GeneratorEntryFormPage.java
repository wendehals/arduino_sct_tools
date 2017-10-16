/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.editor.sgen;

import static org.yakindu.sct.editor.sgen.SGenModelUtil.createFeatureConfiguration;
import static org.yakindu.sct.editor.sgen.SGenModelUtil.createFeatureParameterValue;
import static org.yakindu.sct.editor.sgen.SGenModelUtil.getFeatureConfiguration;
import static org.yakindu.sct.editor.sgen.SGenModelUtil.getFeatureTypes;

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
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.yakindu.base.expressions.expressions.ExpressionsFactory;
import org.yakindu.base.expressions.expressions.FloatLiteral;
import org.yakindu.base.expressions.expressions.PrimitiveValueExpression;
import org.yakindu.sct.editor.sgen.extensions.FeatureConfigurationSectionsExtension;
import org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection;
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

		createSections(toolkit, body);

		startListeningToModelChanges();
	}

	private void createSections(final FormToolkit toolkit, final Composite parent) {
		final Map<String, IFeatureConfigurationSection> featureConfigurationSections = FeatureConfigurationSectionsExtension
				.getFeatureConfigurationSections();

		final Collection<FeatureType> featureTypes = getFeatureTypes(
				GeneratorExtensions.getGeneratorDescriptor(this.generatorId));
		for (final FeatureType featureType : featureTypes) {
			IFeatureConfigurationSection featureConfigurationSection = featureConfigurationSections
					.get(featureType.getName());
			if (featureConfigurationSection == null) {
				featureConfigurationSection = new GenericFeatureConfigurationSection();
			}

			featureConfigurationSection.initialize(this, featureType);
			featureConfigurationSection.createSection(toolkit, parent);
			this.sections.put(featureType.getName(), featureConfigurationSection);
		}

		this.sections.values().forEach(section -> section.modelChanged(getEditor().getXtextDocument()));
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
		action.setImageDescriptor(SGenEditorUIImages.GENERATOR_MODEL.descriptor());
		action.setToolTipText(Messages.SGenMultiPageEditor_generateActionTooltip);

		return action;
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

	public void updateModel(final FeatureParameter parameter, final String value) {
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

						switch (parameter.getParameterType()) {
							case INTEGER:
								try {
									parameterValue.setValue(Integer.parseInt(value));
								} catch (final NumberFormatException exception) {
									parameterValue.setValue(0);
								}
								break;
							case FLOAT:
								final FloatLiteral realLiteral = ExpressionsFactory.eINSTANCE.createFloatLiteral();
								try {
									realLiteral.setValue(Float.parseFloat(value));
								} catch (final NumberFormatException exception) {
									realLiteral.setValue(0);
								}
								final PrimitiveValueExpression expression = ExpressionsFactory.eINSTANCE
										.createPrimitiveValueExpression();
								expression.setValue(realLiteral);
								parameterValue.setExpression(expression);
								break;
							case STRING:
							default:
								parameterValue.setValue(value);
								break;
						}
					}
				});
			}
		});

		startListeningToModelChanges();
	}

	public void updateModel(final FeatureParameter parameter, final boolean value) {
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

	private void startListeningToModelChanges() {
		getEditor().getXtextDocument().addModelListener(this);
	}

	private void stopListeningToModelChanges() {
		final IXtextDocument xtextDocument = getEditor().getXtextDocument();
		if (xtextDocument != null) {
			xtextDocument.removeModelListener(this);
		}
	}

}
