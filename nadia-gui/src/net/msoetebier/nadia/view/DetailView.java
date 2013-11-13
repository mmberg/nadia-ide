package net.msoetebier.nadia.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.msoetebier.nadia.ExceptionHandler;
import net.msoetebier.nadia.Singleton;
import net.msoetebier.nadia.function.RestrictionManagement;
import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.parser.ParserForDetails;
import net.msoetebier.nadia.readsave.AddItemDetailView;
import net.msoetebier.nadia.readsave.RemoveItemDetailView;
import net.msoetebier.nadia.readsave.SaveDetailView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

/**
 * This view shows detail information about a complex type. 
 * It shows an attribute value ('name') and it's simple elements.
 */
public class DetailView extends ViewPart {
	public static final String ID = "nadia-gui.detailView";
	private Parser parser = new Parser();
	private ParserForDetails parserDetails = new ParserForDetails();
	private RestrictionManagement restrictionManagement = new RestrictionManagement();

	private Composite top;
	private ScrolledComposite scrolled;
	private String value;
	private int countElements, counter;
	private List<String> xmlChildrenList = new ArrayList<String>();
	
	@Override
	public void createPartControl(Composite parent) {
		scrolled = new ScrolledComposite(parent, SWT.H_SCROLL |   
				  SWT.V_SCROLL | SWT.BORDER);
		top = new Composite(scrolled, SWT.NONE);
	}
	
