package net.msoetebier.nadia.table;

import java.io.File;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FileDialog extends TitleAreaDialog {
	private static final long serialVersionUID = 3528799914452763480L;
	private Text schemaText, xmlText, restrictionText, nadiaUrlText;
	private String chooseSchema, chooseXml, chooseRestriction, chooseNadiaUrl, fileSpecification = "";
	private boolean okPressed, notification = false;
	private Map<String, String> fileDialogMap;
	private Composite container;
	
	public FileDialog(Shell parentShell, Map<String, String> fileDialogMap, String fileSpecification) {
		super(parentShell);
		this.fileDialogMap = fileDialogMap;
		this.fileSpecification = fileSpecification;
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
		setContainer(container);
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
//		schemaText.setText(System.getProperty("user.home"));

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
		xmlText.addModifyListener(new ModifyListener() {
			private static final long serialVersionUID = 7741733875467296986L;

			@Override
			public void modifyText(ModifyEvent event) {
				modifyNotification(event);
			}
		});
//		xmlText.setText(System.getProperty("user.home"));
		
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
//		restrictionText.setText(System.getProperty("user.home"));
		
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
//		nadiaUrlText.setText(System.getProperty("user.home"));
		
		nadiaUrlText.setText("https://localhost:8080/nadia/engine/dialog/load");
//		nadiaUrlText.setText("http://mmt.et.hs-wismar.de:8080/nadia/engine/dialog/load");
	}
	
	private void createNotification(Composite container, String text) {
		Label lbtEmpty = new Label(container, SWT.NONE);
		lbtEmpty.setText("");
		
		Label lbtNotification = new Label(container, SWT.NONE);
		lbtNotification.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		lbtNotification.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, true, true) );
		lbtNotification.setText(text);
		
		xmlText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));			
		container.layout();
	}
	
	private void removeNotification(Composite container) {
		Control[] childrenContainer = container.getChildren();
		for (Control control : childrenContainer) {
			if (control instanceof Label) {
				Label label = (Label) control;
				if (label.getForeground().equals(Display.getCurrent().getSystemColor(SWT.COLOR_RED))) {
					label.setText("");
				}
			}
		}		
		xmlText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		container.layout();
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
	
	private void modifyNotification(ModifyEvent event) {
		if (schemaText.getText() != null && xmlText.getText() != null) {
			if (checkInputIsCorrect()) {
				setNotificationText();	
			}			
		}
	}
	
	private boolean checkSpecification() {
		boolean check = false;
		if (fileSpecification.equals("newFile")) {
			File xmlFile = new File(xmlText.getText());	
			if (xmlFile.exists()) {
				check = false;
			} else {
				check = true;
			}
		} else if (fileSpecification.equals("existingFile")) {
			File xmlFile = new File(xmlText.getText());	
			if (xmlFile.exists()) {
				check = true;
			} else {
				check = false;
			}
		}
		return check;
	}
	
	private void setNotificationText() {
		setNotification(true);
		if (fileSpecification.equals("newFile")) {
			File xmlFile = new File(xmlText.getText());	
			if (xmlFile.exists()) {
				createNotification(getContainer(), fileDialogMap.get("warningExist"));
			} else {
				removeNotification(getContainer());
			}
		} else if (fileSpecification.equals("existingFile")) {
			File xmlFile = new File(xmlText.getText());	
			if (!xmlFile.exists()) {
				createNotification(getContainer(), fileDialogMap.get("warningNew"));
			} else {
				removeNotification(getContainer());
			}
		}
	}

	@Override
	protected void okPressed() {
		okPressed = false;
		if (checkInputIsCorrect()) {
			if (checkSpecification()) {
				saveInput();
				okPressed = true;
				super.okPressed();													
			} else if (!checkSpecification() && getNotifaction()) {
				saveInput();
				okPressed = true;
				super.okPressed();								
			} else {
				setNotificationText();	
			}
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
	
	private void setNotification(boolean boolNotification) {
		notification = boolNotification;
	}
	
	private boolean getNotifaction() {
		return notification;
	}
	
	private void setContainer(Composite contain) {
		container = contain;
	}
	
	private Composite getContainer() {
		return container;
	}
} 