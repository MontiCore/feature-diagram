/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._parser.FeatureConfigurationPartialParser;
import de.se_rwth.commons.logging.Log;
import mcfdtool.FACT;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.Assert.*;

public class FACTTest extends AbstractTest {

  protected FACT tool = new FACT();

  protected PrintStream originalOut;

  protected ByteArrayOutputStream out;

  @BeforeClass
  public static void initMills(){
    FeatureDiagramMill.init();
  }

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
    tool.run(new String[] {
        "src/test/resources/FalseOptional.fd",
        "-isValid", "src/test/resources/ValidConfig.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("true"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisIsValidFalse() {
    tool.run(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-isValid", "src/test/resources/InvalidConfig.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("false"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisAllProducts() throws IOException {
    tool.run(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-allProducts"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    String[] products = printed.split("featureconfig");
    FeatureConfigurationPartialParser parsers =
        new FeatureConfigurationPartialParser();
    Optional<ASTFCCompilationUnit> conf = parsers
        .parse_String("featureconfig " + products[products.length - 1]);
    assertTrue(conf.isPresent());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisDeadFeatures() {
    tool.run(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-deadFeatures"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("B"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisFalseOpt() {
    tool.run(new String[] {
        "src/test/resources/FalseOptional.fd",
        "-falseOptional"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("B"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisFilter() throws IOException {
    tool.run(new String[] {
        "src/test/resources/FalseOptional.fd",
        "-completeToValid", "src/test/resources/CompleteToValid.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    String[] products = printed.split("featureconfig");
    FeatureConfigurationPartialParser parsers =
        new FeatureConfigurationPartialParser();
    Optional<ASTFCCompilationUnit> conf = parsers
        .parse_String("featureconfig" + products[products.length - 1]);
    assertTrue(conf.isPresent());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisFindValid() throws IOException {
    tool.run(new String[] {
        "src/test/resources/FalseOptional.fd",
        "-findValid"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    String[] products = printed.split("featureconfig");
    FeatureConfigurationPartialParser parsers =
        new FeatureConfigurationPartialParser();
    Optional<ASTFCCompilationUnit> conf = parsers
        .parse_String("featureconfig" + products[products.length - 1]);
    assertTrue(conf.isPresent());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisFindValid2() throws IOException {
    tool.run(new String[] {
        "src/test/resources/fdvalid/CarNavigation.fd",
        "-findValid"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    String[] products = printed.split("featureconfig");
    FeatureConfigurationPartialParser parsers =
        new FeatureConfigurationPartialParser();
    Optional<ASTFCCompilationUnit> conf = parsers
        .parse_String("featureconfig" + products[products.length - 1]);
    assertTrue(conf.isPresent());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testIsVoidFalse() {
    tool.run(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-isVoidFeatureModel"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("false"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testIsVoidTrue() {
    tool.run(new String[] {
        "src/test/resources/Void.fd",
        "-isVoidFeatureModel"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("true"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisNumProducts() {
    tool.run(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-numberOfProducts"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("2"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testAnalysisInvalidFC() {
    PrintStream originalErr = System.err;
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    System.setErr(new PrintStream(err));
    tool.run(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-isValid", "src/test/resources/InvalidConfig2.fc"
    });
    String printed = err.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.contains("0xFD133"));
    System.setErr(originalErr);
    assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testInvalidArg() {
    PrintStream originalErr = System.err;
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    System.setErr(new PrintStream(err));
    tool.run(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-SomethingIsNotRightHere", "src/test/resources/InvalidConfig.fc"
    });
    String printed = err.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.contains("0xFC901"));
    System.setErr(originalErr);
    assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testSemDiff1Argument() {
    tool.run(new String[] {
      "src/test/resources/fddiff/car2.fd",
      "-semdiff"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xFC910 Number of specified input FDs is"));
  }

  @Test
  public void testSemDiff3Arguments() {
    tool.run(new String[] {
      "src/test/resources/fddiff/car2.fd",
      "src/test/resources/fddiff/car2.fd",
      "src/test/resources/fddiff/car2.fd",
      "-semdiff"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xFC999 Too many arguments. At most two FDs are expected."));
  }

  @Test
  public void testSemDiffUnknownValueForSemantics() {
    tool.run(new String[] {
      "src/test/resources/fddiff/car2.fd",
      "src/test/resources/fddiff/car1.fd",
      "-semdiff",
      "nonexistingsemantics"
    });
    String printed = out.toString().trim();
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xFC902 Unknown value "));
  }

  @Test
  public void testSemDiffOpenNoRefinementNoArg() {
    tool.run(new String[] {
      "src/test/resources/fddiff/car2.fd",
      "src/test/resources/fddiff/car1.fd",
      "-semdiff"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.contains("Diff witness: "));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSemDiffOpenRefinementArgOpen() {
    tool.run(new String[] {
      "src/test/resources/fddiff/carPhone.fd",
      "src/test/resources/fddiff/carLocking.fd",
      "-semdiff",
      "open"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.contains("The first input FD is a refinement of the second input FD."));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSemDiffOpenRefinementNoArg() {
    tool.run(new String[] {
      "src/test/resources/fddiff/carPhone.fd",
      "src/test/resources/fddiff/carLocking.fd",
      "-semdiff",
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.contains("The first input FD is a refinement of the second input FD."));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSemDiffOpenNoRefinementArgClosed() {
    tool.run(new String[] {
      "src/test/resources/fddiff/carPhone.fd",
      "src/test/resources/fddiff/carLocking.fd",
      "-semdiff",
      "closed"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.contains("Diff witness: "));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testHelp() {
    tool.run(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FACT.jar <test1.fd> <test2.fd>? [analysis options]"));
    assertEquals(0, Log.getErrorCount());
  }
}
