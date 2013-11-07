package net.msoetebier.nadia.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.msoetebier.nadia.ExceptionHandler;
import net.msoetebier.nadia.Perspective;
import net.msoetebier.nadia.Singleton;
import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.parser.ParserForDetails;
import net.msoetebier.nadia.readsave.AddItemView;
import net.msoetebier.nadia.readsave.RemoveItemView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

/**
 * This view shows the structure of schema elements. This class is contributed through
 * the plugin.xml.
 */
public class View extends ViewPart {
	public static final String ID = "nadia-gui.view";
	NavigationView navigationView = (NavigationView) 
            Perspective.getView(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),  NavigationView.ID);
	DetailView detailView = (DetailView) 
            Perspective.getView(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),  DetailView.ID);
	private Parser parser = new Parser();
	private TreeItem oldCurrent;
	private String attribute = "";
	private boolean isUnique;
	private int count, countNumber;
	private boolean itemFound;
	
	/**
	 * This will allow us to create the viewer and initialize
	 * it. It also puts the different views in singleton.
	 */
	@Override
	  public void createPartControl(Composite parent) {
		putViewPartsToSingleton();
		final Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		setTreeContent(tree);
		addMouseListener(tree);
		addDragListener(tree);
	  }
	
	private void setTreeContent(Tree tree) {
		TreeItem treeItem = new TreeItem(tree, 0);
		treeItem.setText("Main");
		try {
			addComplexTypesToMap(readXmlFileToSetTreeContent(), treeItem);
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	private Element readXmlFileToSetTreeContent() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(navigationView.getXmlPath());
		Document document = (Document) builder.build(xmlFile);
		return document.getRootElement();
	}
	
	private String getAttributeForItem(int counter, TreeItem treeItem, String item) {
		removeAttribute();
		try {
			Element rootNode = readXmlFileToSetTreeContent();
			if (rootNode.getName().equals(item)) {
				if (counter == 0) {
					if (rootNode.getAttribute("name") != null) { //TODO
						setAttribute(rootNode.getAttributeValue("name"));
					}
				}
			} else {
				setCounter(counter);
				setAttributeChildrenFromXmlFile(treeItem, rootNode, item);
			}
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return getAttribute();
	}
	
	private void setAttributeChildrenFromXmlFile(TreeItem treeItem, Element rootNode, String item) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getName() != null) {
				if (node.getName().equals(item)) {
					if (getCounter() == 0) {
						if (node.getAttributeValue("name") != null) { //TODO
							if (getAttribute().equals("")) {
								setAttribute(node.getAttributeValue("name"));							
							}
							setCounter(getCounter() - 1);
							break;
						}					
					} else {
						setCounter(getCounter() - 1);
					}
				} else {
					setAttributeChildrenFromXmlFile(treeItem, node, item);
				}						
			}
		}
	}
	
	/**
	 * Add complex types to tree. 
	 */
	private void addComplexTypesToMap(Element rootNode, TreeItem treeItem) {
		try {
			if (parser.checkIfTypIsComplexForElement(rootNode.getName(), true, navigationView.getSchemaPath())) {
				TreeItem newItem = new TreeItem(treeItem, SWT.NONE);
				if (rootNode.getAttributeValue("name") == null) {
					newItem.setText(rootNode.getName());					
				} else if (rootNode.getAttributeValue("name").equals("")){
					newItem.setText(rootNode.getName());					
				} else {
					newItem.setText(rootNode.getName() +  "=" + rootNode.getAttributeValue("name"));
				}
			    addChildrenFromXmlFile(rootNode, newItem);
			} 
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
	}
	
	/**
	 * Add children elements from the selected xml file depending if they are complex types to the tree.
	 */
	private void addChildrenFromXmlFile(Element rootNode, TreeItem treeItem) throws SAXException, IOException {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (parser.checkIfTypIsComplexForElement(node.getName(), false, navigationView.getSchemaPath())) {
				TreeItem newItem = new TreeItem(treeItem, SWT.NONE);
				if (node.getAttributeValue("name") == null) {
					newItem.setText(node.getName());					
				} else if (node.getAttributeValue("name").equals("")){
					newItem.setText(node.getName());					
				} else {
					newItem.setText(node.getName() +  "=" + node.getAttributeValue("name"));
				}
				addChildrenFromXmlFile(node, newItem);
			}
		}
	}
	
	private void putViewPartsToSingleton() {
		Singleton.getInstance().put("NavigationView.ID",navigationView);
		Singleton.getInstance().put("View.ID",this);
		Singleton.getInstance().put("DetailView.ID",detailView);
	}

	/**
 	 * Add MouseListener to click on a element in the tree.
	 */
	private void addMouseListener(final Tree tree) {
		tree.addMouseListener(new MouseListener() {
			private static final long serialVersionUID = -1417718092429208348L;
			
			@Override
			public void mouseUp(MouseEvent event) {	
			}		
			@Override
			public void mouseDown(MouseEvent event) { //update NavigationView
				if (event.button == 3) {
					treeMenuManager(event);			
				} else {
					Point point = new Point(event.x, event.y);
			        TreeItem item = tree.getItem(point);
			        System.out.println("Mouse down: " + item.getText());
			        	boolean firstLevel = false;
			        		try {
			        			List<String> parentElements = parser.getParentElements(navigationView.getSchemaPath());
			        			for (String element : parentElements) {
			        				if (element.equals(changeItem(item.getText()))) {
			        					firstLevel = true;
			        				}
			        			}
			        			if (getOldCurrent() != null && !getOldCurrent().getText().equals("Main")) {
			        				checked(item, firstLevel);
			        			} else {
			        				itemChecked(item, firstLevel);
			        			}
							}  catch (Exception exception) {
								new ExceptionHandler(exception.getMessage());
							}
				}   		
			}
			
			private void checked(TreeItem item, boolean firstLevel) throws Exception {
		        if (item != null && detailView.getName(getOldCurrent()) != "" && isUnique(getOldCurrent(), detailView.getName(getOldCurrent()))) {
//		        if (item != null && detailView.getName(item) != "" && isUnique(item, detailView.getName(item))) {
//		        	detailView.removeBackgroundColorForName(); 
		        	itemChecked(item, firstLevel);
		        } else {
//		        	detailView.setRedBackgroundColorForName(countElements(item), item.getText()); //TODO
    				detailView.updateElements(countElements(getOldCurrent()), getOldCurrent().getText(), parser.getTypesForElement(changeItem(getOldCurrent().getText()), firstLevel, navigationView.getSchemaPath(), false), false, true);
	        	}
			}
			
			private void itemChecked(TreeItem item, boolean firstLevel) throws Exception {
				if (changeItem(item.getText()).equals("Main")) {
    				if (getOldCurrent() != null) {
						if (!changeItem(getOldCurrent().getText()).equals("Main")) { 
							detailView.saveDetails(getOldCurrent(), parser.getTypesForElement(changeItem(getOldCurrent().getText()), firstLevel, navigationView.getSchemaPath(), false), countElements(getOldCurrent()));
						}
						if (getAttributeForItem(countElements(getOldCurrent()), getOldCurrent(), changeItem(getOldCurrent().getText())) != "") { //TODO vorher countElements(item)
							getOldCurrent().setText(changeItem(getOldCurrent().getText()) + "=" + getAttributeForItem(countElements(getOldCurrent()), getOldCurrent(),changeItem(getOldCurrent().getText())));         					
						} else {
							getOldCurrent().setText(changeItem(getOldCurrent().getText()));
						}      				
    				}
    				navigationView.setViewerInput(parser.getParentElements(navigationView.getSchemaPath()));
    				//detailView.updateElements(countElements(item), item.getText(), parser.getTypesForElement(changeItem(item.getText()), firstLevel, navigationView.getSchemaPath(), false), false, false);
    				setOldCurrent(item);
    			} else {
    				if (!changeItem(getOldCurrent().getText()).equals("Main")) {
    					detailView.saveDetails(getOldCurrent(), parser.getTypesForElement(changeItem(getOldCurrent().getText()), firstLevel, navigationView.getSchemaPath(), false), countElements(getOldCurrent()));
    				}
    				if (getAttributeForItem(countElements(getOldCurrent()), getOldCurrent(), changeItem(getOldCurrent().getText())) != "") {
    					getOldCurrent().setText(changeItem(getOldCurrent().getText()) + "=" + getAttributeForItem(countElements(getOldCurrent()), getOldCurrent(),changeItem(getOldCurrent().getText())));			        					
    				} else {
    					getOldCurrent().setText(changeItem(getOldCurrent().getText()));
    				}
    				navigationView.setViewerInput(parser.getTypesForElement(changeItem(item.getText()), firstLevel, navigationView.getSchemaPath(), true));
    				detailView.updateElements(countElements(item), item.getText(), parser.getTypesForElement(changeItem(item.getText()), firstLevel, navigationView.getSchemaPath(), false), false, false);
    				setOldCurrent(item);
    			}
			}
			
			/**
			 * This method organizes the right click with the mouse.
			 */
			private void treeMenuManager(MouseEvent event) {		        
				Map<String, String> rightClickMap = navigationView.getLanguageManagement().get("rightClick");	
				Menu popupMenu = new Menu(tree);
		        MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
		        deleteItem.setText(rightClickMap.get("delete"));
		        tree.setMenu(popupMenu);
		        final Point point = new Point(event.x, event.y);
		        deleteItem.addSelectionListener(new SelectionListener() {
					private static final long serialVersionUID = -5412544692884362472L;
					@Override
					public void widgetSelected(SelectionEvent selection) {
				        TreeItem item = tree.getItem(point);
				        if (!item.equals("Main")) {
				        	RemoveItemView removeItem = new RemoveItemView();
				        	removeItem.deleteItem(tree, item, navigationView.getXmlPath());
				        }
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
			}
			@Override
			public void mouseDoubleClick(MouseEvent event) {
			}
		});
	}

	/**
 	 * A drag listener adds the new tree item to the tree.
	 */
	private void addDragListener(final Tree tree) {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
	    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
		final DragSource source = new DragSource(tree, operations);
	    source.setTransfer(types);
	    final TreeItem[] dragSourceItem = new TreeItem[1];
	    source.addDragListener(new DragSourceListener() {
			private static final long serialVersionUID = 5723284367857899358L;
			
			public void dragStart(DragSourceEvent event) {
		        TreeItem[] selection = tree.getSelection();
		        if (selection.length > 0 && selection[0].getItemCount() == 0) {
		          event.doit = true;
		          dragSourceItem[0] = selection[0];
		        } else {
		          event.doit = false;
		        }
		      };

	      public void dragSetData(DragSourceEvent event) {
	        event.data = dragSourceItem[0].getText();
	      }

	      public void dragFinished(DragSourceEvent event) {
	        if (event.detail == DND.DROP_MOVE)
	          dragSourceItem[0].dispose();
	        dragSourceItem[0] = null;
	      }
	    });

	    DropTarget target = new DropTarget(tree, operations);
	    target.setTransfer(types);
	    target.addDropListener(new DropTargetAdapter() {
			private static final long serialVersionUID = -4455636545779194594L;
			public void dragOver(DropTargetEvent event) {
			}
	    
			public void drop(DropTargetEvent event) {
		        if (event.data == null) {
		          event.detail = DND.DROP_NONE;
		          return;
		        }
		        String text = (String) event.data;
		        if (event.item == null) {
		          TreeItem item = new TreeItem(tree, SWT.NONE);
		          item.setText(text);
		        } else {
		          TreeItem item = (TreeItem) event.item;
		          TreeItem parent = item.getParentItem();
		          try {
		        	 if (isChildrenOfElement(text, changeItem(item.getText())) && elementIsAllowedInSchema(text, countElements(text, item), navigationView.getSchemaPath(), getFirstLevel(text))) {    
					  AddItemView addItem = new AddItemView();
					  addItem.saveItemToXmlFile(item, (String) event.data);
					  boolean firstLevel = false;
					  List<String> parentElements;
					  parentElements = parser.getParentElements(navigationView.getSchemaPath());
					  for (String element : parentElements) {
						if (element.equals(item.getText())) {
							firstLevel = true;
						}
					  }  
					  detailView.saveDetails(getOldCurrent(), parser.getTypesForElement(changeItem(getOldCurrent().getText()), firstLevel, navigationView.getSchemaPath(), false), countElements(getOldCurrent()));
//						setOldCurrent(item);

					  if (parent != null) {
					    TreeItem[] items = parent.getItems();
					    for (int i = 0; i < items.length; i++) {
					      if (items[i] == item) {
					        break;
					      }
					    }
					      TreeItem newItem = new TreeItem(item, SWT.NONE);
					      newItem.setText(text);
//					      setOldCurrent(newItem);
					  } else {
					    TreeItem[] items = tree.getItems();
//		            int index = 0;
					    for (int i = 0; i < items.length; i++) {
					      if (items[i] == item) {
//		                index = i;
					        break;
					      }
					    }
					
					      TreeItem newItem = new TreeItem(item, SWT.NONE);
					      newItem.setText(text);
//					      setOldCurrent(newItem);
					  }

					}
				}  catch (Exception exception) {
					new ExceptionHandler(exception.getMessage());
				}}
	      }
	    });
	}
	
	private boolean isChildrenOfElement(String text, String parent) {
	    boolean childrenOfElement = false;
		List<String> parentElements = new ArrayList<String>();		
		try {
			if (parent.equals("Main")) {
				parentElements = parser.getParentElements(navigationView.getSchemaPath());
			} else {
				parentElements = parser.getTypesForElement(parent, isFirstLevel(parent), navigationView.getSchemaPath(), true);
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
	
	private boolean isFirstLevel(String item) {
		boolean firstLevel = false;
		List<String> parentElements;
		try {
			parentElements = parser.getParentElements(navigationView.getSchemaPath());
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
	
	private void setOldCurrent(TreeItem treeItem) {
		oldCurrent = treeItem;
	}
	
	private TreeItem getOldCurrent() {
		return oldCurrent;
	}
	
	private void removeAttribute() {
		attribute = "";
	}
	
	private void setAttribute(String attributeString) {
		attribute = attributeString;
	}
	
	private String getAttribute() {
		return attribute;
	}
	
	private String changeItem(String item) {
		if (item.contains("=")) {
			String[] changeItem = item.split("=");
			return changeItem[0];			
		} else {
			return item;
		}
	}
	
	private int countElements(String item, TreeItem parent) {
		int countElements = 0;
		TreeItem[] childrenItem = parent.getItems();
		for (TreeItem children : childrenItem) {
			if (changeItem(children.getText()).equals(changeItem(item))) {
				if (!children.getText().equals(item)) {
					countElements = countElements + 1;					
				}
			}
		}
		return countElements;
	}
	
	public boolean elementIsAllowedInSchema(String item, int countItem, String schemaPath, boolean firstLevel) throws SAXException, IOException {
		boolean isAllowed = false;
		int neededNumber = getMaxNumber(item, schemaPath, firstLevel);
		if (countItem <= neededNumber) {
			isAllowed = true;
		}
		return isAllowed;
	}
	
	private int getMaxNumber(String item, String schemaPath, boolean firstLevel) throws SAXException, IOException {
		int number = 0;
		ParserForDetails parserForDetails = new ParserForDetails();
		Map<Object, String> details = parserForDetails.getMapForItem(item, firstLevel, schemaPath);
		String minOccurs = details.get("minOccurs");
		String maxOccurs = details.get("maxOccurs");
		
		if (minOccurs == null && maxOccurs == null) {
			number = 1;
		} else if (maxOccurs.equals("-1")) {
			number = Integer.MAX_VALUE;
		} else if (maxOccurs != null && !maxOccurs.equals("-1")) {
			number = Integer.parseInt(maxOccurs);
		}
		return number;
	}
	
	private boolean getFirstLevel(String item) {
		boolean firstLevel = false;
		List<String> parentElements;
		try {
			parentElements = parser.getParentElements(navigationView.getSchemaPath());
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
	
	public int countElements(TreeItem treeItem) {
//		int count = 0;
//		TreeItem parentItem = treeItem.getParentItem();
//		if (parentItem != null) {
//			TreeItem[] items = parentItem.getItems();
//			for (TreeItem item : items) {
//				if (changeItem(item.getText()).equals(changeItem(treeItem.getText()))) {
//					if (!item.getText().equals(treeItem.getText())) {
//						count = count + 1;
//					} else {
//						break;
//					}
//				}
//			}
//		}
//		return count;
		count = 0;
		setItemFound(false);
		Tree tree = treeItem.getParent();
		TreeItem[] items = tree.getItems();
		for (TreeItem item : items) {
			if (changeItem(item.getText()).equals(changeItem(treeItem.getText()))) {
				if (item.getText().equals(treeItem.getText())) {
					setItemFound(true);
				} else if (!item.getText().equals(treeItem.getText()) && !getItemFound()) {
					count = count + 1;
				} 
			}
			childCounter(item, treeItem);
		}
		return count;
	}
	
	private void childCounter(TreeItem item, TreeItem treeItem) {
		TreeItem[] child = item.getItems();
		for (TreeItem childItem : child) {
			if (changeItem(childItem.getText()).equals(changeItem(treeItem.getText()))) {
				if (childItem.getText().equals(treeItem.getText())) {
					setItemFound(true);
				} else if (!childItem.getText().equals(treeItem.getText()) && !getItemFound()) {
					count = count + 1;
				} 
			}
			childCounter(childItem, treeItem);
		}
	}
	
	private void setCounter(int counterForNumber) {
		countNumber = counterForNumber;
	}
	
	private int getCounter() {
		return countNumber;
	}
	
	private void setItemFound(boolean found) {
		itemFound = found;
	}
	
	private boolean getItemFound() {
		return itemFound;
	}
	
	private boolean isUnique(TreeItem oldCurrent, String text) {
		isUnique = true;
		SAXBuilder builder = new SAXBuilder();
		NavigationView navigationView = (NavigationView) Singleton.getInstance().get("NavigationView.ID");
		File xmlFile = new File(navigationView.getXmlPath());	
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			if (rootNode.getAttributeValue("name") != null) {
				if (rootNode.getAttributeValue("name").equals(text) && !rootNode.getName().equals(changeItem(oldCurrent.getText()))) {
					isUnique = false;
				} 				
			}
			isUniqueForChildren(oldCurrent, rootNode, text);
		} catch (Exception exception) {
			new ExceptionHandler(exception.getMessage());
		}
		return isUnique;
	}
	
	private void isUniqueForChildren(TreeItem oldCurrent, Element rootNode, String text) {
		List<Element> list = rootNode.getChildren();
		for (Element node : list) {
			if (node.getAttributeValue("name") != null) {
				if (node.getAttributeValue("name").equals(text) && !node.getName().equals(changeItem(oldCurrent.getText()))) {
					isUnique = false;
					break;
				} else {
					isUniqueForChildren(oldCurrent, node, text);
				}				
			}
		}
	}
	
	@Override
	public void setFocus() {
	}
}