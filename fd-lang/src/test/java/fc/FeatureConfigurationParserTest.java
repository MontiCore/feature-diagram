/* (c) https://github.com/MontiCore/monticore */
package fc;

import de.monticore.featureconfiguration._parser.FeatureConfigurationParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractTest;

import java.io.IOException;
import java.util.Optional;

/**
 * This class tests the parser of feature diagram and feature configuration languages
 */
public class FeatureConfigurationParserTest extends AbstractTest {

  @Test
  public void testParseStringsForIndividualGrammarRules() throws IOException {
    FeatureConfigurationParser parser = new FeatureConfigurationParser();
    assertPresent(parser.parse_StringFCElement("A , B, C;"));
    assertPresent(parser.parse_StringFCElement("A ;"));

    assertPresent(parser.parse_StringFeatures("A , B, C;"));
    assertPresent(parser.parse_StringFeatures("A ;"));

    assertPresent(parser.parse_StringFeatureConfiguration("featureconfig A for FD { }"));
    assertPresent(parser.parse_StringFeatureConfiguration("featureconfig A for FD { A; }"));
    assertPresent(parser.parse_StringFeatureConfiguration("featureconfig A for FD { A, B; }"));
    assertPresent(parser.parse_StringFeatureConfiguration("featureconfig A for FD { A; B; }"));

    assertPresent(parser.parse_StringFCCompilationUnit("featureconfig A for FD { A; B; }"));
    assertPresent(parser.parse_StringFCCompilationUnit("package a.b.c; featureconfig A for FD { A; B; }"));
    assertPresent(parser.parse_StringFCCompilationUnit("package a; featureconfig A for FD { A; B; }"));
    assertPresent(parser.parse_StringFCCompilationUnit("package a; import d.e.f; featureconfig A for FD { A; B; }"));
    assertPresent(parser.parse_StringFCCompilationUnit("import d; featureconfig A for FD { A; B; }"));

  }

  @Test
  public void testValidFCs() throws IOException {
    FeatureConfigurationParser parser = new FeatureConfigurationParser();
    assertPresent(parser.parse("src/test/resources/fcvalid/BasicCarNavigation.fc"));
    assertPresent(parser.parse("src/test/resources/fcvalid/PremiumCarNavigation.fc"));
    assertPresent(parser.parse("src/test/resources/fcvalid/SelectImported.fc"));
    assertPresent(parser.parse("src/test/resources/fcvalid/SelectNone.fc"));
    assertPresent(parser.parse("src/test/resources/fcvalid/SelectOne.fc"));
    assertPresent(parser.parse("src/test/resources/fcvalid/SelectSome.fc"));
    assertPresent(parser.parse("src/test/resources/fcvalid/StarImport.fc"));
  }

}
