package net.msoetebier.nadia.readsave;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import net.msoetebier.nadia.ExceptionHandler;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class RemoveItemView {
	String xmlPath = "";
	
	public void deleteItem(Tree tree, TreeItem item, String xmlPath) { //item task=start
    	setXmlPath(xmlPath);
    	removeItemFromXmlFile(item);
    	item.dispose();
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
	
	private void removeItemFromXmlFile(TreeItem item) {
		File xmlFile = new File(getXmlPath());	 
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			
			if (getNodeValue(rootNode).equals(item.getText())) {
				rootNode.detach();
			} else {
				findAndRemoveItem(rootNode);
			}
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(document, new FileWriter(getXmlPath()));
			System.out.println("File Saved!");
		}  catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private void findAndRemoveItem(Element children) {
		List<Element> list = children.getChildren();
		for (Element element : list) {
			if (getNodeValue(element).equals(children.getText())) {
				element.detach();
				break;
			} else {
				findAndRemoveItem(element);
			}
		}
	}
	
	private void setXmlPath(String path) {
		xmlPath = path;
	}
	
	private String getXmlPath() {
		return xmlPath;
	}
}