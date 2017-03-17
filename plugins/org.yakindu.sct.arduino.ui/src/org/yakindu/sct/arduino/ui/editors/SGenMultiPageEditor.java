/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.yakindu.sct.arduino.ui.SCTArduinoUIPlugin;
import org.yakindu.sct.model.sgen.GeneratorEntry;
import org.yakindu.sct.model.sgen.GeneratorModel;
import org.yakindu.sct.model.sgraph.Statechart;

import com.google.inject.Inject;

public class SGenMultiPageEditor extends FormEditor implements IResourceChangeListener, IXtextModelListener {

	@Inject
	protected XtextEditor xtextEditor;

	public SGenMultiPageEditor() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(final IEditorInput input) {
		super.setInput(input);
		setPartName(getEditorInput().getName());
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		try {
			final int index = addPage(this.xtextEditor, getEditorInput());
			setPageText(index, Messages.ArduinoSGenEditor_sourcePageText);

			addGeneratorEntryPages();
		} catch (final PartInitException exception) {
			SCTArduinoUIPlugin.logError(exception);
		}

		getXtextDocument().addModelListener(this);
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(final IProgressMonitor monitor) {
		commitPages(true);
		this.xtextEditor.doSave(monitor);
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// nothing to be implemented
	}

	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {

				/**
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					final IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (final IWorkbenchPage page : pages) {
						if (((FileEditorInput) SGenMultiPageEditor.this.xtextEditor.getEditorInput()).getFile()
								.getProject().equals(event.getResource())) {
							final IEditorPart editorPart = page
									.findEditor(SGenMultiPageEditor.this.xtextEditor.getEditorInput());
							page.closeEditor(editorPart, true);
						}
					}
				}
			});
		}
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
				try {
					updateGeneratorPages();
				} catch (final PartInitException exception) {
					SCTArduinoUIPlugin.logError(exception);
				}
			}
		});
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
	 */
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public XtextEditor getXtextEditor() {
		return this.xtextEditor;
	}

	public IXtextDocument getXtextDocument() {
		return this.xtextEditor.getDocument();
	}

	protected String getGeneratorId() {
		return getXtextDocument().readOnly(new IUnitOfWork<String, XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork#exec(java.lang.Object)
			 */
			@Override
			public String exec(final XtextResource resource) throws Exception {
				final GeneratorModel generatorModel = (GeneratorModel) resource.getContents().get(0);
				return generatorModel.getGeneratorId();
			}
		});
	}

	protected List<GeneratorEntry> getGeneratorEntries() {
		return getXtextDocument().readOnly(new IUnitOfWork<List<GeneratorEntry>, XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork#exec(java.lang.Object)
			 */
			@Override
			public List<GeneratorEntry> exec(final XtextResource resource) throws Exception {
				final GeneratorModel generatorModel = (GeneratorModel) resource.getContents().get(0);
				return generatorModel.getEntries();
			}
		});
	}

	private void addGeneratorEntryPages() throws PartInitException {
		for (final GeneratorEntry generatorEntry : getGeneratorEntries()) {
			final EObject elementRef = generatorEntry.getElementRef();
			if (elementRef instanceof Statechart) {
				final Statechart statechart = (Statechart) elementRef;
				final String name = statechart.getName();

				final int index = getPageCount() - 1;
				addPage(index, new GeneratorEntryFormPage(this, getGeneratorId(), name));
				setPageText(index, name);
			}
		}
	}

	protected void updateGeneratorPages() throws PartInitException {
		final List<GeneratorEntry> generatorEntries = getGeneratorEntries();

		// add new pages for new generator entries
		for (final GeneratorEntry generatorEntry : generatorEntries) {
			if (generatorEntry.getElementRef() instanceof Statechart) {
				final Statechart statechart = (Statechart) generatorEntry.getElementRef();
				final String name = statechart.getName();

				boolean found = false;
				// ignore the last source page
				for (int i = 0; i < (getPageCount() - 1); i++) {
					if (getPageText(i).equals(name)) {
						found = true;
						break;
					}
				}
				if (!found) {
					final int index = getPageCount() - 1;
					addPage(index, new GeneratorEntryFormPage(this, getGeneratorId(), name));
					setPageText(index, name);
				}
			}
		}

		// remove pages of removed generator entries
		// ignore the last source page
		for (int i = getPageCount() - 2; i >= 0; i--) {
			boolean found = false;
			for (final GeneratorEntry generatorEntry : generatorEntries) {
				if (generatorEntry.getElementRef() instanceof Statechart) {
					final Statechart statechart = (Statechart) generatorEntry.getElementRef();
					final String name = statechart.getName();

					if (getPageText(i).equals(name)) {
						found = true;
						break;
					}
				}
			}

			if (!found) {
				removePage(i);
			}
		}
	}

}
