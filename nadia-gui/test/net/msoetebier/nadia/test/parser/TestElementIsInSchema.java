package net.msoetebier.nadia.test.parser;

import static org.junit.Assert.*;

import org.jdom2.Element;
import org.junit.Test;

import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.test.ressources.AbstractConverter;

public class TestElementIsInSchema extends AbstractConverter{
	Parser parser = new Parser();

	@Test
	public void testAreElementsInOtherFile() throws Exception {
		Element computerElement = new Element("computer");
		boolean isComputerIsInSchema = parser.elementIsInSchema(computerElement , getOtherPath1(), true);
		boolean computerIsInSchema = true;
		assertEquals(computerIsInSchema, isComputerIsInSchema);
		
		Element keyboardElement = new Element("keyboard");
		boolean isKeyboardIsInSchema = parser.elementIsInSchema(keyboardElement, getOtherPath1(), false);
		boolean keyboardIsInSchema = true;
		assertEquals(keyboardIsInSchema, isKeyboardIsInSchema);
		
		Element element = new Element("test");
		boolean isElementIsInSchema = parser.elementIsInSchema(element, getOtherPath1(), false);
		boolean elementIsInSchema = false;
		assertEquals(elementIsInSchema, isElementIsInSchema);
	}
	
	@Test
	public void testIsDialogElementInNadia() throws Exception {
		Element dialogElement = new Element("dialog");
		boolean isDialogElementIsInSchema = parser.elementIsInSchema(dialogElement, getNadiaPath(), true);
		boolean dialogElementIsInSchema = true;
		assertEquals(dialogElementIsInSchema, isDialogElementIsInSchema);
	}
	
	@Test
	public void testIsTasksElementInNadia() throws Exception {
		Element tasksElement = new Element("tasks");
		boolean isTasksElementIsInSchema = parser.elementIsInSchema(tasksElement, getNadiaPath(), false);
		boolean tasksElementIsInSchema = true;
		assertEquals(tasksElementIsInSchema, isTasksElementIsInSchema);
	}
	
	@Test
	public void testIsTaskElementInNadia() throws Exception {
		Element taskElement = new Element("task");
		boolean isTaskElementIsInSchema = parser.elementIsInSchema(taskElement, getNadiaPath(), false);
		boolean taskElementIsInSchema = true;
		assertEquals(taskElementIsInSchema, isTaskElementIsInSchema);
	}
	
	@Test
	public void testIsItosElementInNadia() throws Exception {
		Element itosElement = new Element("itos");
		boolean isItosElementIsInSchema = parser.elementIsInSchema(itosElement, getNadiaPath(), false);
		boolean itosElementIsInSchema = true;
		assertEquals(itosElementIsInSchema, isItosElementIsInSchema);
	}
	
	@Test
	public void testIsItoElementInNadia() throws Exception {
		Element itoElement = new Element("ito");
		boolean isItoElementIsInSchema = parser.elementIsInSchema(itoElement, getNadiaPath(), false);
		boolean itoElementIsInSchema = true;
		assertEquals(itoElementIsInSchema, isItoElementIsInSchema);
	}
	
	@Test
	public void testIsTestInNadia() throws Exception {
		Element testElement = new Element("test");
		boolean isTestElementIsInSchema = parser.elementIsInSchema(testElement, getNadiaPath(), true);
		boolean testElementIsInSchema = false;
		assertEquals(testElementIsInSchema, isTestElementIsInSchema);
		boolean isTestElement2IsInSchema = parser.elementIsInSchema(testElement, getNadiaPath(), false);
		assertEquals(testElementIsInSchema, isTestElement2IsInSchema);
	}
}