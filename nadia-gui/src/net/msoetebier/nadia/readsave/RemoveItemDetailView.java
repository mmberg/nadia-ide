package net.msoetebier.nadia.readsave;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import net.msoetebier.nadia.ExceptionHandler;
import net.msoetebier.nadia.Singleton;
import net.msoetebier.nadia.view.NavigationView;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class RemoveItemDetailView {
	private int countItem;

	 public void removeItemFromXmlFile(int countElements, int countItem, String parentItem, String item) {
		 	NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
			File xmlFile = new File(navigationView.getXmlPath());	 
			SAXBuilder builder = new SAXBuilder();
			try {
				Document document = (Document) builder.build(xmlFile);
				Element rootNode = document.getRootElement();
				if (rootNode.getName().equals(changeItem(parentItem))) {
					List<Element> children = rootNode.getChildren();
					for (Element child : children) {
						if (child.getName().equals(item)) {
							if (countItem == 0) {
								rootNode.detach();						
							}
							countItem = countItem - 1;							
						}
					}
				} else {
					setCountItem(countItem);
					findAndRemoveItem(rootNode, parentItem, item);
				}
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(document, new FileWriter(navigationView.getXmlPath()));
				System.out.println("File Saved!");
			 } catch (Exception exception) {
				new ExceptionHandler(exception.getMessage());
			}
	 }
	 
	 private void findAndRemoveItem(Element children, String parentItem, String item) {
			List<Element> list = children.getChildren();
			for (Element element : list) {
				if (element.getName().equals(changeItem(parentItem))) {
					if (element.getAttributeValue("name").equals(attributeValue(parentItem))) {
						List<Element> childrenElements = element.getChildren();
						for (Element childrenElement : childrenElements) {
							if (childrenElement.getName().equals(item)) {
								if (getCountItem() == 0) {
									childrenElement.detach();
									setCountItem(getCountItem() - 1);
									break;
								}
								setCountItem(getCountItem() - 1);								
							}
						}
					}
				} else {
					findAndRemoveItem(element, parentItem, item);
				}
			}
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
	 
	 private void setCountItem(int counter) {
		 countItem = counter;
	 }
	 
	 private int getCountItem() {
		 return countItem;
	 }
}
