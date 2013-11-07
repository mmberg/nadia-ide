package net.msoetebier.nadia.test.parser;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Test;
import com.sun.xml.xsom.XSSchemaSet;
import net.msoetebier.nadia.parser.Parser;
import net.msoetebier.nadia.test.ressources.AbstractConverter;

public class TestParser extends AbstractConverter {
	Parser parser = new Parser();
	
	@Test
	public void testFindSchemaFile() {
		File file = new File(getOtherPath1());
		assertNotNull(file);
	}
	
	@Test
	public void testFindNADIAEasyFile() {
		File oldNadiaFile = new File(getOldNadiaPath());
		assertNotNull(oldNadiaFile);
	}
	
	@Test
	public void testFindNADIASchemaFile() {
		File nadiaFile = new File(getNadiaPath());
		assertNotNull(nadiaFile);
	}
	
	@Test
	public void testCorrectSchemaStructure() throws Exception{
		XSSchemaSet schemaSet = parser.parseXMLSchemaWithXSOM(getOtherPath1());
		assertNotNull(schemaSet);
		XSSchemaSet oldNadiaSchemaSet = parser.parseXMLSchemaWithXSOM(getOldNadiaPath());
		assertNotNull(oldNadiaSchemaSet);
		XSSchemaSet nadiaSchemaSet = parser.parseXMLSchemaWithXSOM(getNadiaPath());
		assertNotNull(nadiaSchemaSet);
	}
}
