package net.msoetebier.nadia.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSTerm;

public class Parser {
	private XSParticle childParticle;
	
	/**
	 * Parse XML-Schema with XSOM. 
	 */
	public XSSchemaSet parseXMLSchemaWithXSOM(String fileLocation) throws SAXException, IOException {
		XSOMParser parser = new XSOMParser();
		parser.parse(new File(fileLocation));		
		XSSchemaSet sset = parser.getResult();
		return sset;
	}
	
	/**
	 * Get first complex Types of a schema depending on the element. Iterate over elements: add element name to list
	 * @return a List with the parent elements
	 */
	public List<String> getParentElements(String fileLocation) throws SAXException, IOException {
		XSSchemaSet schemaSet = parseXMLSchemaWithXSOM(fileLocation);
		List <String> list  = new ArrayList<String>();
		Iterator <XSElementDecl> itrElements = schemaSet.iterateElementDecls();
		while(itrElements.hasNext()) { //find elements
			XSElementDecl xsElementDecl = (XSElementDecl) itrElements.next();
			XSComplexType xsComplexType = xsElementDecl.asElementDecl().getType().asComplexType();
			if (xsComplexType != null) {
				list.add(xsElementDecl.getName()); //add element(s)				
			}
		}
		return list;
	}
	
	public boolean checkIfTypIsComplexForElement(String item, boolean firstLevel, String fileLocation) throws SAXException, IOException {
		boolean complexType = false;
		XSSchemaSet schemaSet = parseXMLSchemaWithXSOM(fileLocation);
		Iterator <XSElementDecl> itrElements = schemaSet.iterateElementDecls();
		
		while(itrElements.hasNext()) {
		XSElementDecl xsElementDecl = (XSElementDecl) itrElements.next();
			if (xsElementDecl.getType().getName() != null) {
				if (xsElementDecl.getType().getName().equals(item) && firstLevel == true) {
					//check if it's a complexType
					XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
					if (xsComplexType != null) {
			    	  complexType = true;
			    	  }
				} else if (firstLevel == false) {
					XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
					if (xsComplexType != null) {
						XSContentType xsContentType = xsComplexType.getContentType();
					    XSParticle particle = xsContentType.asParticle();
					    childParticle = null;
					    findParticle(particle, item);
					    if (childParticle != null) {
							XSTerm pterm = childParticle.getTerm();
							if (pterm.isElementDecl()) {
								 if (pterm.asElementDecl().getType().isComplexType()) {
									 complexType = true;
								 }
							} else if (pterm.isModelGroup()) {
								 XSModelGroup xsModelGroup2 = pterm.asModelGroup();
								 XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
				            	 for(XSParticle xsParticleTemp : xsParticleArray){ 
					            	 XSTerm pterm2 = xsParticleTemp.getTerm();
									 if (pterm2.isElementDecl() && pterm2.asElementDecl().getType().isComplexType()) {
										complexType = true;
									 }
								 }
				            	
							}
						}
					}
				}
			} else if (firstLevel == true) {
				XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
				if (xsComplexType != null) {
					XSContentType xsContentType = xsComplexType.getContentType();
				    XSParticle particle = xsContentType.asParticle();
				    childParticle = null;
				    findParticle(particle, item);
				    if (childParticle != null) {
						XSTerm pterm = childParticle.getTerm();
						if(pterm.isElementDecl()) {
							 if (pterm.asElementDecl().getType().isComplexType()) {
								 complexType = true;
							 }
						}
					}				
				}
			}
		}
		return complexType;
	}
	 
	/**
	 * Find the children of an item and return them
	 * @return an string array with children
	 */
	public List<String> getTypesForElement(String item, boolean firstLevel, String fileLocation, boolean complexType) throws SAXException, IOException {
		XSSchemaSet schemaSet = parseXMLSchemaWithXSOM(fileLocation);
		Iterator <XSElementDecl> itrElements = schemaSet.iterateElementDecls();
		List<String> children = new ArrayList<String>();

		while(itrElements.hasNext()) {
		XSElementDecl xsElementDecl = (XSElementDecl) itrElements.next();
		List<String> elements = new ArrayList<String>();
			if (xsElementDecl.getType().getName() != null) {
				if (xsElementDecl.getType().getName().equals(item) && firstLevel == true) {
					//check if it's a complexType
					XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
					if (xsComplexType != null) {
			    	  XSContentType xsContentType = xsComplexType.getContentType();
			    	  XSParticle particle = xsContentType.asParticle();
			    	  //if it's a complexType, check if it has children
			    	  children = findChildren(particle, elements, true, complexType);
			    	  }
				} else if (firstLevel == false) {
					XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
					if (xsComplexType != null) {
						XSContentType xsContentType = xsComplexType.getContentType();
					    XSParticle particle = xsContentType.asParticle();
					    childParticle = null;
					    findParticleForDetail(particle, item);
						if (childParticle != null) {
							children = findChildren(childParticle, elements, true, complexType); 						
						}
					}
				}
			} else if (firstLevel == true) {
				XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
				if (xsComplexType != null) {
					XSContentType xsContentType = xsComplexType.getContentType();
				    XSParticle particle = xsContentType.asParticle();
				    childParticle = null;
				    findParticle(particle, item);
					children = findChildren(particle, elements, true, complexType);						
				}
			}
		}
		return children;
	}
	
