/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import mcfdtool.FACT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FACTTest extends AbstractTest {

  protected PrintStream originalOut;

  protected ByteArrayOutputStream out;

  @Before
  public void redirectSysOut() {
    Log.initWARN();
    Log.enableFailQuick(false);
    originalOut = System.out;
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
  }

  @After
  public void restoreSysOut() {
    System.setOut(originalOut);
  }

  @Test
  public void testAnalysisIsValidTrue() {
    new FACT(new String[] {
        "src/test/resources/FalseOptional.fd",
        "-isValid", "src/test/resources/ValidConfig.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("true"));
  }

  @Test
  public void testAnalysisIsValidFalse() {
    new FACT(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-isValid", "src/test/resources/InvalidConfig.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("false"));
  }

  @Test
  public void testAnalysisAllProducts() throws IOException {
    new FACT(new String[] {
      "src/test/resources/DeadFeatures.fd",
      "-allProducts"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    String[] products = printed.split("featureconfig");
    FeatureConfigurationPartialParser parsers =
      new FeatureConfigurationPartialParser();
    Optional<ASTFCCompilationUnit> conf = parsers.parse_String("featureconfig" +products[products.length -1]);
    assertTrue(conf.isPresent());
  }

  @Test
  public void testAnalysisDeadFeatures(){
    new FACT(new String[] {
      "src/test/resources/DeadFeatures.fd",
      "-dead"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("B"));
  }

  @Test
  public void testAnalysisFalseOpt() throws IOException {
    new FACT(new String[] {
      "src/test/resources/FalseOptional.fd",
      "-falseOpt"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("B"));
  }

  @Test
  public void testAnalysisFilter() throws IOException {
    new FACT(new String[] {
      "src/test/resources/FalseOptional.fd",
      "-filter", "src/test/resources/CompleteToValid.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    String[] products = printed.split("featureconfig");
    FeatureConfigurationPartialParser parsers =
      new FeatureConfigurationPartialParser();
    Optional<ASTFCCompilationUnit> conf = parsers.parse_String("featureconfig" +products[products.length -1]);
    assertTrue(conf.isPresent());
  }

  @Test
  public void testAnalysisFindValid() throws IOException {
    new FACT(new String[] {
      "src/test/resources/FalseOptional.fd",
      "-findValid"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    String[] products = printed.split("featureconfig");
    FeatureConfigurationPartialParser parsers =
      new FeatureConfigurationPartialParser();
    Optional<ASTFCCompilationUnit> conf = parsers.parse_String("featureconfig" +products[products.length -1]);
    assertTrue(conf.isPresent());
  }

  @Test
  public void testIsVoidFalse() {
    new FACT(new String[] {
      "src/test/resources/DeadFeatures.fd",
      "-isVoid"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("false"));
  }

  @Test
  public void testIsVoidTrue() {
    new FACT(new String[] {
      "src/test/resources/Void.fd",
      "-isVoid"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("true"));
  }

  @Test
  public void testAnalysisNumProducts() {
    new FACT(new String[] {
      "src/test/resources/DeadFeatures.fd",
      "-numProducts"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("2"));
  }


  @Test
  public void testAnalysisInvalidFC() {
    PrintStream originalErr = System.err;
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    System.setErr(new PrintStream(err));
    new FACT(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-isValid", "src/test/resources/InvalidConfig2.fc"
    });
    String printed = err.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.contains("0xFC002"));
    System.setErr(originalErr);
  }

  @Test
  public void testInvalidArg() {
    PrintStream originalErr = System.err;
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    System.setErr(new PrintStream(err));
    new FACT(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-SomethingIsNotRightHere", "src/test/resources/InvalidConfig.fc"
    });
    String printed = err.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.contains("0xFC901"));
    System.setErr(originalErr);
  }

  @Test
  public void testHelp() {
    new FACT(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FACT.jar <test.fd> [analysis options]"));
  }
}
