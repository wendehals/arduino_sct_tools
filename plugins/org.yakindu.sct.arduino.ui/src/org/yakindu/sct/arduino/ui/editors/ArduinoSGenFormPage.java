/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.yakindu.sct.arduino.generator.cpp.extensions.ArchitectureElement;
import org.yakindu.sct.arduino.generator.cpp.extensions.ArchitecturesExtension;
import org.yakindu.sct.arduino.generator.cpp.extensions.TimerElement;
import org.yakindu.sct.arduino.generator.cpp.features.IArduinoFeatureConstants;
import org.yakindu.sct.arduino.ui.SCTArduinoUIImages;
import org.yakindu.sct.arduino.ui.wizards.CyclePeriodsProvider;
import org.yakindu.sct.arduino.ui.wizards.NamedExtensionElementsProvider;
import org.yakindu.sct.generator.builder.action.GenerateModelAction;
import org.yakindu.sct.model.sgen.FeatureConfiguration;
import org.yakindu.sct.model.sgen.FeatureParameterValue;
import org.yakindu.sct.model.sgen.GeneratorModel;

public class ArduinoSGenFormPage extends FormPage
		implements ModifyListener, ISelectionChangedListener, IXtextModelListener {

	private Text timerImplDescText;
	private ComboViewer architectureViewer;
	private ComboViewer timerViewer;
	private ComboViewer cyclePeriodViewer;
	private StackLayout cyclePeriodLayout;
	private Composite cyclePeriodComposite;
	private Text cyclePeriodText;

	public ArduinoSGenFormPage(final SGenMultiPageEditor editor) {
		super(editor, "org.yakindu.sct.arduino.ui.editors.ArduinoSGenFormPage", //$NON-NLS-1$
				Messages.ArduinoSGenFormPage_formPageName);
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		final FormToolkit toolkit = managedForm.getToolkit();

		final ScrolledForm scrolledForm = managedForm.getForm();
		scrolledForm.setText(Messages.ArduinoSGenFormPage_formHeader);

		final Form form = scrolledForm.getForm();
		toolkit.decorateFormHeading(form);

		final IToolBarManager toolBarManager = form.getToolBarManager();
		toolBarManager.add(createGenerateAction());
		form.updateToolBar();

		final Composite body = scrolledForm.getBody();
		body.setLayout(new TableWrapLayout());

		final Section section = toolkit.createSection(body, ExpandableComposite.TITLE_BAR);
		section.setText(Messages.ArduinoSGenFormPage_timerSectionHeader);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		final Composite composite = toolkit.createComposite(section);
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		section.setClient(composite);

		Label label = toolkit.createLabel(composite, Messages.ArduinoSGenFormPage_architectureLabel);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.architectureViewer = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		Combo combo = this.architectureViewer.getCombo();
		toolkit.adapt(combo);
		combo.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		NamedExtensionElementsProvider provider = new NamedExtensionElementsProvider();
		this.architectureViewer.setContentProvider(provider);
		this.architectureViewer.setLabelProvider(provider);
		this.architectureViewer.addSelectionChangedListener(this);

		label = toolkit.createLabel(composite, ""); //$NON-NLS-1$
		label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

		label = toolkit.createLabel(composite, Messages.ArduinoSGenFormPage_timerLabel);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.timerViewer = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo = this.timerViewer.getCombo();
		toolkit.adapt(combo);
		combo.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		provider = new NamedExtensionElementsProvider();
		this.timerViewer.setContentProvider(provider);
		this.timerViewer.setLabelProvider(provider);
		this.timerViewer.addSelectionChangedListener(this);

		label = toolkit.createLabel(composite, ""); //$NON-NLS-1$
		label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

		label = toolkit.createLabel(composite, Messages.ArduinoSGenFormPage_cyclePeriodLabel);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.cyclePeriodLayout = new StackLayout();

		this.cyclePeriodComposite = new Composite(composite, SWT.NONE);
		this.cyclePeriodComposite.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));
		this.cyclePeriodComposite.setLayout(this.cyclePeriodLayout);

		this.cyclePeriodText = toolkit.createText(this.cyclePeriodComposite, "", SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		this.cyclePeriodText.setToolTipText(Messages.ArduinoSGenFormPage_cyclePeriodToolTip);
		this.cyclePeriodText.addModifyListener(this);

		this.cyclePeriodViewer = new ComboViewer(this.cyclePeriodComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo = this.cyclePeriodViewer.getCombo();
		toolkit.adapt(combo);

		final CyclePeriodsProvider cyclePeriodsProvider = new CyclePeriodsProvider();
		this.cyclePeriodViewer.setContentProvider(cyclePeriodsProvider);
		this.cyclePeriodViewer.setLabelProvider(cyclePeriodsProvider);
		this.cyclePeriodViewer.addSelectionChangedListener(this);

		label = toolkit.createLabel(composite, ""); //$NON-NLS-1$
		label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

		this.timerImplDescText = toolkit.createText(composite, "", SWT.READ_ONLY | SWT.MULTI | SWT.BORDER); //$NON-NLS-1$
		this.timerImplDescText.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL_GRAB, 1, 2));

		label = toolkit.createLabel(composite, ""); //$NON-NLS-1$
		label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		updateArchitectureViewer();
		startListeningToModelChanges();
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#getEditor()
	 */
	@Override
	public SGenMultiPageEditor getEditor() {
		return (SGenMultiPageEditor) super.getEditor();
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(final ModifyEvent event) {
		updateModel(this.cyclePeriodText.getText());
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final Object source = event.getSource();

		if (source == this.architectureViewer) {
			updateTimerViewer(null);
		}

		if ((source == this.architectureViewer) || (source == this.timerViewer)) {
			final IStructuredSelection selection = (IStructuredSelection) this.timerViewer.getSelection();
			final TimerElement timer = (TimerElement) selection.getFirstElement();
			updateModel(timer);
		} else if (source == this.cyclePeriodViewer) {
			final IStructuredSelection selection = (IStructuredSelection) this.cyclePeriodViewer.getSelection();
			final String cyclePeriod = (String) selection.getFirstElement();
			updateModel(cyclePeriod);
		}
	}

	private void updateModel(final TimerElement timer) {
		stopListeningToModelChanges();

		Display.getDefault().syncExec(new Runnable() {

			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				setParameterValue(IArduinoFeatureConstants.PARAM_TIMER, timer.getId());
			}
		});

		startListeningToModelChanges();
	}

	private void updateModel(final String cyclePeriod) {
		stopListeningToModelChanges();

		Display.getDefault().syncExec(new Runnable() {

			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				setParameterValue(IArduinoFeatureConstants.PARAM_CYCLE_PERIOD, cyclePeriod);
			}
		});

		startListeningToModelChanges();
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
				updateArchitectureViewer();
			}
		});
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose() {
		stopListeningToModelChanges();
		super.dispose();
	}

	protected void startListeningToModelChanges() {
		getEditor().getXtextDocument().addModelListener(this);
	}

	protected void stopListeningToModelChanges() {
		getEditor().getXtextDocument().removeModelListener(this);
	}

	protected String getParameterValue(final String param) {
		final IXtextDocument xtextDocument = getEditor().getXtextDocument();
		return xtextDocument.readOnly(new IUnitOfWork<String, XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork#exec(java.lang.Object)
			 */
			@Override
			public String exec(final XtextResource resource) throws Exception {
				final FeatureParameterValue parameterValue = findFeatureParameterValue(resource, param);
				if (parameterValue != null) {
					return parameterValue.getStringValue();
				}
				return ""; //$NON-NLS-1$
			}
		});
	}

	protected boolean setParameterValue(final String param, final String value) {
		final IXtextDocument xtextDocument = getEditor().getXtextDocument();
		return xtextDocument.modify(new IUnitOfWork<Boolean, XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork#exec(java.lang.Object)
			 */
			@Override
			public Boolean exec(final XtextResource resource) throws Exception {
				final FeatureParameterValue parameterValue = findFeatureParameterValue(resource, param);
				if (parameterValue != null) {
					parameterValue.setValue(value);
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
		});
	}

	protected FeatureParameterValue findFeatureParameterValue(final XtextResource resource, final String param) {
		final GeneratorModel generatorModel = (GeneratorModel) resource.getParseResult().getRootASTElement();
		final TreeIterator<EObject> iterator = generatorModel.eResource().getAllContents();
		while (iterator.hasNext()) {
			final EObject eObject = iterator.next();
			if (eObject instanceof FeatureConfiguration) {
				final FeatureConfiguration featureConfiguration = (FeatureConfiguration) eObject;
				if (IArduinoFeatureConstants.FEATURE_NAME.equals(featureConfiguration.getType().getName())) {
					return featureConfiguration.getParameterValue(param);
				}
			}
		}

		return null;
	}

	protected void updateArchitectureViewer() {
		this.architectureViewer.removeSelectionChangedListener(this);
		this.architectureViewer.setInput(ArchitecturesExtension.getArchitectures());

		final String timerId = getParameterValue(IArduinoFeatureConstants.PARAM_TIMER);
		final TimerElement timer = ArchitecturesExtension.getTimer(timerId);
		if (timer != null) {
			final NamedExtensionElementsProvider provider = (NamedExtensionElementsProvider) this.architectureViewer
					.getContentProvider();
			final int index = provider.getIndex(timer.getArchitecture().getId());
			if (index >= 0) {
				this.architectureViewer.getCombo().select(index);
			}
		} else {
			this.architectureViewer.getCombo().select(0);
		}
		this.architectureViewer.addSelectionChangedListener(this);

		updateTimerViewer(timer);
	}

	private void updateTimerViewer(final TimerElement timer) {
		final ArchitectureElement architecture = (ArchitectureElement) this.architectureViewer.getStructuredSelection()
				.getFirstElement();

		this.timerViewer.removeSelectionChangedListener(this);
		this.timerViewer.setInput(architecture.getTimers());

		if (timer != null) {
			final NamedExtensionElementsProvider provider = (NamedExtensionElementsProvider) this.timerViewer
					.getContentProvider();
			final int index = provider.getIndex(timer.getId());
			if (index >= 0) {
				this.timerViewer.getCombo().select(index);
			}
		} else {
			this.timerViewer.getCombo().select(0);
		}
		this.timerViewer.addSelectionChangedListener(this);

		updateCyclePeriodControls();
	}

	private void updateCyclePeriodControls() {
		final String cyclePeriod = getParameterValue(IArduinoFeatureConstants.PARAM_CYCLE_PERIOD);

		final TimerElement timer = (TimerElement) this.timerViewer.getStructuredSelection().getFirstElement();
		this.timerImplDescText.setText(timer.getDescription());
		if (timer.getPreDefinedCyclePeriods().isEmpty()) {
			this.cyclePeriodLayout.topControl = this.cyclePeriodText;

			this.cyclePeriodText.removeModifyListener(this);
			this.cyclePeriodText.setText(cyclePeriod);
			this.cyclePeriodText.addModifyListener(this);
		} else {
			this.cyclePeriodLayout.topControl = this.cyclePeriodViewer.getCombo();

			this.cyclePeriodViewer.removeSelectionChangedListener(this);
			this.cyclePeriodViewer.setInput(timer.getPreDefinedCyclePeriods());
			this.cyclePeriodViewer.getCombo().select(0);
			this.cyclePeriodViewer.addSelectionChangedListener(this);
		}

		this.cyclePeriodComposite.layout();
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

}
