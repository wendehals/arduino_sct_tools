/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.yakindu.sct.arduino.generator.cpp.extensions.ArchitectureElement;
import org.yakindu.sct.arduino.generator.cpp.extensions.ArchitecturesExtension;
import org.yakindu.sct.arduino.generator.cpp.extensions.TimerElement;
import org.yakindu.sct.arduino.generator.cpp.features.IArduinoFeatureConstants;
import org.yakindu.sct.arduino.ui.wizards.CyclePeriodsProvider;
import org.yakindu.sct.arduino.ui.wizards.NamedExtensionElementsProvider;
import org.yakindu.sct.editor.sgen.GeneratorEntryFormPage;
import org.yakindu.sct.editor.sgen.SGenModelUtil;
import org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection;
import org.yakindu.sct.model.sgen.FeatureParameter;
import org.yakindu.sct.model.sgen.FeatureType;

public class ArduinoFeatureConfigurationSection
		implements IFeatureConfigurationSection, ModifyListener, ISelectionChangedListener {

	private GeneratorEntryFormPage formPage;

	private FeatureType featureType;

	private Section section;

	private Text userSrcFolderText;

	private ComboViewer architectureViewer;

	private ComboViewer timerViewer;

	private Composite cyclePeriodComposite;

	private StackLayout cyclePeriodLayout;

	private ComboViewer cyclePeriodViewer;

	private Text cyclePeriodText;

	private Text timerImplDescText;

	private boolean mutex;

	/**
	 * @see org.yakindu.sct.editor.sgen.IFeatureConfigurationSection#initialize(org.yakindu.sct.editor.sgen.GeneratorEntryFormPage,
	 *      org.yakindu.sct.model.sgen.FeatureType)
	 */
	@Override
	public void initialize(final GeneratorEntryFormPage formPage, final FeatureType featureType) {
		this.formPage = formPage;
		this.featureType = featureType;
	}

	/**
	 * @see org.yakindu.sct.editor.sgen.IFeatureConfigurationSection#createSection(org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Section createSection(final FormToolkit toolkit, final Composite parent) {
		this.section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.section.setText(this.featureType.getName() + '\u002A');
		this.section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		this.section.setExpanded(true);

		final Composite composite = toolkit.createComposite(this.section);
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final FeatureParameter userSrcFolderParameter = SGenModelUtil.getFeatureParameter(this.featureType,
				IArduinoFeatureConstants.PARAM_USER_SRC_FOLDER);

		Label label = toolkit.createLabel(composite, Messages.ArduinoFeatureConfigurationSection_userSrcFolderLabel);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.userSrcFolderText = toolkit.createText(composite, "", SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		this.userSrcFolderText.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL_GRAB));
		this.userSrcFolderText.addModifyListener(this);
		this.userSrcFolderText.setData(userSrcFolderParameter);

		label = toolkit.createLabel(composite, Messages.ArduinoFeatureConfigurationSection_architectureLabel);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.architectureViewer = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		Combo combo = this.architectureViewer.getCombo();
		toolkit.adapt(combo);
		combo.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		final FeatureParameter timerParameter = SGenModelUtil.getFeatureParameter(this.featureType,
				IArduinoFeatureConstants.PARAM_TIMER);

		NamedExtensionElementsProvider provider = new NamedExtensionElementsProvider();
		this.architectureViewer.setContentProvider(provider);
		this.architectureViewer.setLabelProvider(provider);
		this.architectureViewer.setInput(ArchitecturesExtension.getArchitectures());
		this.architectureViewer.addSelectionChangedListener(this);
		this.architectureViewer.getCombo().setData(timerParameter);

		label = toolkit.createLabel(composite, Messages.ArduinoFeatureConfigurationSection_timerLabel);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.timerViewer = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo = this.timerViewer.getCombo();
		toolkit.adapt(combo);
		combo.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		provider = new NamedExtensionElementsProvider();
		this.timerViewer.setContentProvider(provider);
		this.timerViewer.setLabelProvider(provider);
		this.timerViewer.addSelectionChangedListener(this);
		this.timerViewer.getCombo().setData(timerParameter);

		label = toolkit.createLabel(composite, Messages.ArduinoFeatureConfigurationSection_cyclePeriodLabel);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.cyclePeriodLayout = new StackLayout();

		this.cyclePeriodComposite = new Composite(composite, SWT.NONE);
		this.cyclePeriodComposite.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));
		this.cyclePeriodComposite.setLayout(this.cyclePeriodLayout);

		final FeatureParameter cyclePeriodParameter = SGenModelUtil.getFeatureParameter(this.featureType,
				IArduinoFeatureConstants.PARAM_CYCLE_PERIOD);

		this.cyclePeriodText = toolkit.createText(this.cyclePeriodComposite, "", SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		this.cyclePeriodText.setToolTipText(Messages.ArduinoFeatureConfigurationSection_cyclePeriodToolTip);
		this.cyclePeriodText.addModifyListener(this);
		this.cyclePeriodText.setData(cyclePeriodParameter);

		this.cyclePeriodViewer = new ComboViewer(this.cyclePeriodComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo = this.cyclePeriodViewer.getCombo();
		toolkit.adapt(combo);

		final CyclePeriodsProvider cyclePeriodsProvider = new CyclePeriodsProvider();
		this.cyclePeriodViewer.setContentProvider(cyclePeriodsProvider);
		this.cyclePeriodViewer.setLabelProvider(cyclePeriodsProvider);
		this.cyclePeriodViewer.addSelectionChangedListener(this);
		this.cyclePeriodViewer.getCombo().setData(cyclePeriodParameter);

		this.timerImplDescText = toolkit.createText(composite, "", SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.BORDER); //$NON-NLS-1$
		this.timerImplDescText.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL_GRAB, 1, 2));

		this.section.setClient(composite);

		return this.section;
	}

	/**
	 * @see org.yakindu.sct.editor.sgen.IFeatureConfigurationSection#modelChanged(org.eclipse.xtext.ui.editor.model.IXtextDocument)
	 */
	@Override
	public void modelChanged(final IXtextDocument xtextDocument) {
		if (!this.mutex) {
			this.mutex = true;

			xtextDocument.readOnly(new IUnitOfWork.Void<XtextResource>() {
				/**
				 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork.Void#process(java.lang.Object)
				 */
				@Override
				public void process(final XtextResource resource) throws Exception {
					updateUserSrcFolder(resource);
					updateArchitectureViewer(resource);
				}
			});

			this.mutex = false;
		}
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(final ModifyEvent event) {
		if (!this.mutex) {
			this.mutex = true;

			final Text text = (Text) event.getSource();
			this.formPage.updateModel((FeatureParameter) text.getData(), text.getText());

			this.mutex = false;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		if (!this.mutex) {
			this.mutex = true;

			final Object source = event.getSource();

			if (source == this.architectureViewer) {
				this.formPage.getEditor().getXtextDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
					/**
					 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork.Void#process(java.lang.Object)
					 */
					@Override
					public void process(final XtextResource resource) throws Exception {
						updateTimerViewer(resource, null);
					}
				});
			}

			if ((source == this.architectureViewer) || (source == this.timerViewer)) {
				final TimerElement timer = (TimerElement) this.timerViewer.getStructuredSelection().getFirstElement();
				this.formPage.updateModel((FeatureParameter) this.architectureViewer.getCombo().getData(),
						timer.getId());
			} else if (source == this.cyclePeriodViewer) {
				final Integer cyclePeriod = (Integer) this.cyclePeriodViewer.getStructuredSelection().getFirstElement();
				this.formPage.updateModel((FeatureParameter) this.cyclePeriodViewer.getCombo().getData(),
						cyclePeriod.toString());
			}

			this.mutex = false;
		}
	}

	/**
	 * @see org.yakindu.sct.editor.sgen.IFeatureConfigurationSection#dispose()
	 */
	@Override
	public void dispose() {
		// nothing to do
	}

	protected void updateUserSrcFolder(final XtextResource resource) {
		final String userSrcFolder = SGenModelUtil.getStringParameterValue(resource, this.formPage.getStatechartName(),
				(FeatureParameter) this.userSrcFolderText.getData());
		this.userSrcFolderText.setText(userSrcFolder);
	}

	protected void updateArchitectureViewer(final XtextResource resource) {
		final String timerId = SGenModelUtil.getStringParameterValue(resource, this.formPage.getStatechartName(),
				(FeatureParameter) this.architectureViewer.getCombo().getData());
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

		updateTimerViewer(resource, timer);
	}

	protected void updateTimerViewer(final XtextResource resource, final TimerElement timer) {
		final ArchitectureElement architecture = (ArchitectureElement) this.architectureViewer.getStructuredSelection()
				.getFirstElement();

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

		updateCyclePeriodControls(resource);
	}

	protected void updateCyclePeriodControls(final XtextResource resource) {
		final FeatureParameter parameter = (FeatureParameter) this.cyclePeriodText.getData();
		final String cyclePeriod = SGenModelUtil.getStringParameterValue(resource, this.formPage.getStatechartName(),
				parameter);

		final TimerElement timer = (TimerElement) this.timerViewer.getStructuredSelection().getFirstElement();
		if (timer.getPreDefinedCyclePeriods().isEmpty()) {
			this.cyclePeriodLayout.topControl = this.cyclePeriodText;

			this.cyclePeriodText.setText(cyclePeriod);
		} else {
			final Combo combo = this.cyclePeriodViewer.getCombo();
			this.cyclePeriodLayout.topControl = combo;

			this.cyclePeriodViewer.setInput(timer.getPreDefinedCyclePeriods());

			final int index = ((CyclePeriodsProvider) this.cyclePeriodViewer.getContentProvider())
					.getIndex(cyclePeriod);
			if (index > -1) {
				combo.select(index);
			} else {
				combo.select(0);
				// cyclePeriod could not be found in list, update model
				final Integer intCyclePeriod = (Integer) this.cyclePeriodViewer.getStructuredSelection()
						.getFirstElement();
				this.formPage.updateModel(parameter, intCyclePeriod.toString());
			}
		}

		this.cyclePeriodComposite.layout();

		this.timerImplDescText.setText(timer.getDescription());
	}

}
