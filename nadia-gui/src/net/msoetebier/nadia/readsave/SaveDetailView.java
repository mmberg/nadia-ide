package net.msoetebier.nadia.readsave;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import net.msoetebier.nadia.ExceptionHandler;
import net.msoetebier.nadia.Singleton;
import net.msoetebier.nadia.view.NavigationView;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class SaveDetailView {
	private boolean isCurrent;
	private int count;
	private int countElements;
	private int attributeCounter;
	
	public String getName(Composite top, TreeItem current) {
		String getName = "";
		String currentElement = "";
		for (Control control : top.getChildren()) {
			if (control instanceof Label) {
				Label label = (Label) control;
				currentElement = returnFirstLetterUpperCase(label.getText());
			} else if (currentElement.equals("name")) {
					Text text = (Text) control;
					if (text.getText() != null) {
						getName = text.getText();						
					}
			}
		}
		return getName;
	}	
	
	/**
	 * Called from mouseDown() or drop() in View.
	 * Needs to save this view, before the detail view gets updated.
	 * Find the widgets (Label, Text, ..) and add it to the xml file.
	 */
	public void saveDetails(Composite top, TreeItem current, List<String> details, int counter) { //TODO changeItem
		List<String> controlElements = new ArrayList<String>();
//		int counter = countElements(current);
		String currentElement = "";
		for (Control control : top.getChildren()) {
			if (control instanceof Label) {
				Label label = (Label) control;
				currentElement = returnFirstLetterUpperCase(label.getText());
			} else if (currentElement.equals("name")) {
					Text text = (Text) control;
//					if (!text.getText().equals("")) { //TODO
						saveAttributeToExistingFile(counter, changeItem(current.getText()), current, text.getText());  //TODO
						currentElement = "";
//					}
			} else if (currentElement != "") {
				if (control instanceof Text) {
					int currentControl = setIsCurrentControl(currentElement, controlElements, counter);
					Text text = (Text) control;
					controlElements.add(currentElement);
					saveValueToExistingFile(counter, current, currentElement, text.getText(), getIsCurrent(), currentControl);
				} else if (control instanceof Combo) {
					int currentControl = setIsCurrentControl(currentElement, controlElements, counter);
					Combo combo = (Combo) control;
					controlElements.add(currentElement);
					saveValueToExistingFile(counter, current, currentElement, combo.getText(), getIsCurrent(), currentControl);
				} else if (control instanceof Spinner) {
					int currentControl = setIsCurrentControl(currentElement, controlElements, counter);
					Spinner spinner = (Spinner) control;
					controlElements.add(currentElement);
					saveValueToExistingFile(counter, current, currentElement, IntegerToString(spinner.getSelection()), getIsCurrent(), currentControl);
				} else if (control instanceof Button) {
					int currentControl = setIsCurrentControl(currentElement, controlElements, counter);
					Button enabledCheckbox = (Button) control;
					controlElements.add(currentElement);
					saveValueToExistingFile(counter, current, currentElement, BooleanToString(enabledCheckbox.getSelection()), getIsCurrent(), currentControl);
				}
			}
		}
	}
	
//	private int countElements(TreeItem treeItem) {
////		int count = 0;
////		TreeItem parentItem = treeItem.getParentItem();
////		if (parentItem != null) {
////			TreeItem[] items = parentItem.getItems();
////			for (TreeItem item : items) {
////				if (changeItem(item.getText()).equals(changeItem(treeItem.getText()))) {
////					if (!item.getText().equals(treeItem.getText())) {
////						count = count + 1;
////					} else {
////						break;
////					}
////				}
////			}
////		}
////		return count;
//	}
	
	private void getXmlCountForElement(String currentElement) {
		SAXBuilder builder = new SAXBuilder();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		File xmlFile = new File(navigationView.getXmlPath());	
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (rootNode.getName().equals(currentElement)) {
				setCounter(1);
			} else {
				countForElement(rootNode, currentElement);
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private int setIsCurrentControl(String currentElement, List<String> controlElements, int counter) {
		int countControlElement = 0;
		removeIsCurrent();
		isCurrentElementInFile(currentElement, counter);
		for (String controlElement : controlElements) {
			if (controlElement.equals(currentElement)) {
				countControlElement = countControlElement + 1;
			}
		}
		removeCounter();
		getXmlCountForElement(currentElement);
		if (countControlElement == getCounter()) {
			setIsCurrent(false);		
		}
		return countControlElement;
	}
	
	private void countForElement(Element rootNode, String currentElement) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getName().equals(currentElement)) {
				setCounter(1);
			} else {
				countForElement(node, currentElement);
			}
		}
	}
	
	private void removeCounter() {
		count = 0;
	}
	
	private void setAttributeCounter (int countAttribute) {
		attributeCounter = countAttribute;
	}
	
	private int getAttributeCounter() {
		return attributeCounter;
	}
	
	private void setCounter(int counter) {
		count = count + counter;
	}
	
	private int getCounter() {
		return count;
	}
	
	private void setCountElements (int counterForElements) {
		countElements = counterForElements;
	}
	
	private int getCountElements() {
		return countElements;
	}
	
	/**
	 * Check if current element is in file. If yes setIsCurrent().
	 */
	private void isCurrentElementInFile(String currentElement, int counter) {
		SAXBuilder builder = new SAXBuilder();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		File xmlFile = new File(navigationView.getXmlPath());	
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (rootNode.getName().equals(currentElement)) {
				setIsCurrent(true);
			} else {
				setCountElements(counter);
				findCurrentElement(rootNode, currentElement);
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}		
	}
	
	/**
	 * Recursion. Find if current element is in file. If yes, setIsCurrent(true).
	 */
	private void findCurrentElement(Element rootNode, String currentElement) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getName().equals(currentElement)) {
				if (getCountElements() == 0) {
					setIsCurrent(true);					
				} else {
					setCountElements(getCountElements() -1);
					findCurrentElement(node, currentElement);
				}
			} else if (!getIsCurrent()){
				findCurrentElement(node, currentElement);
			}
		}
	}
	
	/**
	 * Save Attribute value to xml file.
	 */
	private void saveAttributeToExistingFile(int counter, String current, TreeItem currentElement, String attributeValue) {
		SAXBuilder builder = new SAXBuilder();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		File xmlFile = new File(navigationView.getXmlPath());	 
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			
			if (getNodeValue(rootNode).equals(currentElement.getText())) {
				if (counter == 0) {
					Attribute attribute = new Attribute("name", attributeValue);
					rootNode.setAttribute(attribute);
					counter = counter - 1;
				} else {
					counter = counter -1;
				}
			} else {
				setAttributeCounter(counter);
				findAndSaveElementForAttribute(rootNode, current, currentElement, attributeValue);
			}
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(document, new FileWriter(navigationView.getXmlPath()));
			System.out.println("File Saved!");
		} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
		}
	}
	
	private void findAndSaveElementForAttribute(Element rootNode, String current, TreeItem currentElement, String attributeValue) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (getNodeValue(node).equals(currentElement.getText())) {
//				if (getAttributeCounter() == 0) {
					Attribute attribute = new Attribute("name", attributeValue);
					node.setAttribute(attribute);
					setAttributeCounter(getAttributeCounter() - 1);
					break;
//				} else {
//					setAttributeCounter(getAttributeCounter() - 1);
//					findAndSaveElementForAttribute(node, current, currentElement, attributeValue);
//				}
			} else {
				findAndSaveElementForAttribute(node, current, currentElement, attributeValue);
			}
		}
	}
	
	/**
	 * Save value to xml file.
	 * @param currentControl 
	 */
	private void saveValueToExistingFile(int counter, TreeItem currentElement, String label, String attributeValue, boolean isCurrentElementInFile, int currentControl) {
		SAXBuilder builder = new SAXBuilder();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		File xmlFile = new File(navigationView.getXmlPath());	 
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			
			if (getNodeValue(rootNode).equals(currentElement.getText())) {
				if (isCurrentElementInFile && counter == 0) {
					List<Element> children = rootNode.getChildren();
					for (Element element : children) {
						if (element.getName().equals(label)) {
							if (counter == 0 && currentControl == 0) {
								if (attributeValue != null) {
									element.setText(attributeValue); //changed
								}	
								counter = counter - 1;
							} else {
								currentControl = currentControl - 1;	
								counter = counter - 1;
							}
						}
					}
				} else if (!isCurrentElementInFile && counter == 0){
					Element newElement = new Element(label);
					if (attributeValue != null) {
//						Attribute attribute = new Attribute("name", attributeValue);
//						newElement.setAttribute(attribute);
						newElement.setText(attributeValue); //changed
					}
					rootNode.addContent(newElement);
				}
			} else {
				findAndSaveElementForValues(rootNode, currentElement, label, attributeValue, isCurrentElementInFile, currentControl);
			}
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(document, new FileWriter(navigationView.getXmlPath()));
			System.out.println("File Saved!");
		} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
		}
	}
	
	private void findAndSaveElementForValues(Element rootNode, TreeItem current, String label, String attributeValue, boolean isCurrentElementInFile, int currentControl) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getName().equals(changeItem(current.getText()))) {
				if (node.getAttributeValue("name").equals(getAttributeChangeItem(current.getText()))) {
					setIsCurrent(node, label);
					if (getIsCurrent()) {
						List<Element> children = node.getChildren();
						for (Element element : children) {
							if (element.getName().equals(label)) {
								if (currentControl == 0) {
									if (attributeValue != null) {
										if (label.equals("code")) {
											CDATA cdata = new CDATA(attributeValue);
											element.setContent(cdata);
										} else {
											element.setText(attributeValue); //changed											
										}
									}								
								} else {
									currentControl = currentControl - 1;						
								}
							}
						}
					} else {
						Element newElement = new Element(label);
						if (!attributeValue.equals("")) {
//						Attribute attribute = new Attribute("name", attributeValue);
//						newElement.setAttribute(attribute);
							if (label.equals("code")) {
								CDATA cdata = new CDATA(attributeValue);
								newElement.setContent(cdata);
							} else {
								newElement.setText(attributeValue); //changed								
							}
						}
						node.addContent(newElement);					
					}		
				}
			} else {
				findAndSaveElementForValues(node, current, label, attributeValue, isCurrentElementInFile, currentControl);
			}
		}
	}

	private String getNodeValue(Element node) {
		String nodeValue = "";
		if (node.getAttributeValue("name") == "") {
			nodeValue = node.getName();			
		} else if (node.getAttributeValue("name") == null) {
			nodeValue = node.getName();
		} else {
			nodeValue = node.getName() + "=" + node.getAttributeValue("name");
		}
		return nodeValue;
	}
	
	private String changeItem(String item) {
		if (item.contains("=")) {
			String[] changeItem = item.split("=");
			return changeItem[0];			
		} else {
			return item;
		}
	}
	
	private String getAttributeChangeItem(String item) {
		if (item.contains("=")) {
			String[] changeItem = item.split("=");
			return changeItem[1];
		} else {
			return item;
		}
	}
	
	 private String returnFirstLetterUpperCase(String string) {
		 String temp = "" + string.charAt(0);
		 temp = temp.toLowerCase();
		 return temp + string.substring(1, string.length()-2);
	 }
	 
	 private void setIsCurrent(Element node, String label) {
		isCurrent = false;
		List<Element> children = node.getChildren();
		for (Element child : children) {
			if (child.getName().equals(label)) {
				isCurrent = true;
				break;
			}
		}
	 }
	
	 private void setIsCurrent(boolean current) {
		 isCurrent = current;
	 }
	
	 private boolean getIsCurrent() {
		 return isCurrent;
	 }
	 
	 private void removeIsCurrent() {
		 setIsCurrent(false);
	 }
	 
	private String BooleanToString(boolean bool) {
		return String.valueOf(bool);
	}
		
	private String IntegerToString(int integer) {
		return String.valueOf(new Integer(integer));
	}
}