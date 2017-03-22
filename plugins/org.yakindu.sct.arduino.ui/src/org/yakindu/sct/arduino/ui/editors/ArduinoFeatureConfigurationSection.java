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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
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
import org.yakindu.sct.editor.sgen.AbstractFeatureConfigurationSection;
import org.yakindu.sct.editor.sgen.SGenModelUtil;
import org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection;
import org.yakindu.sct.model.sgen.FeatureParameter;

public class ArduinoFeatureConfigurationSection extends AbstractFeatureConfigurationSection
		implements FocusListener, ISelectionChangedListener {

	private Section section;

	private Text userSrcFolderText;

	private ComboViewer architectureViewer;

	private ComboViewer timerViewer;

	private Composite cyclePeriodComposite;

	private StackLayout cyclePeriodLayout;

	private ComboViewer cyclePeriodViewer;

	private Text cyclePeriodText;

	private Text timerDescText;

	private boolean mutex;

	/**
	 * @see org.yakindu.sct.editor.sgen.IFeatureConfigurationSection#createSection(org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Section createSection(final FormToolkit toolkit, final Composite parent) {
		this.section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.section.setText(getFeatureType().getName() + IFeatureConfigurationSection.ASTERISK);
		this.section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		this.section.setExpanded(true);

		final Composite composite = toolkit.createComposite(this.section);
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final FeatureParameter userSrcFolderParameter = SGenModelUtil.getFeatureParameter(getFeatureType(),
				IArduinoFeatureConstants.PARAM_USER_SRC_FOLDER);

		Label label = toolkit.createLabel(composite, Messages.ArduinoFeatureConfigurationSection_userSrcFolderLabel
				+ IFeatureConfigurationSection.ASTERISK + ':');
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.userSrcFolderText = toolkit.createText(composite, "", SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		this.userSrcFolderText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL, 1, 2));
		this.userSrcFolderText.addFocusListener(this);
		this.userSrcFolderText.setData(userSrcFolderParameter);

		label = toolkit.createLabel(composite, Messages.ArduinoFeatureConfigurationSection_architectureLabel
				+ IFeatureConfigurationSection.ASTERISK + ':');
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.architectureViewer = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		Combo combo = this.architectureViewer.getCombo();
		toolkit.adapt(combo);
		combo.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		final FeatureParameter timerParameter = SGenModelUtil.getFeatureParameter(getFeatureType(),
				IArduinoFeatureConstants.PARAM_TIMER);

		NamedExtensionElementsProvider provider = new NamedExtensionElementsProvider();
		this.architectureViewer.setContentProvider(provider);
		this.architectureViewer.setLabelProvider(provider);
		this.architectureViewer.setInput(ArchitecturesExtension.getArchitectures());
		this.architectureViewer.addSelectionChangedListener(this);
		this.architectureViewer.getCombo().setData(timerParameter);

		this.timerDescText = toolkit.createText(composite, "", SWT.READ_ONLY | SWT.MULTI | SWT.WRAP); //$NON-NLS-1$
		this.timerDescText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 2, 1));

		label = toolkit.createLabel(composite,
				Messages.ArduinoFeatureConfigurationSection_timerLabel + IFeatureConfigurationSection.ASTERISK + ':');
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

		label = toolkit.createLabel(composite, Messages.ArduinoFeatureConfigurationSection_cyclePeriodLabel
				+ IFeatureConfigurationSection.ASTERISK + ':');
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.cyclePeriodLayout = new StackLayout();

		this.cyclePeriodComposite = new Composite(composite, SWT.NONE);
		this.cyclePeriodComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL, 1, 2));
		this.cyclePeriodComposite.setLayout(this.cyclePeriodLayout);

		final FeatureParameter cyclePeriodParameter = SGenModelUtil.getFeatureParameter(getFeatureType(),
				IArduinoFeatureConstants.PARAM_CYCLE_PERIOD);

		this.cyclePeriodText = toolkit.createText(this.cyclePeriodComposite, "", SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		this.cyclePeriodText.setToolTipText(Messages.ArduinoFeatureConfigurationSection_cyclePeriodToolTip);
		this.cyclePeriodText.addFocusListener(this);
		this.cyclePeriodText.setData(cyclePeriodParameter);

		this.cyclePeriodViewer = new ComboViewer(this.cyclePeriodComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo = this.cyclePeriodViewer.getCombo();
		combo.setToolTipText(Messages.ArduinoFeatureConfigurationSection_cyclePeriodToolTip);
		combo.setData(cyclePeriodParameter);
		toolkit.adapt(combo);

		final CyclePeriodsProvider cyclePeriodsProvider = new CyclePeriodsProvider();
		this.cyclePeriodViewer.setContentProvider(cyclePeriodsProvider);
		this.cyclePeriodViewer.setLabelProvider(cyclePeriodsProvider);
		this.cyclePeriodViewer.addSelectionChangedListener(this);

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

			updateUserSrcFolder(xtextDocument);
			updateArchitectureViewer(xtextDocument);

			this.mutex = false;
		}
	}

	/**
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusGained(final FocusEvent e) {
		// nothing to do
	}

	/**
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent event) {
		if (!this.mutex) {
			this.mutex = true;

			final Text text = (Text) event.getSource();
			getFormPage().updateModel((FeatureParameter) text.getData(), text.getText());

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
				updateTimerViewer(getFormPage().getEditor().getXtextDocument(), null);
			}

			if ((source == this.architectureViewer) || (source == this.timerViewer)) {
				final TimerElement timer = (TimerElement) this.timerViewer.getStructuredSelection().getFirstElement();
				getFormPage().updateModel((FeatureParameter) this.architectureViewer.getCombo().getData(),
						timer.getId());
			} else if (source == this.cyclePeriodViewer) {
				final Integer cyclePeriod = (Integer) this.cyclePeriodViewer.getStructuredSelection().getFirstElement();
				getFormPage().updateModel((FeatureParameter) this.cyclePeriodViewer.getCombo().getData(),
						cyclePeriod.toString());
			}

			this.mutex = false;
		}
	}

	private void updateUserSrcFolder(final IXtextDocument xtextDocument) {
		final String userSrcFolder = getStringParameterValue(xtextDocument, this.userSrcFolderText);
		this.userSrcFolderText.setText(userSrcFolder);
	}

	private void updateArchitectureViewer(final IXtextDocument xtextDocument) {
		final String timerId = getStringParameterValue(xtextDocument, this.architectureViewer.getCombo());
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

		updateTimerViewer(xtextDocument, timer);
	}

	private void updateTimerViewer(final IXtextDocument xtextDocument, final TimerElement timer) {
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

		updateCyclePeriodControls(xtextDocument);
	}

	private void updateCyclePeriodControls(final IXtextDocument xtextDocument) {
		final String cyclePeriod = getStringParameterValue(xtextDocument, this.cyclePeriodText);

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
				getFormPage().updateModel((FeatureParameter) this.cyclePeriodViewer.getCombo().getData(),
						intCyclePeriod.toString());
			}
		}

		this.cyclePeriodComposite.layout();

		this.timerDescText.setText(timer.getDescription());
	}

	private String getStringParameterValue(final IXtextDocument xtextDocument, final Widget widget) {
		final String statechartName = getFormPage().getStatechartName();
		final FeatureParameter featureParameter = (FeatureParameter) widget.getData();
		final String value = xtextDocument.readOnly(new IUnitOfWork<String, XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork.Void#process(java.lang.Object)
			 */
			@Override
			public String exec(final XtextResource resource) throws Exception {
				return SGenModelUtil.getStringParameterValue(resource, statechartName, featureParameter);
			}
		});

		return value;
	}

}
