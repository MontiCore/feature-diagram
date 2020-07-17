/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;

import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tool.FACT;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    FACT.main(new String[] {
        "src/test/resources/FalseOptional.fd",
        "-isValid", "src/test/resources/ValidConfig.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("true"));
  }

  @Test
  public void testAnalysisIsValidFalse() {
    FACT.main(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-isValid", "src/test/resources/InvalidConfig.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("false"));
  }

  @Test
  public void testAnalysisInvalidFC() {
    FACT.main(new String[] {
        "src/test/resources/DeadFeatures.fd",
        "-isValid", "src/test/resources/InvalidConfig2.fc"
    });
    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.endsWith("false"));
  }

  @Test
  public void testInvalidArg() {
    PrintStream originalErr = System.err;
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    System.setErr(new PrintStream(err));
    FACT.main(new String[] {
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
    FACT.main(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FACT.jar <test.fd> [analysis options]"));
  }
}