	public XSParticle getParticle(XSParticle particle, String item) {
		childParticle = null;
		findParticle(particle, item);
		return childParticle;
	}
	
	/**
	 * Find XSParticle, if Name = Item, if not: recursion
	 */
	void findParticle(XSParticle xsParticle, String item) {		
		 if(xsParticle != null) {
			 XSTerm pterm = xsParticle.getTerm();
			 
			 if(pterm.isElementDecl()) { 
				 if (pterm.asElementDecl().getName().equals(item)) {
			         XSComplexType xsComplexType =  (pterm.asElementDecl()).getType().asComplexType();
			         if (xsComplexType != null && !(pterm.asElementDecl().getType()).toString().contains("Enumeration")) {
			        	 childParticle = xsParticle; 
			         } else {
			        	 childParticle = xsParticle;			        	 
			         }
				 }
		         
		         XSComplexType xsComplexType =  (pterm.asElementDecl()).getType().asComplexType();
		             if (xsComplexType != null && !(pterm.asElementDecl().getType()).toString().contains("Enumeration"))
		             {
		                XSContentType xsContentType = xsComplexType.getContentType();		              
		                XSParticle xsParticleInside = xsContentType.asParticle();
		                	findParticle(xsParticleInside, item);		                			                	
		             }
		      
		        } else if(pterm.isModelGroup()){
		             XSModelGroup xsModelGroup2 = pterm.asModelGroup();
		             XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
		            	 for(XSParticle xsParticleTemp : xsParticleArray){ 
			            	  findParticle(xsParticleTemp, item);		            		  		            		  
			            }
		        }
		    }
	}
	
	private void findParticleForDetail(XSParticle xsParticle, String item) {		
		 if(xsParticle != null) {
			 XSTerm pterm = xsParticle.getTerm();
			 
			 if(pterm.isElementDecl()) { 
				 if (pterm.asElementDecl().getName().equals(item)) {
			         XSComplexType xsComplexType =  (pterm.asElementDecl()).getType().asComplexType();
			         if (xsComplexType != null && !(pterm.asElementDecl().getType()).toString().contains("Enumeration")) {
			        	 XSContentType xsContentType = xsComplexType.getContentType();		              
			        	 childParticle = xsContentType.asParticle();
			         } else {
			        	 childParticle = xsParticle;			        	 
			         }
				 }
		         
		         XSComplexType xsComplexType =  (pterm.asElementDecl()).getType().asComplexType();
		             if (xsComplexType != null && !(pterm.asElementDecl().getType()).toString().contains("Enumeration"))
		             {
		                XSContentType xsContentType = xsComplexType.getContentType();		              
		                XSParticle xsParticleInside = xsContentType.asParticle();
		                	findParticleForDetail(xsParticleInside, item);		                			                	
		             }
		      
		        } else if(pterm.isModelGroup()){
		             XSModelGroup xsModelGroup2 = pterm.asModelGroup();
		             XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
		            	 for(XSParticle xsParticleTemp : xsParticleArray){ 
			            	  findParticleForDetail(xsParticleTemp, item);		            		  		            		  
			            }
		        }
		    }
	}
 
