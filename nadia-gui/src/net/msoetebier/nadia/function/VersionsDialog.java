package net.msoetebier.nadia.function;

import java.util.Map;

import net.msoetebier.nadia.Singleton;
import net.msoetebier.nadia.view.NavigationView;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class VersionsDialog extends TitleAreaDialog{
	private static final long serialVersionUID = 5623911944967935649L;
	private Map<String, String> versionsDialogMap;
	private Text versionsText;
	private boolean okPressed;

	public VersionsDialog(Shell parentShell, Map<String, String> versionsDialogMap) {
		super(parentShell);
		this.versionsDialogMap = versionsDialogMap;
	}

	@Override
	public void create() {
		super.create();
		setTitle(versionsDialogMap.get("title"));
		setMessage(versionsDialogMap.get("message"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);
		
		createPath(container, versionsDialogMap);
		return area;
	}
	
	private void createPath(Composite container, Map<String, String> versionsDialogMap) {
		Label lbtPath = new Label(container, SWT.NONE);
		lbtPath.setText(versionsDialogMap.get("path"));
		
		GridData dataPath = new GridData();
		dataPath.grabExcessHorizontalSpace = true;
		dataPath.horizontalAlignment = GridData.FILL;
		versionsText = new Text(container, SWT.BORDER);
		versionsText.setLayoutData(dataPath);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	/**
	 * Save the values of the text fields into string, because the UI gets disposed 
	 * and the text fields are not accessible any more.
	 */
	private void saveInput() {
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		navigationView.setXmlPath(versionsText.getText());
	}
	
	private boolean checkInputIsCorrect() {
		boolean inputIsCorrect = false;
		String xmlFile = versionsText.getText();		
		if (!xmlFile.equals("")) {
			String xmlFormat = xmlFile.substring(xmlFile.length()-3, xmlFile.length());
			if (xmlFormat.equals("xml") || xmlFormat.equals("XML")) {
				inputIsCorrect = true;						
			}
		}
		return inputIsCorrect;
	}

	@Override
	protected void okPressed() {
		okPressed = false;
		if (checkInputIsCorrect()) {
			saveInput();
			okPressed = true;
			super.okPressed();	
		} else if (versionsText.getText() == ""){
			okPressed = true;
			super.okPressed();
		} else {
			setMessage(versionsDialogMap.get("errorMessage"), IMessageProvider.ERROR);
		}
	}
	
	@Override
	public boolean close() {
		boolean isClosing = false;
		if (okPressed) {
			isClosing = true;
			super.close();
		} 
		return isClosing;
	}
}
