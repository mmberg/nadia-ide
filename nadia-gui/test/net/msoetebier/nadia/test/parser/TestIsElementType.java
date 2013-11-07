package net.msoetebier.nadia.test.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.test.ressources.AbstractConverter;

public class TestIsElementType extends AbstractConverter{
	Parser parser = new Parser();
	
	@Test
	public void testIsStartTaskNameElementType() throws Exception {
		boolean isStartTaskNameElementType = parser.checkIfTypIsComplexForElement("start_task_name", true, getNadiaPath());
		boolean startTaskNameElementType = false;
		assertEquals(startTaskNameElementType, isStartTaskNameElementType);
	}
	
	@Test
	public void testIsGlobalLanguageElementType() throws Exception {
		boolean isGlobalLanguageElementType = parser.checkIfTypIsComplexForElement("global_language", true, getNadiaPath());
		boolean globalLanguageElementType = false;
		assertEquals(globalLanguageElementType, isGlobalLanguageElementType);
	}
	
	@Test
	public void testIsGlobalPolitenessElementType() throws Exception {
		boolean isGlobalPolitenessElementType = parser.checkIfTypIsComplexForElement("global_politeness", true, getNadiaPath());
		boolean globalPolitenessElementType = false;
		assertEquals(globalPolitenessElementType, isGlobalPolitenessElementType);
	}
	
	@Test
	public void testIsGlobalFormalityElementType() throws Exception {
		boolean isGlobalFormalityElementType = parser.checkIfTypIsComplexForElement("global_formality", true, getNadiaPath());
		boolean globalFormalityElementType = false;
		assertEquals(globalFormalityElementType, isGlobalFormalityElementType);
	}
	
	@Test
	public void testIsUseSODAElementType() throws Exception {
		boolean isUseSODAElementType = parser.checkIfTypIsComplexForElement("useSODA", true, getNadiaPath());
		boolean useSODAElementType = false;
		assertEquals(useSODAElementType, isUseSODAElementType);
	}
	
	@Test
	public void testIsAllowSwitchTasksElementType() throws Exception {
		boolean isAllowSwitchTasksElementType = parser.checkIfTypIsComplexForElement("allowSwitchTasks", true, getNadiaPath());
		boolean allowSwitchTasksElementType = false;
		assertEquals(allowSwitchTasksElementType, isAllowSwitchTasksElementType);
	}
	
	@Test
	public void testIsAllowOverAnsweringElementType() throws Exception {
		boolean isAllowOverAnsweringElementType = parser.checkIfTypIsComplexForElement("allowOverAnswering", true, getNadiaPath());
		boolean allowOverAnsweringElementType = false;
		assertEquals(allowOverAnsweringElementType, isAllowOverAnsweringElementType);
	}
	
	@Test
	public void testIsReturnAnswerElementType() throws Exception {
		boolean isReturnAnswerElementType = parser.checkIfTypIsComplexForElement("returnAnswer", false, getNadiaPath());
		boolean returnAnswerElementType = false;
		assertEquals(returnAnswerElementType, isReturnAnswerElementType);
	}
	
	@Test
	public void testIsUtteranceTemplateElementType() throws Exception {
		 boolean isUtteranceTemplateElementType = parser.checkIfTypIsComplexForElement("utteranceTemplate", false, getNadiaPath());
		 boolean utteranceTemplate = false;
		 assertEquals(utteranceTemplate, isUtteranceTemplateElementType);
	}
	
	@Test
	public void testIsDomainElementType() throws Exception {
		boolean isDomainElementType = parser.checkIfTypIsComplexForElement("domain", false, getNadiaPath());
		boolean domainElementType = false;
		assertEquals(domainElementType, isDomainElementType);
	}
	
	@Test
	public void testIsFallbackQuestionElementType() throws Exception {
		boolean isFallbackQuestionElementType = parser.checkIfTypIsComplexForElement("fallback_question", false, getNadiaPath());
		boolean fallbackQuestionElementType = false;
		assertEquals(fallbackQuestionElementType, isFallbackQuestionElementType);
	}
	
	@Test
	public void testIsReferenceElementType() throws Exception {
		boolean isReferenceElementType = parser.checkIfTypIsComplexForElement("reference", false, getNadiaPath());
		boolean referenceElementType = false;
		assertEquals(referenceElementType, isReferenceElementType);
	}
	
	@Test
	public void testIsFormalityElementType() throws Exception {
		 boolean isFormalityElementType = parser.checkIfTypIsComplexForElement("formality", false, getNadiaPath());
		 boolean formalityElementType = false;
		 assertEquals(formalityElementType, isFormalityElementType);
	}
	
	@Test
	public void testIsAnswerTypeElementType() throws Exception {
		boolean isAnswerTypeElementType = parser.checkIfTypIsComplexForElement("answerType", false, getNadiaPath());
		boolean answerTypeElementType = false;
		assertEquals(answerTypeElementType, isAnswerTypeElementType);
	}
}