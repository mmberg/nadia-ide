package net.msoetebier.nadia.table;

import java.io.File;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FileDialog extends TitleAreaDialog {
	private static final long serialVersionUID = 3528799914452763480L;
	private Text schemaText, xmlText, restrictionText, nadiaUrlText;
	private String chooseSchema, chooseXml, chooseRestriction, chooseNadiaUrl;
	private boolean okPressed;
	private Map<String, String> fileDialogMap;
	
	public FileDialog(Shell parentShell, Map<String, String> fileDialogMap) {
		super(parentShell);
		this.fileDialogMap = fileDialogMap;
	}

	@Override
	public void create() {
		super.create();
		setTitle(fileDialogMap.get("title"));
		setMessage(fileDialogMap.get("message"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);
		
		createSchema(container, fileDialogMap);
		createXml(container, fileDialogMap);
		createRestriction(container, fileDialogMap);
		createNadiaUrl(container, fileDialogMap);
		
		return area;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
	    createButton(parent, IDialogConstants.OK_ID,
	            "OK", true);
	}

	private void createSchema(Composite container, Map<String, String> fileDialogMap) {
		Label lbtSchema = new Label(container, SWT.NONE);
		lbtSchema.setText(fileDialogMap.get("schemaPath"));

		GridData dataSchema = new GridData();
		dataSchema.grabExcessHorizontalSpace = true;
		dataSchema.horizontalAlignment = GridData.FILL;

		schemaText = new Text(container, SWT.BORDER);
		schemaText.setLayoutData(dataSchema);
//	  	schemaText.setText("D:/schema3.xsd");
		schemaText.setText("D:/Anwendung/schemaDatei/schema1.xsd");
	}
	  
	private void createXml(Composite container, Map<String, String> fileDialogMap) {
		Label lbtXml = new Label(container, SWT.NONE);
		lbtXml.setText(fileDialogMap.get("xmlPath"));
	  
		GridData dataXml = new GridData();
		dataXml.grabExcessHorizontalSpace = true;
		dataXml.horizontalAlignment = GridData.FILL;
		xmlText = new Text(container, SWT.BORDER);
		xmlText.setLayoutData(dataXml);
	  	xmlText.setText("D:/Anwendung/xmlDatei/xmlfile.xml");
//		xmlText.setText("C:/Users/Christina/Desktop/Master/Master-Thesis/Dateien/Aktuell/XML-Dateien/dummy4.xml");
	}
	
	private void createRestriction(Composite container, Map<String, String> fileDialogMap) {
		Label lbtRestriction = new Label(container, SWT.NONE);
		lbtRestriction.setText(fileDialogMap.get("restrictionPath"));
		
		GridData dataRestriction = new GridData();
		dataRestriction.grabExcessHorizontalSpace = true;
		dataRestriction.horizontalAlignment = GridData.FILL;
		restrictionText = new Text(container, SWT.BORDER);
		restrictionText.setLayoutData(dataRestriction);
		restrictionText.setText("D:/Anwendung/restrictionDatei/restriction.xml");
	}
	
	private void createNadiaUrl(Composite container, Map<String, String> fileDialogMap) {
		Label lbtNadiaUrl = new Label(container, SWT.NONE);
		lbtNadiaUrl.setText(fileDialogMap.get("nadiaUrlPath"));
		
		GridData dataNadiaUrl = new GridData();
		dataNadiaUrl.grabExcessHorizontalSpace = true;
		dataNadiaUrl.horizontalAlignment = GridData.FILL;
		nadiaUrlText = new Text(container, SWT.BORDER);
		nadiaUrlText.setLayoutData(dataNadiaUrl);
		nadiaUrlText.setText("https://localhost:8080/nadia/engine/dialog/load");
	}

	@Override
	protected boolean isResizable() {
		return false;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(700, 500);
	}

	/**
	 * Save the values of the text fields into string, because the UI gets disposed 
	 * and the text fields are not accessible any more.
	 */
	private void saveInput() {
		chooseSchema = schemaText.getText();
		chooseXml = xmlText.getText();
		chooseRestriction = restrictionText.getText();
		chooseNadiaUrl = nadiaUrlText.getText();
	}
	
	private boolean checkInputIsCorrect() {
		boolean inputIsCorrect = false;
		String schemaFile = schemaText.getText();
		String xmlFile = xmlText.getText();		
		if (!schemaFile.equals("") && !xmlFile.equals("")) {
			String schemaFormat = schemaFile.substring(schemaFile.length()-3, schemaFile.length());
			String xmlFormat = xmlFile.substring(xmlFile.length()-3, xmlFile.length());
			if (schemaFormat.equals("xsd") || schemaFormat.equals("XSD")) {
				if (xmlFormat.equals("xml") || xmlFormat.equals("XML")) {
					if (new File(schemaFile).exists()) {
						inputIsCorrect = true;						
					}
				}
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
		} else {
			setMessage(fileDialogMap.get("errorMessage"), IMessageProvider.ERROR);
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

	public String getSchemaPath() {
		return chooseSchema;
	}

	public String getXmlPath() {
		return chooseXml;
	}
	
	public String getRestrictionPath() {
		return chooseRestriction;
	}
	
	public String getNadiaUrlPath() {
		return chooseNadiaUrl;
	}
} 