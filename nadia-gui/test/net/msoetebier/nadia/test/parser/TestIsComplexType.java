package net.msoetebier.nadia.test.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.test.ressources.AbstractConverter;

public class TestIsComplexType extends AbstractConverter {
	Parser parser = new Parser();

	@Test
	public void testIsComplexTypeInOtherFile() throws Exception {
		boolean isComplexType = parser.checkIfTypIsComplexForElement("computer2", true, getOtherPath1());
		boolean complexType = true;
		assertEquals(complexType, isComplexType);
	}
	
	@Test
	public void testIsGroovyActionComplexType() throws Exception {
		boolean isGroovyActionComplexType = parser.checkIfTypIsComplexForElement("groovyAction", true, getNadiaPath());
		boolean groovyActionComplexType = true;
		assertEquals(groovyActionComplexType, isGroovyActionComplexType);
	}
	
	@Test
	public void testIsDialogComplexType() throws Exception {
		boolean isDialogComplexType = parser.checkIfTypIsComplexForElement("dialog", true, getNadiaPath());
		boolean dialogComplexType = true;
		assertEquals(dialogComplexType, isDialogComplexType);
	}
	
	@Test
	public void testIsTasksComplexType() throws Exception {
		boolean isTasksComplexType = parser.checkIfTypIsComplexForElement("tasks", false, getNadiaPath());
		boolean tasksComplexType = true;
		assertEquals(tasksComplexType, isTasksComplexType);
	}
	
	@Test
	public void testIsTaskComplexType() throws Exception {
		boolean isTaskComplexType = parser.checkIfTypIsComplexForElement("task", false, getNadiaPath());
		boolean taskComplexType = true;
		assertEquals(taskComplexType, isTaskComplexType);
	}
	
	@Test
	public void testIsItosComplexType() throws Exception {
		boolean isItosComplexType = parser.checkIfTypIsComplexForElement("itos", false, getNadiaPath());
		boolean itosComplexType = true;
		assertEquals(itosComplexType, isItosComplexType);
	}
	
	@Test
	public void testIsItoComplexType() throws Exception {
		boolean isItoComplexType = parser.checkIfTypIsComplexForElement("ito", false, getNadiaPath());
		boolean itoComplexType = true;
		assertEquals(itoComplexType, isItoComplexType);
	}
	
	@Test
	public void testIsSelectorComplexType() throws Exception {
		boolean isSelectorComplexType = parser.checkIfTypIsComplexForElement("selector", false, getNadiaPath());
		boolean selectorComplexType = true;
		assertEquals(selectorComplexType, isSelectorComplexType);
	}
	
	@Test
	public void testIsActionComplexType() throws Exception {
		boolean isActionComplexType = parser.checkIfTypIsComplexForElement("action", false, getNadiaPath());
		boolean actionComplexType = true;
		assertEquals(actionComplexType, isActionComplexType);
	}
	
	@Test
	public void testIsFollowupComplexType() throws Exception {
		boolean isFollowupComplexType = parser.checkIfTypIsComplexForElement("followup", false, getNadiaPath());
		boolean followupComplexType = true;
		assertEquals(followupComplexType, isFollowupComplexType);
	}
	
	@Test
	public void testIsBagOfWordsTaskSelectorComplexType() throws Exception {
		boolean isBagOfWordsTaskSelectorComplexType = parser.checkIfTypIsComplexForElement("bagOfWordsTaskSelector", false, getNadiaPath());
		boolean bagOfWordsTaskSelectorComplexType = true;
		assertEquals(bagOfWordsTaskSelectorComplexType, isBagOfWordsTaskSelectorComplexType);
	}
	
	@Test
	public void testIsAQDComplexType() throws Exception {
		boolean isAQDComplexType = parser.checkIfTypIsComplexForElement("AQD", false, getNadiaPath());
		boolean aqdComplexType = true;
		assertEquals(aqdComplexType, isAQDComplexType);
	}
	
	@Test
	public void testIsContextComplexType() throws Exception {
		boolean isContentComplexType = parser.checkIfTypIsComplexForElement("context", false, getNadiaPath());
		boolean contentComplexType = true;
		assertEquals(contentComplexType, isContentComplexType);
	}
	
	@Test
	public void testIsFormComplexType() throws Exception {
		boolean isFormComplexType = parser.checkIfTypIsComplexForElement("form", false, getNadiaPath());
		boolean formComplexType = true;
		assertEquals(formComplexType, isFormComplexType);
	}
	
	@Test
	public void testIsTypeComplexType() throws Exception {
		boolean isTypeComplexType = parser.checkIfTypIsComplexForElement("type", false, getNadiaPath());
		boolean typeComplexType = true;
		assertEquals(typeComplexType, isTypeComplexType);
	}
	
	@Test
	public void testIsAnswerMappingComplexType() throws Exception {
		boolean isAnswerMappingComplexType = parser.checkIfTypIsComplexForElement("answerMapping", false, getNadiaPath());
		boolean answerMapping = true;
		assertEquals(answerMapping, isAnswerMappingComplexType);
	}
	
	@Test
	public void testIsItemComplexType() throws Exception {
		boolean isItemComplexType = parser.checkIfTypIsComplexForElement("item", false, getNadiaPath());
		boolean itemComplexType = true;
		assertEquals(itemComplexType, isItemComplexType);
	}
}