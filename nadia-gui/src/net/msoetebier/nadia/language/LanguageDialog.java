package net.msoetebier.nadia.language;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class LanguageDialog extends TitleAreaDialog {
	private static final long serialVersionUID = -654180888821834470L;
	String language = "";
	
	public LanguageDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	public void create() {
		super.create();
		setTitle("Choose language");
		setMessage("Please choose a given language für this application.", IMessageProvider.INFORMATION);
	}
	
	@Override
	  protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
		container.setLayout(layout);
		
	    createEnglishButton(container);
	    createGermanButton(container);
	    return container;
	}

	private void createEnglishButton(Composite container) {
		Button englishButton = new Button(container, SWT.PUSH);
	    englishButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.RIGHT, false,
	        false));
	    englishButton.setText("English");
	    englishButton.addSelectionListener(new SelectionAdapter() {
	    	private static final long serialVersionUID = 1L;
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		language = "english";
	    		close();
	    	}
	    });
	}
    
	private void createGermanButton(Composite container) {
	    Button germanButton = new Button(container, SWT.PUSH);
	    germanButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.RIGHT, false,
	        false));
	    germanButton.setText("Deutsch");
	    germanButton.addSelectionListener(new SelectionAdapter() {
		private static final long serialVersionUID = 1L;
		@Override
	      public void widgetSelected(SelectionEvent e) {
	       language = "german";
	       close();
	      }
	    });
	}
	
	@Override
	protected boolean isResizable() {
		return false;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(400, 200);
	}
	
	@Override
	public boolean close() {
		boolean isClosing = false;
		if (language != "") {
			isClosing = true;
			super.close();
		} 
		return isClosing;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
	}
	
	public String getLanguage() {
		return language;
	}
} 