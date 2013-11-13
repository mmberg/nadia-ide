package net.msoetebier.nadia.language;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class TestLanguageManagement {
	LanguageManagement languageManagement = new LanguageManagement();
	
	@Test
	public void testSetEnglishLanguage() {
		Map<String, Map<String, String>> englishMap = languageManagement.setMap("english");
		Map<String, String> fileDialogMap = englishMap.get("fileDialog");
		assertEquals("Schema file path:", fileDialogMap.get("schemaPath"));
		assertEquals("XML file path:", fileDialogMap.get("xmlPath"));
		assertEquals("Restriction file path:", fileDialogMap.get("restrictionPath"));
	}
	
	@Test
	public void testSetGermanLanguage() {
		Map<String, Map<String, String>> germanMap = languageManagement.setMap("german");
		Map<String, String> fileDialogMap = germanMap.get("fileDialog");
		assertEquals("Dateipfad f�r das Schema:", fileDialogMap.get("schemaPath"));
		assertEquals("Dateipfad f�r die XML-Datei:", fileDialogMap.get("xmlPath"));
		assertEquals("Dateipfad f�r die Beschr�nkungsdatei:", fileDialogMap.get("restrictionPath"));
	}
	
	@Test 
	public void testEnglishVersionDialog() {
		Map<String, Map<String, String>> englishMap = languageManagement.setMap("english");
		Map<String, String> versionsDialogMap = englishMap.get("versionsDialog");
		assertEquals("Version out of date or structure incompatible", versionsDialogMap.get("title"));
	}
	
	@Test 
	public void testGermanVersionDialog() {
		Map<String, Map<String, String>> germanMap = languageManagement.setMap("german");
		Map<String, String> versionsDialogMap = germanMap.get("versionsDialog");
		assertEquals("Version veraltet oder Struktur inkompatibel", versionsDialogMap.get("title"));
	}
	
	@Test 
	public void testEnglishAboutActionDialog() {
		Map<String, Map<String, String>> englishMap = languageManagement.setMap("english");
		Map<String, String> aboutActionMap = englishMap.get("aboutAction");
		assertEquals("About NADIA", aboutActionMap.get("title"));
	}
	
	@Test 
	public void testGermanAboutActionDialog() {
		Map<String, Map<String, String>> germanMap = languageManagement.setMap("german");
		Map<String, String> aboutActionMap = germanMap.get("aboutAction");
		assertEquals("�ber NADIA", aboutActionMap.get("title"));
	}
	
	@Test 
	public void testEnglishSaveFileActionDialog() {
		Map<String, Map<String, String>> englishMap = languageManagement.setMap("english");
		Map<String, String> saveFileActionMap = englishMap.get("saveFileAction");
		assertEquals("How to save this XML file", saveFileActionMap.get("title"));
	}
	
	@Test 
	public void testGermanSaveFileActionDialog() {
		Map<String, Map<String, String>> germanMap = languageManagement.setMap("german");
		Map<String, String> saveFileActionMap = germanMap.get("saveFileAction");
		assertEquals("Wie soll diese XML-Datei gespeichert werden?", saveFileActionMap.get("title"));
	}
}
