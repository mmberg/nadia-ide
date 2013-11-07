package net.msoetebier.nadia.readsave;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import net.msoetebier.nadia.ExceptionHandler;
import net.msoetebier.nadia.Perspective;
import net.msoetebier.nadia.view.NavigationView;

import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class AddItemView {
	NavigationView navigationView = (NavigationView) 
            Perspective.getView(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),  NavigationView.ID);
	
	/**
	 * Save an item to a new XML-file or to an existing file.
	 */
	public void saveItemToXmlFile(TreeItem parent, String current) {
		File xmlFile = new File(navigationView.getXmlPath());
		if (xmlFile.exists()) {
			if (parent.getParentItem() == null) {
				saveItemToFile(current);
			} else {
				saveItemIntoExistingFile(parent, current);
			}
		} else { 
			saveItemToFile(current);
		}
	}
	
	/**
	 * Save an new item to the XML-file depending on the name and attributeName. 
	 * First add the element to the document, then write the file.
	 */
	private void saveItemToFile(String current) {
		Element element = new Element(current);
		Document doc = new Document(element);
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		try {
			xmlOutput.output(doc, new FileWriter(navigationView.getXmlPath()));
		}  catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		System.out.println("File Saved!");
	}
	
	/**
 	 * This function adds an item to an existing file. 
	 * First read the file, then check if the rootNode has the same attributeName. 
	 * If it does, add the item as a child. Also check child nodes by findParentElement.
	 * Finally save the new document with the new item. 
	 */
	private void saveItemIntoExistingFile(TreeItem parent, String current) {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(navigationView.getXmlPath());	 
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
				
			if (getNodeValue(rootNode).equals(parent.getText())) {
				Element newElement = new Element(current);
				rootNode.addContent(newElement);
			} else {
				findParentElement(rootNode, parent, current);
			}
				
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(document, new FileWriter(navigationView.getXmlPath()));
			System.out.println("File Saved!");
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private void findParentElement(Element rootNode, TreeItem parent, String current) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (getNodeValue(node).equals(parent.getText())) {
				Element newElement = new Element(current);
				node.addContent(newElement);
				break;
			} else {
				findParentElement(node, parent, current);
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
}