	private List<String> getListElements(String item, boolean firstLevel, String schemaPath) {
		List<String> list = new ArrayList<String>();
		try {
			List<String> schemaList = parser.getTypesForElement(changeItem(item), firstLevel, schemaPath, false);
			getChildrenOfItem(changeItem(item), attributeValue(item));
			List <String> xmlList = getXmlChildrenList();
			for (String xml : xmlList) {
				if (schemaList.contains(xml)) {
					list.add(xml);
				}
			}
			for (String schema : schemaList) {
				if (!list.contains(schema)) {
					list.add(schema);
				}
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return list;
	}
	
	public void updateElements(int counter, String item, boolean red, String schemaPath) {
		deleteElementsOnTop();
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		List<String> listElements = getListElements(item, isFirstLevel(changeItem(item), schemaPath), schemaPath);
		removeCountElements();
		if (itemContainsName(item)) {
			setText(counter, changeItem(item), red);
			setElements(counter, changeItem(item), item, listElements, red);
		} else {
			setText(counter, item, red);
			setElements(counter, item, item, listElements, red);			
		}
		top.layout(true);
		scrolled.setContent(top);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		scrolled.setMinSize(top.computeSize(1000, 1000));
	}
	
	private boolean isFirstLevel(String item, String schemaPath) {
		boolean firstLevel = false;
		List<String> parentElements;
		try {
			parentElements = parser.getParentElements(schemaPath);
			for (String element : parentElements) {
				if (element.equals(item)) {
					firstLevel = true;
				}
			} 
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return firstLevel;
	}
	
	private boolean itemContainsName(String item) {
		boolean containsName = false;
		if (item.contains("=")) {
			containsName = true;
		}
		return containsName;
	}
	
	private String changeItem(String item) {
		String[] changeItem = item.split("=");
		return changeItem[0];
	}
	
	private String attributeValue(String item) {
		if (item.contains("=")) {
			String[] changeItem = item.split("=");
			return changeItem[1];	
		} else {
			return "";
		}
	}
	
	public void saveDetails(TreeItem current, List<String> details, int counter) {
		SaveDetailView saveDetailView = new SaveDetailView();
		saveDetailView.saveDetails(top, current, details, counter); 
	}
	
	public String getName(TreeItem current) {
		SaveDetailView saveDetailView = new SaveDetailView();
		return saveDetailView.getName(top, current);
	}
	
	/**
	 * Sets text for an item. Remove value and set it for this item.
	 * If the complexType has a attribute value, setText().
	 */
	private Text setText(int counter, String item, boolean red) {
		new Label(top, SWT.NONE).setText("Name: ");
		Text text = new Text(top, SWT.BORDER);
		try {
			removeValue();
			getValueOrAttributeForItem(counter, item, false);
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		if (getValue() != "") {
			text.setText(getValue());
		}
		GridData data =  new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);
		if (red) {
			text.setBackground(setRedBackgroundColorForName());		
		}
		return text;
	}
	
	public Color setRedBackgroundColorForName() {
		Display display = Display.getCurrent();
		return display.getSystemColor(SWT.COLOR_RED);
	}
	
	public void removeBackgroundColorForName(Text text) {
		Display display = Display.getCurrent();
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		text.setBackground(white);
	}
	
	/**
	 * Set elements to update them. Then update the layout with top.layout(true).
	 * MaxOccurs = 'unbounded' is -1.
	 */
	private void setElements(int counter, String item, String fullItem, List<String> listElements, boolean red) {
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		countElements = 1;
		List<String> multipleElement = new ArrayList<String>();
		for (String element : listElements) {
			Map<Object, String> elementDetail = new HashMap<Object, String>();
			try {
				elementDetail = parserDetails.getMapForItem(element, isFirstLevel(element, navigationView.getSchemaPath()), navigationView.getSchemaPath());
				String maxOccurs = elementDetail.get("maxOccurs");
				if (StringToInteger(maxOccurs) > 1 || maxOccurs.equals("-1")) {
					int number = 0;
					if (multipleElement.contains(element)) {
						for (String multiple : multipleElement) {
							if (multiple.equals(element)) {
								number = number + 1;
							}
						}
					}
					multipleElement.add(element);
					List<String> elementsInXml = getElementListAndSetValue(fullItem, element);
					if (elementsInXml.size() == 0) {
						removeValue();
						chooseTypeElement(counter, item, fullItem, listElements, isFirstLevel(element, navigationView.getSchemaPath()), firstLetterUpperCase(element), elementDetail, red);
					} else {
						for (int i = 0; i < elementsInXml.size(); i++) {
							if (number == i) {
								removeValue();
								setValue(elementsInXml.get(number));
								chooseTypeElement(counter, item, fullItem, listElements, isFirstLevel(element, navigationView.getSchemaPath()), firstLetterUpperCase(element), elementDetail, red);																
							}					
						}						
					}
				} else {
					removeValue();
					getValueOrAttributeForItem(counter, element, true); 
					chooseTypeElement(counter, item, fullItem, listElements, isFirstLevel(element, navigationView.getSchemaPath()), firstLetterUpperCase(element), elementDetail, red);					
				}
			} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
			top.layout(true);
		}
	}
	
	private List<String> getElementListAndSetValue(String parentItem, String element) {
		List<String> elementList = new ArrayList<String>();
		SAXBuilder builder = new SAXBuilder();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		File xmlFile = new File(navigationView.getXmlPath());	 
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (rootNode.getName().equals(changeItem(parentItem))) {
				List<Element> children = rootNode.getChildren();
				for (Element child : children) {
					if (child.getName().equals(element)) {
						elementList.add(child.getValue());						
					}
				}
			} else {
				getElementListForChildren(rootNode, parentItem, element, elementList);
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return elementList;			
	}
	
	private void getElementListForChildren(Element rootNode, String parentItem, String element, List<String> elementList) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getName().equals(changeItem(parentItem))) {
				if (node.getAttributeValue("name").equals(attributeValue(parentItem))) {
					List<Element> children = node.getChildren();
					for (Element child : children) {
						if (child.getName().equals(element)) {
							elementList.add(child.getValue());						
						}
					}
				}
			} else {
				getElementListForChildren(node, parentItem, element, elementList);
			}
		}
	}

	/**
	 * Choose different elements, depending on the "type".
	 */
	private void chooseTypeElement(int counter, String item, String fullItem, List<String> listElements, boolean firstLevel, String element, Map<Object, String> elementDetail, boolean red) throws JDOMException, IOException {
		setCountElements();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		restrictionManagement.setRestrictionMap(navigationView.getRestrictionPath());
		if (elementDetail.size() != 0) {
			if (restrictionManagement.isRestrictedItemWithValues(returnFirstLetterUpperCase(element))) {
				if (restrictionManagement.isString(returnFirstLetterUpperCase(element))) {
					newComboElement(counter, getCountElements(), item, fullItem, firstLevel, element, getValue(), red);					
				} else if (restrictionManagement.isInteger(returnFirstLetterUpperCase(element))) {
					int minimum = restrictionManagement.getMinimum(returnFirstLetterUpperCase(element));
					int maximum = restrictionManagement.getMaximum(returnFirstLetterUpperCase(element));
					newRestrictedIntegerElement(counter, getCountElements(), item, fullItem, firstLevel, element, getValue(), minimum, maximum, red);
				}
			} else if (elementDetail.get("type") == null || elementDetail.get("type").equals("string")) {
				newStringElement(counter, getCountElements(), item, fullItem, firstLevel, element, getValue(), red);
			} else if (elementDetail.get("type").equals("int")) {
				newIntegerElement(counter, getCountElements(), item, fullItem, firstLevel, element, getValue(), red);
			} else if (elementDetail.get("type").equals("boolean")) {
				newBooleanElement(counter, getCountElements(), item, fullItem, firstLevel, element, getValue(), red);
			}
		}
	}
	
	private void getChildrenOfItem(String element, String attributeValue) throws JDOMException, IOException {
		removeXmlChildrenList();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(navigationView.getXmlPath());
		Document document = (Document) builder.build(xmlFile);
		Element rootNode = document.getRootElement();
		
		if (rootNode.getName().equals(element)) {
				List<Element> childrenElements = rootNode.getChildren();
				for (Element childrenElement : childrenElements) {
					setXmlChildrenList(childrenElement.getName());
				}
		} else {
			findChildrenOfItem(rootNode, element, attributeValue);
		}
	}
	
	 private void findChildrenOfItem(Element rootNode, String element, String attributeValue) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getName().equals(element)) {
				if (node.getAttributeValue("name").equals(attributeValue)) {	
					List<Element> childrenElements = node.getChildren();
					for (Element childrenElement : childrenElements) {
						setXmlChildrenList(childrenElement.getName());
					}
				} else {
					findChildrenOfItem(node, element, attributeValue);
				}
			} else {
				findChildrenOfItem(node, element, attributeValue);
			}
		}
	}
	
	/**
	 * Depending on getValueElement true or false this function sets the attribute value or it's value for a element / item.
	 */
	private void getValueOrAttributeForItem(int counter, String element, boolean getValueElement) throws JDOMException, IOException {
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(navigationView.getXmlPath());
		Document document = (Document) builder.build(xmlFile);
		Element rootNode = document.getRootElement();
		
		if (rootNode.getName().equals(element)) {
			if (counter == 0) {
				if (getValueElement) {
					if (!rootNode.getValue().isEmpty()) {
						if (getValue().equals("")) {
							setValue(rootNode.getValue());								
						}
						counter = counter - 1;
					}
				} else {
					if (rootNode.getAttribute("name") != null) {
						if (getValue().equals("")) {
							setValue(rootNode.getAttributeValue("name"));							
						}
						counter = counter - 1;
					}
				}
			}
		} else {
			setCounter(counter);
			findValueForChildren(rootNode, element, getValueElement);
		}
	}
	
	 private void findValueForChildren(Element rootNode, String element, boolean getValue) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getName().equals(element)) {
				if (getCounter() == 0) {	
					if (getValue) {
						if (isElementACharacterData(node)) {
							setValue(getCharacterDataFromElement(node));
						} else if (getValue().equals("")) {
							setValue(node.getValue());							
						}
						setCounter(getCounter() - 1);
						break;
					} else {
						if (node.getAttribute("name") != null) {
							if (getValue().equals("")) {
								setValue(node.getAttributeValue("name"));	
							}
							setCounter(getCounter() - 1);
							break;
						}
					}
				} else {
					setCounter(getCounter() - 1);
					findValueForChildren(node, element, getValue);
				}
			} else {
				findValueForChildren(node, element, getValue);
			}
		}
	}
	 
	private boolean isElementACharacterData(Element node)  {
		boolean isCharacterData = false;
		List<Content> contentList = node.getContent();
		for (Content content : contentList) {
			CType ctype = content.getCType();
			String name = ctype.name();
			if (name.equals("CDATA")) {
				isCharacterData = true;
				break;
			}
		}
		return isCharacterData;
	}
	 
	private String getCharacterDataFromElement(Element node) {
		String cData = "";
		List<Content> contentList = node.getContent();
		for (Content content : contentList) {
			CType ctype = content.getCType();
			if (ctype.name().equals("CDATA")) {
				cData = content.getValue();
			}
		}
		return cData;
	}

	private void newStringElement(int counter, int countElements, String item, String fullItem, boolean firstLevel, String text, String value, boolean red) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(counter, countElements, item, fullItem, firstLevel, label, red);
		  
		Text textElement = new Text(top, SWT.SINGLE | SWT.BORDER);
		if (value != null) {
			textElement.setText(value);
		}
		GridData data =  new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		textElement.setLayoutData(data);
	}
	
	private void newComboElement(int counter, int countElements, String item, String fullItem, boolean firstLevel, String text, String value, boolean red) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(counter, countElements, item, fullItem, firstLevel, label, red);
		Combo combo = new Combo(top, SWT.READ_ONLY | SWT.BORDER);
		combo.setItems(restrictionManagement.getRestrictionValuesForItem(returnFirstLetterUpperCase(text)));
		if (value != null) {
			combo.setText(value);			
		}
		GridData data =  new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		combo.setLayoutData(data);
	}
	  
	private void newIntegerElement(int counter, int countElements, String item, String fullItem, boolean firstLevel, String text, String value, boolean red) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(counter, countElements, item, fullItem, firstLevel, label, red);
		Spinner spinner = new Spinner(top, SWT.BORDER);
		if (value != "") {
			spinner.setSelection(StringToInteger(value));
		} else {
			spinner.setSelection(0);			  
		}
	}
	
	private void newRestrictedIntegerElement(int counter, int countElements, String item, String fullItem, boolean firstLevel, String text, String value, int minimum, int maximum, boolean red) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(counter, countElements, item, fullItem, firstLevel, label, red);
		Spinner spinner = new Spinner(top, SWT.BORDER);
		if (minimum != -1) {
			spinner.setMinimum(minimum);
		} 
		if (maximum != -1) {
			spinner.setMaximum(maximum);			
		}
		if (value != null) {
			spinner.setSelection(StringToInteger(value));
		} else {
			spinner.setSelection(0);			  
		}
	}
	  
	private void newBooleanElement(int counter, int countElements, String item, String fullItem, boolean firstLevel, String text, String value, boolean red) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(counter, countElements, item, fullItem, firstLevel, label, red);
		final Button enabledCheckbox = new Button(top, SWT.CHECK);
		if (getValue() != null && getValue().equals("false")) {
			enabledCheckbox.setSelection(false);
		} else if (value != null && value.equals("true")) {
			enabledCheckbox.setSelection(true);
		} else {
			enabledCheckbox.setSelection(true);			  
		}
	}
	 
	 /**
	  * If right click ('3') open detail view menu manager.
	  */
	 private void addMouseListener(final int counter, final int countElements, final String item, final String fullItem, final boolean firstLevel, final Label label, final boolean red) {
		label.addMouseListener(new MouseListener() {
		private static final long serialVersionUID = 1214520849840906741L;
			@Override
			public void mouseUp(MouseEvent event) {
			}
			@Override
			public void mouseDown(MouseEvent event) {
				if (event.button == 3) {
					detailViewMenuManager(counter, countElements, item, fullItem, firstLevel, event, label, red);
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent event) {
			}
		});
	 }
	 
	 private void detailViewMenuManager(final int counter, final int countElements, final String item, final String fullItem, final boolean firstLevel, MouseEvent event, final Label label, final boolean red) {		        
		final NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		Map<String, String> rightClickMap = navigationView.getLanguageManagement().get("rightClick");
		
		Menu popupMenu = new Menu(label);
	    if (newItemIsAllowed(returnFirstLetterUpperCase(label.getText()))) {
	    	MenuItem newItem = new MenuItem(popupMenu, SWT.NONE);
	       	newItem.setText(rightClickMap.get("new") + " " + label.getText().substring(0, label.getText().length()-2));
	       	newItem.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = 75473005874148164L;
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("New " + label.getText());
					AddItemDetailView addItem = new AddItemDetailView();
					addItem.addItemToXmlFile(fullItem, returnFirstLetterUpperCase(label.getText()));
					try {
						updateElements(counter, fullItem, red, navigationView.getSchemaPath());
					} catch (Exception exception) {
						new ExceptionHandler(exception.getMessage());
					}
				}		
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
	    }
	    if (!itemIsRequired(returnFirstLetterUpperCase(label.getText()))) {
	    	MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
	        deleteItem.setText(rightClickMap.get("delete")  + " " + label.getText().substring(0, label.getText().length()-2));
		    deleteItem.addSelectionListener(new SelectionListener() {
		    	private static final long serialVersionUID = -5412544692884362472L;
				@Override
				public void widgetSelected(SelectionEvent selection) {
					System.out.println("Delete " + label.getText());
					RemoveItemDetailView removeItem = new RemoveItemDetailView();
					removeItem.removeItemFromXmlFile(countElements, getCountItem(countElements, label.getText()), fullItem, returnFirstLetterUpperCase(label.getText()));
					try {
						updateElements(counter, fullItem, red, navigationView.getSchemaPath());
					} catch (Exception exception) {
						new ExceptionHandler(exception.getMessage());
					}
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
	    }
	    label.setMenu(popupMenu);
	 }
	 
	 private int getCountItem(int countElements, String item) {
		int countElement = 0;
		for (Control control : top.getChildren()) {
			if (control instanceof Label) {
				if (countElements > 1) {
					countElements = countElements - 1;
					Label label = (Label) control;
					if (returnFirstLetterUpperCase(item).equals(returnFirstLetterUpperCase(label.getText()))) { //e.g. if item word equals all elements, add to counter  
						countElement = countElement + 1;
					}
				}  					 
			}
		}
		return countElement;
	 }
	 
	 private boolean itemIsRequired(String item) {
		boolean isRequired = false;
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		try {
			Map<Object, String> itemInformation = parserDetails.getMapForItem(item, ifItemIsFirstLevel(item), navigationView.getSchemaPath());
			String minOccurs = itemInformation.get("minOccurs");
			int count = countLabels(firstLetterUpperCase(item));
			if (StringToInteger(minOccurs) == 0) {
				isRequired = false;
		 	} else if (StringToInteger(minOccurs) >= count) {
				isRequired = true;
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return isRequired;
	 }
	 
	 private boolean newItemIsAllowed(String item) {
		boolean isAllowed = true;
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		try {
			Map<Object, String> itemInformation = parserDetails.getMapForItem(item, ifItemIsFirstLevel(item), navigationView.getSchemaPath());
			String maxOccurs = itemInformation.get("maxOccurs");
			int count = countLabels(firstLetterUpperCase(item));
			if (StringToInteger(maxOccurs) == -1) {
				isAllowed = true;
			} else if (StringToInteger(maxOccurs) <= count) {
				isAllowed = false;
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		} 
		return isAllowed;
	 }
	 
	 private boolean ifItemIsFirstLevel(String item) throws SAXException, IOException {
		boolean firstLevel = false;
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		List<String> parentElements = parser.getParentElements(navigationView.getSchemaPath());
		for (String element : parentElements) {
			if (element.equals(item)) {
				firstLevel = true;
			}
		}
		return firstLevel;
	 }
	 
	 private int countLabels(String item) {
		int count = 0;
		for (Control control : top.getChildren()) {
			if (control instanceof Label) {
				Label label = (Label) control;
				if (label.getText().equals(item)) {
					count = count + 1;
				}
			}
		}
		return count;
	}
	 
	private void removeCountElements() {
		countElements = 0;
	}
	 
	private void setCountElements() {
		countElements = countElements + 1;
	}
		
	private int getCountElements() {
		return countElements;
	}
	 
	private void setValue(String valueElement) {
		value = valueElement;
	}
	
	private String getValue() {
		return value;
	}
	
	private void setCounter(int count) {
		counter = count;
	}
	
	private int getCounter() {
		return counter;
	}
	
	private void removeValue() {
		setValue("");
	}
		
	 private String firstLetterUpperCase(String string) {
		String temp = "" + string.charAt(0);
		temp = temp.toUpperCase();
		return temp + string.substring(1) + ": ";
	 }
	 
	 private String returnFirstLetterUpperCase(String string) {
		String temp = "" + string.charAt(0);
		temp = temp.toLowerCase();
		return temp + string.substring(1, string.length()-2);
	 }
	 
	private void deleteElementsOnTop() {
		for (Control control : top.getChildren()) {
			control.dispose();
		}
	}
	
	private Integer StringToInteger(String string) {
	    return new Integer(string);
	}
	
	private void removeXmlChildrenList() {
		if (!xmlChildrenList.isEmpty()) {
			xmlChildrenList.clear();			
		}
	}
	
	private void setXmlChildrenList(String elementString) {
		xmlChildrenList.add(elementString);
	}
	
	private List<String> getXmlChildrenList() {
		return xmlChildrenList;
	}

	@Override
	public void setFocus() {		
	}
}