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
	private int countElements;
	private int counter;
	
	@Override
	public void createPartControl(Composite parent) {
		scrolled = new ScrolledComposite(parent, SWT.H_SCROLL |   
				  SWT.V_SCROLL | SWT.BORDER);
		top = new Composite(scrolled, SWT.NONE);
	}
	
	public void updateElements(int counter, String item, List<String> listElements, boolean firstLevel, boolean red) {
		deleteElementsOnTop();
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		if (itemContainsName(item)) {
			item = changeItem(item);
			setText(counter, item, red);
			setElements(counter, item, listElements, firstLevel);
		} else {
			setText(counter, item, red);
			setElements(counter, item, listElements, firstLevel);			
		}
		top.layout(true);
		scrolled.setContent(top);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		scrolled.setMinSize(top.computeSize(1000, 1000));
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
	private void setElements(int counter, String item, List<String> listElements, boolean firstLevel) {
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		countElements = 1;
		for (String element : listElements) {
			Map<Object, String> elementDetail = new HashMap<Object, String>();
			try {
				elementDetail = parserDetails.getMapForItem(element, firstLevel, navigationView.getSchemaPath());
				String maxOccurs = elementDetail.get("maxOccurs");
				if (StringToInteger(maxOccurs) > 1) {
//				if (StringToInteger(maxOccurs) == -1 || StringToInteger(maxOccurs) > 1) {
					List<String> elementsInXml = getElementListAndSetValue(element);
					for (String elementInXml : elementsInXml) {
						removeValue();
						setValue(elementInXml);
						chooseTypeElement(item, listElements, firstLevel, firstLetterUpperCase(element), elementDetail);
					}
				} else {
					removeValue();
					getValueOrAttributeForItem(counter, element, true); 
					chooseTypeElement(item, listElements, firstLevel, firstLetterUpperCase(element), elementDetail);					
				}
			} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
			top.layout(true);
		}
	}
	
	private List<String> getElementListAndSetValue(String element) {
		List<String> elementList = new ArrayList<String>();
		SAXBuilder builder = new SAXBuilder();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		File xmlFile = new File(navigationView.getXmlPath());	 
		Document document;
		try {
			document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (rootNode.getName().equals(element)) {
				elementList.add(rootNode.getAttributeValue("name"));
			} else {
				getElementListForChildren(rootNode, element, elementList);
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return elementList;			
	}
	
	private void getElementListForChildren(Element rootNode, String element,
			List<String> elementList) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getName().equals(element)) {
				elementList.add(node.getText());
			} else {
				getElementListForChildren(node, element, elementList);
			}
		}
	}

	/**
	 * Choose different elements, depending on the "type".
	 */
	private void chooseTypeElement(String item, List<String> listElements, boolean firstLevel, String element, Map<Object, String> elementDetail) throws JDOMException, IOException {
		setCountElements();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		restrictionManagement.setRestrictionMap(navigationView.getRestrictionPath());
		if (elementDetail.size() != 0) {
			if (restrictionManagement.isRestrictedItemWithValues(returnFirstLetterUpperCase(element))) {
				if (restrictionManagement.isString(returnFirstLetterUpperCase(element))) {
					newComboElement(getCountElements(), item, listElements, firstLevel, element, getValue());					
				} else if (restrictionManagement.isInteger(returnFirstLetterUpperCase(element))) {
					int minimum = restrictionManagement.getMinimum(returnFirstLetterUpperCase(element));
					int maximum = restrictionManagement.getMaximum(returnFirstLetterUpperCase(element));
					newRestrictedIntegerElement(getCountElements(), item, listElements, firstLevel, element, getValue(), minimum, maximum);
				}
			} else if (elementDetail.get("type") == null || elementDetail.get("type").equals("string")) {
				newStringElement(getCountElements(), item, listElements, firstLevel, element, getValue());
			} else if (elementDetail.get("type").equals("int")) {
				newIntegerElement(getCountElements(), item, listElements, firstLevel, element, getValue());
			} else if (elementDetail.get("type").equals("boolean")) {
				newBooleanElement(getCountElements(), item, listElements, firstLevel, element, getValue());
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

	private void newStringElement(int countElements, String item, List<String> listElements, boolean firstLevel, String text, String value) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(countElements, item, listElements, firstLevel, label);
		  
		Text textElement = new Text(top, SWT.SINGLE | SWT.BORDER);
		if (value != null) {
			textElement.setText(value);
		}
		GridData data =  new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		textElement.setLayoutData(data);
	}
	
	private void newComboElement(int countElements, String item, List<String> listElements, boolean firstLevel, String text, String value) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(countElements, item, listElements, firstLevel, label);
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
	  
	private void newIntegerElement(int countElements, String item, List<String> listElements, boolean firstLevel, String text, String value) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(countElements, item, listElements, firstLevel, label);
		Spinner spinner = new Spinner(top, SWT.BORDER);
		if (value != "") {
			spinner.setSelection(StringToInteger(value));
		} else {
			spinner.setSelection(0);			  
		}
	}
	
	private void newRestrictedIntegerElement(int countElements, String item, List<String> listElements, boolean firstLevel, String text, String value, int minimum, int maximum) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(countElements, item, listElements, firstLevel, label);
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
	  
	private void newBooleanElement(int countElements, String item, List<String> listElements, boolean firstLevel, String text, String value) {
		Label label = new Label(top, SWT.NONE);
		label.setText(text);
		addMouseListener(countElements, item, listElements, firstLevel, label);
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
	 private void addMouseListener(final int countElements, final String item, final List<String> listElements, final boolean firstLevel, final Label label) {
		label.addMouseListener(new MouseListener() {
		private static final long serialVersionUID = 1214520849840906741L;
			@Override
			public void mouseUp(MouseEvent event) {
			}
			@Override
			public void mouseDown(MouseEvent event) {
				if (event.button == 3) {
					detailViewMenuManager(countElements, item, listElements, firstLevel, event, label);
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent event) {
			}
		});
	 }
	 
	 private void detailViewMenuManager(final int countElements, final String item, final List<String> listElements, final boolean firstLevel, MouseEvent event, final Label label) {		        
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		Map<String, String> rightClickMap = navigationView.getLanguageManagement().get("rightClick");
		
		Menu popupMenu = new Menu(label);
	    if (newItemIsAllowed(returnFirstLetterUpperCase(label.getText()))) {
	    	MenuItem newItem = new MenuItem(popupMenu, SWT.NONE);
	       	newItem.setText(rightClickMap.get("new") + label.getText().substring(0, label.getText().length()-2));
	       	newItem.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = 75473005874148164L;
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("New " + label.getText());
					addElement(label.getText());
					AddItemDetailView addItem = new AddItemDetailView();
					addItem.addItemToXmlFile(returnFirstLetterUpperCase(label.getText()));
				}		
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
	    }
	    if (!itemIsRequired(returnFirstLetterUpperCase(label.getText()))) {
	    	MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
	        deleteItem.setText(rightClickMap.get("delete"));
		    deleteItem.addSelectionListener(new SelectionListener() {
		    	private static final long serialVersionUID = -5412544692884362472L;
				@Override
				public void widgetSelected(SelectionEvent selection) {
					System.out.println("Delete " + label.getText());
					RemoveItemDetailView removeItem = new RemoveItemDetailView();
					removeItem.removeItemFromXmlFile(countElements, getCountItem(countElements, item), returnFirstLetterUpperCase(label.getText()));
					updateElements(0, item, listElements, firstLevel, false); //TODO 0 muss getauscht werden!
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
	    }
	    label.setMenu(popupMenu);
	 }
	 
	 private void addElement(String item) {
		new Label(top, SWT.NONE).setText(item);
		Text text = new Text(top, SWT.BORDER);
		GridData data =  new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);
		top.layout(true);
	 }
	 
	 private int getCountItem(int countElements, String item) {
		int countElement = 0;
		for (Control control : top.getChildren()) {
			if (control instanceof Label) {
				if (countElements >= 1) {
					countElements = countElements - 1;
					Label label = (Label) control;
					if (item.equals(returnFirstLetterUpperCase(label.getText()))) { //e.g. if item word equals all elements, add to counter  
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

	@Override
	public void setFocus() {		
	}
}