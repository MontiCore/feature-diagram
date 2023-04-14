/* (c) https://github.com/MontiCore/monticore */
package test.fcp;

import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import org.junit.Before;
import org.junit.Test;
import test.AbstractLangTest;

import java.io.IOException;

/**
 * This class tests the parser of feature diagram and feature configuration languages
 */
public class FeatureConfigurationPartialParserTest extends AbstractLangTest {

  @Before
  public void initMill(){
    FeatureConfigurationPartialMill.init();
  }

  @Test
  public void testParseStringsForIndividualGrammarRules() throws IOException {
    FeatureConfigurationPartialParser parser = new FeatureConfigurationPartialParser();
    assertPresent(parser.parse_StringFCElement("A , B, C;"));
    assertPresent(parser.parse_StringFCElement("A ;"));

    assertPresent(parser.parse_StringSelect("select {A , B, C}"));
    assertPresent(parser.parse_StringSelect("select {A }"));
    assertPresent(parser.parse_StringUnselect("exclude {A , B, C}"));
    assertPresent(parser.parse_StringUnselect("exclude {A }"));

    assertPresent(parser.parse_StringFeatureConfiguration("featureconfig A for FD { }"));
    assertPresent(parser.parse_StringFeatureConfiguration("featureconfig A for FD { select {A} }"));
    assertPresent(parser.parse_StringFeatureConfiguration("featureconfig A for FD { exclude {A} }"));
    assertPresent(parser.parse_StringFeatureConfiguration("featureconfig A for FD { select{B,A } select {A} }"));

    assertPresent(parser.parse_StringFCCompilationUnit("featureconfig A for FD { select {A, B} }"));
    assertPresent(parser.parse_StringFCCompilationUnit("package a.b.c; featureconfig A for FD { select {A, B, C} }"));
    assertPresent(parser.parse_StringFCCompilationUnit("package a; featureconfig A for FD { select {A, B} }"));
    assertPresent(parser.parse_StringFCCompilationUnit("package a; import d.e.f; featureconfig A for FD { select {A, B} }"));
    assertPresent(parser.parse_StringFCCompilationUnit("import d; featureconfig A for FD { select {A, B} }"));

  }

  @Test
  public void testValidPartialFCs() throws IOException {
    FeatureConfigurationPartialParser parser = new FeatureConfigurationPartialParser();
    assertPresent(parser.parse("src/test/resources/pfcvalid/BasicCarNavigation.fc"));
    assertPresent(parser.parse("src/test/resources/pfcvalid/SelectImported.fc"));
    assertPresent(parser.parse("src/test/resources/pfcvalid/SelectNone.fc"));
    assertPresent(parser.parse("src/test/resources/pfcvalid/SelectOne.fc"));
    assertPresent(parser.parse("src/test/resources/pfcvalid/SelectSome.fc"));
    assertPresent(parser.parse("src/test/resources/pfcvalid/StarImport.fc"));
  }

}
