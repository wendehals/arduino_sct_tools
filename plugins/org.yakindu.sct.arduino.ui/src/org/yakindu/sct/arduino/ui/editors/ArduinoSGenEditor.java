/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.yakindu.sct.arduino.ui.SCTArduinoUIPlugin;

import com.google.inject.Inject;

public class ArduinoSGenEditor extends FormEditor implements IResourceChangeListener {

	protected boolean dirty = false;

	@Inject
	protected XtextEditor xtextEditor;

	public ArduinoSGenEditor() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		try {
			int index = addPage(this.xtextEditor, getEditorInput());
			setPageText(index, Messages.ArduinoSGenEditor_xtextPageText);

			index = addPage(new ArduinoSGenFormPage(this));
			setPageText(index, Messages.ArduinoSGenEditor_formPageText);
		} catch (final PartInitException exception) {
			SCTArduinoUIPlugin.logError(exception);
		}
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
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		this.dirty = false;
		commitPages(true);
		getEditor(0).doSave(monitor);
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
	 * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
	 */
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
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
						if (((FileEditorInput) ArduinoSGenEditor.this.xtextEditor.getEditorInput()).getFile()
								.getProject().equals(event.getResource())) {
							final IEditorPart editorPart = page
									.findEditor(ArduinoSGenEditor.this.xtextEditor.getEditorInput());
							page.closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public IXtextDocument getXtextDocument() {
		return this.xtextEditor.getDocument();
	}

	public XtextEditor getXtextEditor() {
		return this.xtextEditor;
	}

}
