package net.msoetebier.nadia.table;

import java.util.Map;

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

public class ChooseNewOrExistingFileDialog extends TitleAreaDialog {
	private static final long serialVersionUID = 5230122049398070328L;
	private Map<String, String> chooseFileDialogMap;
	private String fileSpecification = "";
	
	public ChooseNewOrExistingFileDialog(Shell parentShell, Map<String, String> chooseFileDialogMap) {
		super(parentShell);
		this.chooseFileDialogMap = chooseFileDialogMap;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle(chooseFileDialogMap.get("title"));
		setMessage(chooseFileDialogMap.get("message"), IMessageProvider.INFORMATION);
	}
	
	@Override
	  protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
		container.setLayout(layout);
		
	    createNewFileButton(container, chooseFileDialogMap);
	    createExistingFileButton(container, chooseFileDialogMap);
	    return container;
	}

	private void createNewFileButton(Composite container, Map<String, String> chooseFileDialogMap) {
		Button newFileButton = new Button(container, SWT.PUSH);
	    newFileButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.RIGHT, false,
	        false));
	    newFileButton.setText(chooseFileDialogMap.get("newFile"));
	    newFileButton.addSelectionListener(new SelectionAdapter() {
	    	private static final long serialVersionUID = 1L;
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		fileSpecification = "newFile";
	    		close();
	    	}
	    });
	}
    
	private void createExistingFileButton(Composite container, Map<String, String> chooseFileDialogMap) {
	    Button existingFileButton = new Button(container, SWT.PUSH);
	    existingFileButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.RIGHT, false,
	        false));
	    existingFileButton.setText(chooseFileDialogMap.get("existingFile"));
	    existingFileButton.addSelectionListener(new SelectionAdapter() {
		private static final long serialVersionUID = 1L;
		@Override
	      public void widgetSelected(SelectionEvent e) {
	       fileSpecification = "existingFile";
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
		if (fileSpecification != "") {
			isClosing = true;
			super.close();
		} 
		return isClosing;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
	}
	
	public String getFileSpecification() {
		return fileSpecification;
	}
} 