	private List<String> findChildren(XSParticle xsParticle, List<String> elements, boolean firstLevel, boolean findComplexType) {
		 if(xsParticle != null){
			 XSTerm pterm = xsParticle.getTerm();
			 
			 if(pterm.isElementDecl()) {
				 if (pterm.asElementDecl().getType().isComplexType() && findComplexType == true) {
					 System.out.println("ComplexChild: " + pterm.asElementDecl().getName());
					 elements.add(pterm.asElementDecl().getName());					 
				 } else if (!pterm.asElementDecl().getType().isComplexType() && pterm.asElementDecl().getName() != null && findComplexType == false) {
					 System.out.println("ElementChild: " + pterm.asElementDecl().getName());
					 elements.add(pterm.asElementDecl().getName());				
				 }
		         
		         XSComplexType xsComplexType =  (pterm.asElementDecl()).getType().asComplexType();
		             if (xsComplexType != null && !(pterm.asElementDecl().getType()).toString().contains("Enumeration") && findComplexType == true)
		             {
		                XSContentType xsContentType = xsComplexType.getContentType();		              
		                XSParticle xsParticleInside = xsContentType.asParticle();
		                if (firstLevel == true) {
		                	findChildren(xsParticleInside, elements, false, findComplexType);		                	
		                }
		             }
		      
		        } else if(pterm.isModelGroup()){
		             XSModelGroup xsModelGroup2 = pterm.asModelGroup();
		             XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
		              for(XSParticle xsParticleTemp : xsParticleArray ){
		            		  findChildren(xsParticleTemp, elements, false, findComplexType);		            		  
		            }
		        }
		    }
		 	return elements;
	}
	
	/**
	 * For Versionsmanagement. 
	 * Check if the item is allowed in schema or not.
	 * Depending on min or maxOccurs. 
	 * @param item 
	 * @param schemaPath 
	 * @param firstLevel 
	 * @throws IOException @throws SAXException 
	 */
	public boolean elementIsAllowedInSchema(String item, int countItem, String schemaPath, boolean firstLevel) throws SAXException, IOException {
		boolean isAllowed = false;
		int neededNumber = getNeededNumber(item, schemaPath, firstLevel);
		if (neededNumber == -1) {
			isAllowed = true;
		} else if (countItem <= neededNumber) {
			isAllowed = true;
		}
		return isAllowed;
	}
	
	private int getNeededNumber(String item, String schemaPath, boolean firstLevel) throws SAXException, IOException {
		int number = 0;
		ParserForDetails parserForDetails = new ParserForDetails();
		Map<Object, String> details = parserForDetails.getMapForItem(item, firstLevel, schemaPath);
		String minOccurs = details.get("minOccurs");
		String maxOccurs = details.get("maxOccurs");
		
		if (minOccurs == null && maxOccurs == null) {
			number = 1;
		} else if (minOccurs != null && maxOccurs == null) {
			number = Integer.parseInt(minOccurs);
		} else if (maxOccurs != null) {
			number = Integer.parseInt(maxOccurs);
		}
		return number;
	}
	
	public boolean elementIsInSchema(Element element, String schemaPath, boolean firstLevel) throws SAXException, IOException {
		boolean elementIsInSchema = false;
		String nameElement = element.getName();
		XSSchemaSet xsSchemaSet = parseXMLSchemaWithXSOM(schemaPath);
		Iterator <XSElementDecl> itrElements = xsSchemaSet.iterateElementDecls();
		while(itrElements.hasNext()) {
			XSElementDecl xsElementDecl = (XSElementDecl) itrElements.next();
			if (xsElementDecl.getType().getName() != null) {				
				if (xsElementDecl.getType().getName().equals(nameElement) && firstLevel == true) {
					elementIsInSchema = true;
				} else if (firstLevel == false) {
					XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
					if (xsComplexType != null) {
						XSContentType xsContentType = xsComplexType.getContentType();
					    XSParticle particle = xsContentType.asParticle();
					    childParticle = null;
					    findParticle(particle, nameElement);
					    if (childParticle != null) {
							XSTerm pterm = childParticle.getTerm();
							if (pterm.isElementDecl()) {
								 elementIsInSchema = true;
							} else if (pterm.isModelGroup()) {
								 XSModelGroup xsModelGroup2 = pterm.asModelGroup();
								 XSParticle[] xsParticleArray = xsModelGroup2.getChildren();
				            	 for(XSParticle xsParticleTemp : xsParticleArray){ 
					            	 XSTerm pterm2 = xsParticleTemp.getTerm();
									 if (pterm2.isElementDecl()) {
										elementIsInSchema = true;
									 }
								 }
				            	
							}
						}
					}
				}
			} else if (firstLevel == true) {
				XSComplexType xsComplexType = xsElementDecl.getType().asComplexType();
				if (xsComplexType != null) {
					XSContentType xsContentType = xsComplexType.getContentType();
				    XSParticle particle = xsContentType.asParticle();
				    childParticle = null;
				    findParticle(particle, element.getName());
				    if (childParticle != null) {
						XSTerm pterm = childParticle.getTerm();
						if(pterm.isElementDecl()) {
							 if (pterm.asElementDecl().getType().equals(nameElement)) {
								 elementIsInSchema = true;
							 }
						}
					}				
				}
			}
		}
		return elementIsInSchema;
	}
}