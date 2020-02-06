package fd;
/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

import java.io.IOException;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.LogStub;
import featurediagram._parser.FeatureDiagramParser;

/**
 * This class tests the parser of feature diagram and feature configuration languages
 */
public class FeatureDiagramParserTest {
  
  @BeforeClass
  public static void initLog() {
    LogStub.init();
//    Log.enableFailQuick(false);
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
    
    assertPresent(parser.parse_StringCardinalizedGroup("[2 .. 3] (A)"));
    assertPresent(parser.parse_StringCardinalizedGroup("[2 .. 3] (A,B)"));
    assertPresent(parser.parse_StringCardinalizedGroup("[2 .. 3] (A,B,C)"));
    assertPresent(parser.parse_StringCardinalizedGroup("[0 .. *] (A,B,C)"));
    assertPresent(parser.parse_StringCardinalizedGroup("[2] (A,B)"));
    assertEmpty(parser.parse_StringCardinalizedGroup("[-2 .. 3] (A,B)"));
    assertEmpty(parser.parse_StringCardinalizedGroup("[2] ()"));
    
    assertPresent(parser.parse_StringFeature("A"));
    assertPresent(parser.parse_StringFeature("A?"));
    assertPresent(parser.parse_StringFeature("A123456789a_z"));
//    assertPresent(parser.parse_StringFeature("<<final>>A"));
//    assertEmpty(parser.parse_StringFeature("<<>>A"));

    assertPresent(parser.parse_StringConstraintExpression("A requires B"));
    assertPresent(parser.parse_StringConstraintExpression("A excludes B"));
  }
  
  @Test
  public void testBasicElements() throws IOException {
    FeatureDiagramParser parser = new FeatureDiagramParser();
    assertPresent(parser.parse("src/test/resources/fdvalid/BasicElements.fd"));
  }

  protected static void assertPresent(Optional<?> opt) {
    Assert.assertTrue(opt.isPresent());
  }

  protected static void assertEmpty(Optional<?> opt) {
    Assert.assertTrue(!opt.isPresent());
  }
  
}
