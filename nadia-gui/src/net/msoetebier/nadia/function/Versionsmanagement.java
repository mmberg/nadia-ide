package net.msoetebier.nadia.function;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.msoetebier.nadia.ExceptionHandler;
import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.parser.ParserForDetails;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;

/**
 * This class organizes the versions management.
 */
public class Versionsmanagement {
	XSParticle childParticle;
	Parser parser = new Parser();
	boolean isIncompatible, unique;
	
	public void versionsManagement(String schemaPath, String xmlPath, Map<String, String> versionsDialogMap) {
		String schemaVersionsNumber = getSchemaVersionNumber(schemaPath);
		String xmlVersionsNumber = getXmlVersionsNumber(xmlPath);
		if (!schemaVersionsNumber.equals(xmlVersionsNumber) || isXmlFileIncompatibel(schemaPath, xmlPath)) {
			VersionsDialog dialog = new VersionsDialog(new Shell(Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent(), SWT.NO_TRIM), versionsDialogMap);
			dialog.create();
			if (dialog.open() == Window.OK) {
				setXmlFileToNewSchemaStructure(schemaPath, xmlPath);				
			}
		}
	}
	
	/**
	 * Return schema version number if it matches [0-9].[0-9]. 
	 */
	private String getSchemaVersionNumber(String schemaPath) {
		String version = "";
		try {
			XSSchemaSet schemaSet = parser.parseXMLSchemaWithXSOM(schemaPath);
			Iterator<XSSchema> iterator = schemaSet.iterateSchema();
			while (iterator.hasNext()) {
					XSSchema xsSchema = (XSSchema) iterator.next();
					String targetNamespace = xsSchema.getTargetNamespace();
					if (targetNamespace.length() >= 3) {
						String threeChar = targetNamespace.substring(targetNamespace.length()-3, targetNamespace.length());
						if (threeChar.matches("[0-9].[0-9]")) {
							version = threeChar;
							break;
						}					
				}
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return version;
	}
	
	/**
	 * Return XML version number if it matches [0-9].[0-9]. 
	 */
	private String getXmlVersionsNumber(String xmlPath) {
		String version = "";
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(xmlPath);	 
		try {
			if (xmlFile.exists()) {
				Document document = (Document) builder.build(xmlFile);
				if (document.hasRootElement()) {
					Element rootNode = document.getRootElement();
					String targetNamespace = "";
					if (rootNode.getNamespace("n") != null) {
						targetNamespace = rootNode.getNamespace("n").getURI();
						if (targetNamespace.length() >= 3) {
							String threeChar = targetNamespace.substring(targetNamespace.length()-3, targetNamespace.length());
							if (threeChar.matches("[0-9].[0-9]")) {
								version = threeChar;
							}
						}															
					}
				}
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return version;
	}
	
	/**
	 * Return target namespace of schema.
	 */
	private String getTargetNamespace(String schemaPath) {
		String targetNamespace = "";
		try {
			XSSchemaSet schemaSet = parser.parseXMLSchemaWithXSOM(schemaPath);
			Iterator<XSSchema> iterator = schemaSet.iterateSchema();
			while (iterator.hasNext()) {
					XSSchema xsSchema = (XSSchema) iterator.next();
					String namespace = xsSchema.getTargetNamespace();
					if (namespace.length() >= 3) {
						String threeChar = namespace.substring(namespace.length()-3, namespace.length());
						if (threeChar.matches("[0-9].[0-9]")) {
							targetNamespace = namespace;
							break;
						}	
					}
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return targetNamespace;
	}
	
	private boolean isXmlFileIncompatibel(String schemaPath, String xmlPath) {
		setIsIncompatible(false);
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(xmlPath);	 
		try {
			if (xmlFile.exists()) {
				Document document = (Document) builder.build(xmlFile);
				Element rootNode = document.getRootElement();
				if (!parser.elementIsInSchema(rootNode, schemaPath, true)) {
					setIsIncompatible(true);
				} else if (!parser.elementIsAllowedInSchema(rootNode.getName(), 1, schemaPath, true)){
					setIsIncompatible(true);
				} else if (rootNode.getName() == null) {
					setIsIncompatible(true);
				} else {				
					areChildElementsAllowed(rootNode, rootNode, schemaPath, xmlPath);
				}
				if (!getIsIncompatible()) {
					areElementNeeded(rootNode, schemaPath);
				}				
			} else {
				setIsIncompatible(true);
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return getIsIncompatible();
	}
	
	private void areElementNeeded(Element parent, String schemaPath) {
		try {
			List<String> elements = parser.getParentElements(schemaPath);
			for (String dialog : elements) {
				if (dialog.equals("dialog")) {
					areChildElementNeeded(parent, parent, dialog, schemaPath);										
				}
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private void areChildElementNeeded(Element rootNode, Element parent, String parentString, String schemaPath) {
		try {
			if (!getIsIncompatible()) {
				List<String> childListFromSchema = new ArrayList<String>();
				if (parentString.equals("dialog")) {
					childListFromSchema.addAll(parser.getTypesForElement(parentString, true, schemaPath, false));
					childListFromSchema.addAll(parser.getTypesForElement(parentString, true, schemaPath, true));	
				} else {
					childListFromSchema.addAll(parser.getTypesForElement(parentString, false, schemaPath, false));
					childListFromSchema.addAll(parser.getTypesForElement(parentString, false, schemaPath, true));
				}
				
				for (String childString : childListFromSchema) {
					List<Element> elementChilds = new ArrayList<Element>();
					if (parent.getChild(childString) != null) {
						elementChilds = parent.getChild(childString).getParentElement().getChildren();
					} 
					if (ifElementIsNeeded(childString, elementChilds, schemaPath)) {
						setIsIncompatible(true);
						break;
					} else if (countElements(childString, elementChilds) != 0){
						List<String> ifChildList = getChildrenList(childString, false, schemaPath);
						if (!ifChildList.isEmpty()) {
							addChildrenIfNeeded(rootNode, parent.getChild(childString), childString, schemaPath);
						}
					}
				}				
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}	
	
	/**
	 * Sets the XML file to the new schema structure. 
	 * Delete, with detach(), XML elements if they are not in the schema structure.
	 * Add elements in XML file if needed. 
	 */
	private void setXmlFileToNewSchemaStructure(String schemaPath, String xmlPath) {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(xmlPath);	 
		try {
			if (!xmlFile.exists()) {
				createXmlFile(xmlPath);
				xmlFile = new File(xmlPath);	 
			} 
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (!parser.elementIsInSchema(rootNode, schemaPath, true)) {
				rootNode.detach();
			} else if (!parser.elementIsAllowedInSchema(rootNode.getName(), 1, schemaPath, true)){
				rootNode.detach();
			} else if (!isUnique(rootNode, xmlPath) || rootNode.getAttributeValue("name") == null) {
				Attribute attribute = new Attribute("name", getRandomString());
				rootNode.setAttribute(attribute);
			} else {				
				setSchemaStructureForElements(rootNode, rootNode, schemaPath, xmlPath);
			}
			addElementsIfNeeded(rootNode, schemaPath);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			rootNode.removeNamespaceDeclaration(Namespace.getNamespace("n"));
			Namespace nameSpaceN = Namespace.NO_NAMESPACE;
			List<Namespace> namespaces = rootNode.getAdditionalNamespaces();
			for (Namespace namespace : namespaces) {
				if (namespace.getPrefix().equals("n")) {
					nameSpaceN = namespace;
				}
			}
			if (nameSpaceN.getPrefix().equals("n")) {
				rootNode.removeNamespaceDeclaration(nameSpaceN);
			}
			rootNode.setNamespace(Namespace.getNamespace("n", getTargetNamespace(schemaPath)));
			xmlOutput.output(document, new FileWriter(xmlPath));
			System.out.println("File Saved!");
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private void createXmlFile(String xmlFile) {
		Element rootElement = new Element("dialog");
		Document doc = new Document(rootElement);
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		try {
			xmlOutput.output(doc, new FileWriter(xmlFile));
		}  catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private void addElementsIfNeeded(Element parent, String schemaPath) {
		try {
			List<String> elements = parser.getParentElements(schemaPath);
			for (String dialog : elements) {
				if (dialog.equals("dialog")) {
					addChildrenIfNeeded(parent, parent, dialog, schemaPath);										
				}
			}
			} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
	}
	
	private void addChildrenIfNeeded(Element rootNode, Element parent, String parentString, String schemaPath) {
		try {
			List<String> childListFromSchema = new ArrayList<String>();
			if (parentString.equals("dialog")) {
				childListFromSchema.addAll(parser.getTypesForElement(parentString, true, schemaPath, false));
				childListFromSchema.addAll(parser.getTypesForElement(parentString, true, schemaPath, true));
			} else {
				childListFromSchema.addAll(parser.getTypesForElement(parentString, false, schemaPath, false));
				childListFromSchema.addAll(parser.getTypesForElement(parentString, false, schemaPath, true));
			}

			for (String childString : childListFromSchema) {
				List<Element> elementChilds = new ArrayList<Element>();
				if (parent.getChild(childString) != null) {
					elementChilds = parent.getChild(childString).getParentElement().getChildren();
				} 
				if (ifElementIsNeeded(childString, elementChilds, schemaPath)) {
					Element element = new Element(childString);
					if (parser.checkIfTypIsComplexForElement(childString, getFirstLevel(childString, schemaPath), schemaPath)) {
						Attribute attribute = new Attribute("name", getRandomString());
						element.setAttribute(attribute);						
					}
					parent.getChildren().add(element);
					addElementsIfNeeded(rootNode, schemaPath);
					break;
				} else if (countElements(childString, elementChilds) != 0){
					List<String> ifChildList = getChildrenList(childString, false, schemaPath);
					if (!ifChildList.isEmpty()) {
						addChildrenIfNeeded(rootNode, parent.getChild(childString), childString, schemaPath);
					}
				}
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private String getRandomString() {
		 SecureRandom random = new SecureRandom();
		 return new BigInteger(130, random).toString(32);
	}
	
	private List<String> getChildrenList(String childString, boolean firstLevel, String schemaPath) throws SAXException, IOException {
		List<String> childrenList = new ArrayList<String>();
		childrenList.addAll(parser.getTypesForElement(childString, false, schemaPath, false));
		childrenList.addAll(parser.getTypesForElement(childString, false, schemaPath, true));
		if (childrenList.contains(childString)) {
			childrenList.remove(childString);
		}
		return childrenList;
	}
	
	private boolean ifElementIsNeeded(String parentString, List<Element> children, String schemaPath) throws SAXException, IOException {
		boolean elementIsNeeded = false;
		if (countElements(parentString, children) < getMinOccurs(parentString, schemaPath)) {
			elementIsNeeded = true;
		}
		return elementIsNeeded;
	}

	public int getMinOccurs(String item, String schemaPath) throws SAXException, IOException {
		ParserForDetails parserForDetails = new ParserForDetails();
		Map<Object, String> details = parserForDetails.getMapForItem(item, getFirstLevel(item, schemaPath), schemaPath);
		String minOccurs = details.get("minOccurs");
		String maxOccurs = details.get("maxOccurs");
		if (minOccurs == null && maxOccurs == null) {
			return 1;
		} else if (minOccurs != null) {
			return Integer.parseInt(minOccurs);			
		} else {
			return 0;
		}
	}
	
	private void areChildElementsAllowed(Element parent, Element rootNode, String schemaPath, String xmlPath) {
		List<Element> list = rootNode.getChildren();
		if (!getIsIncompatible()) {
			for (Element node : list) {
				try {
					isUnique(node, xmlPath);
					if (!parser.elementIsInSchema(node, schemaPath, getFirstLevel(node.getName(), schemaPath))) {
						setIsIncompatible(true);
						break;
					} else if (!parser.elementIsAllowedInSchema(node.getName(), countElements(node.getName(), list), schemaPath, getFirstLevel(node.getName(), schemaPath))) {
						setIsIncompatible(true);
						break;
					} else if (!getIsUnique()) {
						setIsIncompatible(true);
					} else if (!isChildrenOfElement(node.getName(), node.getParentElement().getName(), schemaPath)) {
						setIsIncompatible(true);
					} else if (parser.checkIfTypIsComplexForElement(node.getName(), getFirstLevel(node.getName(), schemaPath), schemaPath) && node.getAttributeValue("name") == null) {
						setIsIncompatible(true);
					}
				} catch (Exception exception) {
					new ExceptionHandler(exception.getMessage());
				}
				areChildElementsAllowed(parent, node, schemaPath, xmlPath);
			}			
		}
	}
	
	private boolean isChildrenOfElement(String text, String parent, String schemaPath) {
	    boolean childrenOfElement = false;
		List<String> parentElements = new ArrayList<String>();		
		try {
			if (parent.equals("Main")) {
				parentElements = parser.getParentElements(schemaPath);
			} else {
				parentElements.addAll(parser.getTypesForElement(parent, isFirstLevel(parent, schemaPath), schemaPath, true));
				parentElements.addAll(parser.getTypesForElement(parent, isFirstLevel(parent, schemaPath), schemaPath, false));
			}
			for (String element : parentElements) {
				if (element.equals(text)) {
					childrenOfElement = true;
				}	
			}
		} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
		}
		return childrenOfElement;
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
	
	private boolean isUnique(Element node, String xmlPath) {
		setIsUnique(true);
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(xmlPath);	
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (rootNode.getAttributeValue("name") != null) {
				if (rootNode.getAttributeValue("name").equals(node.getAttributeValue("name")) && !rootNode.getText().equals(node.getText())) {
					setIsUnique(false);
				} 				
			}
			isUniqueForChildren(rootNode, node);
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return getIsUnique();
	}
	
	private void isUniqueForChildren(Element rootNode, Element element) {
		if (getIsUnique()) {
			List<Element> list = rootNode.getChildren();
			for (Element node : list) {
				if (node.getAttributeValue("name") != null) {
					if (node.getAttributeValue("name").equals(element.getAttributeValue("name")) && !node.getText().equals(element.getText())) {
						setIsUnique(false);
						break;
					} else {
						isUniqueForChildren(node, element);
					}				
				}
			}			
		}
	}

	private void setSchemaStructureForElements(Element parent, Element rootNode, String schemaPath, String xmlPath) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			try {
				if (!parser.elementIsInSchema(node, schemaPath, getFirstLevel(node.getName(), schemaPath))) {
					node.detach();
					setSchemaStructureForElements(parent, parent, schemaPath, xmlPath);
					break;
				} else if (!parser.elementIsAllowedInSchema(node.getName(), countElements(node.getName(), list), schemaPath, getFirstLevel(node.getName(), schemaPath))) {
					node.detach();
					setSchemaStructureForElements(parent, parent, schemaPath, xmlPath);
					break;
				} else if (!isUnique(node, xmlPath)) {
					Attribute attribute = new Attribute("name", getRandomString());
					node.setAttribute(attribute);
				}  else if (!isChildrenOfElement(node.getName(), node.getParentElement().getName(), schemaPath)) {
					node.detach();
					setSchemaStructureForElements(parent, parent, schemaPath, xmlPath);
					break;
				} else if (parser.checkIfTypIsComplexForElement(node.getName(), getFirstLevel(node.getName(), schemaPath), schemaPath) && node.getAttributeValue("name") == null){
					Attribute attribute = new Attribute("name", getRandomString());
					node.setAttribute(attribute);
				}
			} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
			setSchemaStructureForElements(parent, node, schemaPath, xmlPath);
		}
	}
	
	private int countElements(String item, List<Element> list) {
		int countElements = 0;
		for (Element node : list) {
			if (node.getName().equals(item)) {
				countElements = countElements + 1;
			}
		}
		return countElements;
	}
	
	private boolean getFirstLevel(String item, String schemaPath) throws SAXException, IOException {
		 boolean firstLevel = false;
		 List<String> parentElements = parser.getParentElements(schemaPath);
			for (String element : parentElements) {
				if (element.equals(item)) {
					firstLevel = true;
				}
			}
		 return firstLevel;
	 }
	
	private void setIsIncompatible(boolean incompatible) {
		isIncompatible = incompatible;
	}
	
	private boolean getIsIncompatible() {
		return isIncompatible;
	}
	
	private void setIsUnique(boolean isUnique) {
		unique = isUnique;
	}
	
	private boolean getIsUnique() {
		return unique;
	}
}