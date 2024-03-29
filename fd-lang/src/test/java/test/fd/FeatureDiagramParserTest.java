/* (c) https://github.com/MontiCore/monticore */
package test.fd;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import org.junit.Test;
import org.junit.Before;
import test.AbstractLangTest;

import java.io.IOException;

/**
 * This class tests the parser of feature diagram and feature configuration languages
 */
public class FeatureDiagramParserTest extends AbstractLangTest {

  @Before
  public void initMill(){
    FeatureDiagramMill.init();
  }

  @Test
  public void testParseStringsForIndividualGrammarRules() throws IOException {
    FeatureDiagramParser parser = new FeatureDiagramParser();
    assertPresent(parser.parse_StringXorGroup("A ^ B"));
    assertPresent(parser.parse_StringXorGroup("A ^ B ^ C"));
    assertEmpty(parser.parse_StringXorGroup("A"));
    assertEmpty(parser.parse_StringXorGroup("^A"));
    assertEmpty(parser.parse_StringXorGroup("^"));

    assertPresent(parser.parse_StringAndGroup("A & B"));
    assertPresent(parser.parse_StringAndGroup("A & B & C"));
    assertPresent(parser.parse_StringAndGroup("A"));
    assertEmpty(parser.parse_StringAndGroup("&A"));
    assertEmpty(parser.parse_StringAndGroup("&"));

    assertPresent(parser.parse_StringOrGroup("A | B"));
    assertPresent(parser.parse_StringOrGroup("A | B | C"));
    assertEmpty(parser.parse_StringOrGroup("A"));
    assertEmpty(parser.parse_StringOrGroup("|A"));
    assertEmpty(parser.parse_StringOrGroup("|"));

    assertPresent(parser.parse_StringCardinalizedGroup("[2 .. 3] of {A}"));
    assertPresent(parser.parse_StringCardinalizedGroup("[2 .. 3] of {A,B}"));
    assertPresent(parser.parse_StringCardinalizedGroup("[2 .. 3] of {A,B,C}"));
    assertPresent(parser.parse_StringCardinalizedGroup("[0 .. *] of {A,B,C}"));
    assertPresent(parser.parse_StringCardinalizedGroup("[2] of {A,B}"));
    assertEmpty(parser.parse_StringCardinalizedGroup("[-2 .. 3] of {A,B}"));
    assertEmpty(parser.parse_StringCardinalizedGroup("[2] ()"));
    assertPresent(parser.parse_StringFeatureConstraint("A requires B;"));
    assertPresent(parser.parse_StringFeatureConstraint("A excludes B;"));
    assertPresent(parser.parse_StringFeatureConstraint("A == B;"));
    assertPresent(parser.parse_StringFeatureConstraint("A != B;"));
    assertPresent(parser.parse_StringFeatureConstraint("!A && B || C;"));
  }

  @Test
  public void testValidFDs() throws IOException {
    FeatureDiagramParser parser = new FeatureDiagramParser();
    assertPresent(parser.parse("src/test/resources/fdvalid/BasicElements.fd"));
    assertPresent(parser.parse("src/test/resources/fdvalid/Car.fd"));
    assertPresent(parser.parse("src/test/resources/fdvalid/GraphLibrary.fd"));
    assertPresent(parser.parse("src/test/resources/fdvalid/Phone.fd"));
    assertPresent(parser.parse("src/test/resources/fdvalid/PhoneComplex.fd"));
    assertPresent(parser.parse("src/test/resources/fdvalid/CarNavigation.fd"));
  }

}
