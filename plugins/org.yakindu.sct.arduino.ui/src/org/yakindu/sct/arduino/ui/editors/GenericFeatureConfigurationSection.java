/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import org.yakindu.sct.model.sgen.FeatureConfiguration;
import org.yakindu.sct.model.sgen.FeatureParameter;
import org.yakindu.sct.model.sgen.FeatureType;
import org.yakindu.sct.model.sgen.ParameterTypes;

public class GenericFeatureConfigurationSection
		implements IFeatureConfigurationSection, ModifyListener, SelectionListener {

	private static final String CAMEL_CASE_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"; //$NON-NLS-1$

	protected final GeneratorEntryFormPage generatorEntryFormPage;

	protected final Map<FeatureParameter, Control> controls = new HashMap<>();

	protected final FeatureType featureType;

	protected Section section;

	private boolean updating;

	public GenericFeatureConfigurationSection(final GeneratorEntryFormPage generatorEntryFormPage,
			final FeatureType featureType) {
		this.generatorEntryFormPage = generatorEntryFormPage;
		this.featureType = featureType;
	}

	/**
	 * @see org.yakindu.sct.arduino.ui.editors.IFeatureConfigurationSection#getSection()
	 */
	@Override
	public Section createSection(final FormToolkit toolkit, final Composite parent) {
		this.section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.section.setText(convertCamelCaseName(this.featureType.getName(), !this.featureType.isOptional()));
		this.section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		final Composite composite = toolkit.createComposite(this.section);
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		for (final FeatureParameter parameter : this.featureType.getParameters()) {
			final Control control = createParameterControl(toolkit, composite, parameter);
			this.controls.put(parameter, control);
		}

		this.section.setClient(composite);

		return this.section;
	}

	private Control createParameterControl(final FormToolkit toolkit, final Composite composite,
			final FeatureParameter parameter) {
		final StringBuilder builder = new StringBuilder();
		builder.append(convertCamelCaseName(parameter.getName(), !parameter.isOptional()));
		builder.append(':');

		final Label label = toolkit.createLabel(composite, builder.toString());
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		Control control;
		if (ParameterTypes.BOOLEAN.equals(parameter.getParameterType())) {
			final Button button = toolkit.createButton(composite, "", SWT.CHECK); //$NON-NLS-1$
			button.addSelectionListener(this);
			control = button;
		} else {
			final Text text = toolkit.createText(composite, "", SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
			text.addModifyListener(this);
			control = text;
		}

		control.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));
		control.setData(parameter);

		return control;
	}

	/**
	 * @see org.yakindu.sct.arduino.ui.editors.IFeatureConfigurationSection#initialize(org.eclipse.xtext.ui.editor.model.IXtextDocument)
	 */
	@Override
	public void initialize(final IXtextDocument xtextDocument) {
		modelChanged(xtextDocument);
	}

	/**
	 * @see org.yakindu.sct.arduino.ui.editors.IFeatureConfigurationSection#modelChanged(org.eclipse.xtext.ui.editor.model.IXtextDocument)
	 */
	@Override
	public void modelChanged(final IXtextDocument xtextDocument) {
		this.updating = true;

		xtextDocument.readOnly(new IUnitOfWork.Void<XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork.Void#process(java.lang.Object)
			 */
			@Override
			public void process(final XtextResource resource) throws Exception {
				final FeatureConfiguration featureConfiguration = SGenModelUtil.getFeatureConfiguration(resource,
						GenericFeatureConfigurationSection.this.generatorEntryFormPage.getStatechartName(),
						GenericFeatureConfigurationSection.this.featureType);
				if (featureConfiguration == null) {
					GenericFeatureConfigurationSection.this.section.setExpanded(false);

					for (final Control control : GenericFeatureConfigurationSection.this.controls.values()) {
						if (control instanceof Text) {
							((Text) control).setText(""); //$NON-NLS-1$
						} else if (control instanceof Button) {
							((Button) control).setSelection(false);
						}
					}
				} else {
					GenericFeatureConfigurationSection.this.section.setExpanded(true);

					for (final FeatureParameter parameter : GenericFeatureConfigurationSection.this.featureType
							.getParameters()) {
						final Control control = GenericFeatureConfigurationSection.this.controls.get(parameter);
						if (control instanceof Text) {
							final String value = SGenModelUtil.getStringParameterValue(resource,
									GenericFeatureConfigurationSection.this.generatorEntryFormPage.getStatechartName(),
									parameter);
							((Text) control).setText(value);
						} else if (control instanceof Button) {
							final boolean value = SGenModelUtil.getBooleanParameterValue(resource,
									GenericFeatureConfigurationSection.this.generatorEntryFormPage.getStatechartName(),
									parameter);
							((Button) control).setSelection(value);
						}
					}
				}
			}
		});

		this.updating = false;
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(final ModifyEvent event) {
		if (!this.updating) {
			final Text text = (Text) event.getSource();
			final FeatureParameter parameter = (FeatureParameter) text.getData();

			this.generatorEntryFormPage.updateModel(parameter, text.getText());
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (!this.updating) {
			final Button button = (Button) event.getSource();
			final FeatureParameter parameter = (FeatureParameter) button.getData();

			this.generatorEntryFormPage.updateModel(parameter, button.getSelection());
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing to do
	}

	/**
	 * @see org.yakindu.sct.arduino.ui.editors.IFeatureConfigurationSection#dispose()
	 */
	@Override
	public void dispose() {
		// nothing to do
	}

	private String convertCamelCaseName(final String name, final boolean mandatory) {
		final StringBuilder builder = new StringBuilder();
		final String[] splitName = name.split(CAMEL_CASE_PATTERN);

		builder.append(splitName[0].substring(0, 1).toUpperCase());
		if (splitName[0].length() > 1) {
			builder.append(splitName[0].substring(1));
		}

		for (int i = 1; i < splitName.length; i++) {
			builder.append(" ").append(splitName[i]); //$NON-NLS-1$
		}

		if (mandatory) {
			builder.append('\u002A');
		}

		return builder.toString();
	}

}
