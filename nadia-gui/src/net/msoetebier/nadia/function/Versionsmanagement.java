package net.msoetebier.nadia.function;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	
	public void versionsManagement(String schemaPath, String xmlPath, Map<String, String> versionsDialogMap) {
		String schemaVersionsNumber = getSchemaVersionNumber(schemaPath);
		String xmlVersionsNumber = getXmlVersionsNumber(xmlPath);
		if (!schemaVersionsNumber.equals(xmlVersionsNumber)) {
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
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			String targetNamespace = "";
			targetNamespace = rootNode.getNamespace("n").getURI();
//			List<Namespace> namespaces = rootNode.getAdditionalNamespaces();
//			for (Namespace namespace : namespaces) {
//				if (namespace.getPrefix().equals("n")) {
//					targetNamespace = namespace.getURI();
//				}
//			}
			if (targetNamespace.length() >= 3) {
				String threeChar = targetNamespace.substring(targetNamespace.length()-3, targetNamespace.length());
				if (threeChar.matches("[0-9].[0-9]")) {
					version = threeChar;
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
	
	/**
	 * Sets the XML file to the new schema structure. 
	 * Delete, with detach(), XML elements if they are not in the schema structure.
	 * Add elements in XML file if needed. 
	 */
	private void setXmlFileToNewSchemaStructure(String schemaPath, String xmlPath) {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(xmlPath);	 
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (!parser.elementIsInSchema(rootNode, schemaPath, true)) {
				rootNode.detach();
			} else if (!parser.elementIsAllowedInSchema(rootNode.getName(), 1, schemaPath, true)){
				rootNode.detach();
			} else {				
				setSchemaStructureForElements(rootNode, rootNode, schemaPath);
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
					parent.getChildren().add(new Element(childString));
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

	private int getMinOccurs(String item, String schemaPath) throws SAXException, IOException {
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

	private void setSchemaStructureForElements(Element parent, Element rootNode, String schemaPath) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			try {
				if (!parser.elementIsInSchema(node, schemaPath, getFirstLevel(node.getName(), schemaPath))) {
					node.detach();
					setSchemaStructureForElements(parent, parent, schemaPath);
					break;
				} else if (!parser.elementIsAllowedInSchema(node.getName(), countElements(node.getName(), list), schemaPath, getFirstLevel(node.getName(), schemaPath))) {
					node.detach();
					setSchemaStructureForElements(parent, parent, schemaPath);
					break;
				}
			} catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
			setSchemaStructureForElements(parent, node, schemaPath);
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
}
