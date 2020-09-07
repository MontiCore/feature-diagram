/* (c) https://github.com/MontiCore/monticore */

package test.fc;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class FeatureConfigurationToolTest {

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
  public void testHelp() {
    FeatureConfigurationTool.main(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureConfigurationTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testHelpLong() {
    FeatureConfigurationTool.main(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureConfigurationTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParseValidModel() {
    FeatureConfigurationTool.main(new String[] { "-i", validFC("BasicCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationTool.main(new String[] { "-i", validFC("PremiumCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationTool.main(new String[] { "-input", validFC("SelectImported"), "-path", "src/test/resources"});
    FeatureConfigurationTool.main(new String[] { "-input", validFC("SelectNone"), "-path", "src/test/resources"});
    FeatureConfigurationTool.main(new String[] { "-input", validFC("SelectOne"), "-path", "src/test/resources"});
    FeatureConfigurationTool.main(new String[] { "-input", validFC("SelectSome"), "-path", "src/test/resources"});
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testWithoutSetPath() {
    FeatureConfigurationTool.main(
        new String[] {
            "-i", "src/test/resources/phone/PremiumPhone.fc"
        });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToConsole() {
    FeatureConfigurationTool.main(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "src/test/resources",
        "-pp"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureDiagramTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToFile() {
    FeatureConfigurationTool.main(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-pp", "BasicCarNavigationOut.fc"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureDiagramTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSetOutput() {
    FeatureConfigurationTool.main(
        new String[] {
            "-i", validFC("BasicCarNavigation"),
            "-o", "target/mytarget",
            "-pp", "BasicCarNavigation.fc"});

    assertTrue(new File("target/mytarget/BasicCarNavigation.fc").exists());
    assertEquals(0, Log.getErrorCount());
  }




  private String validFC(String name) {
    return "src/test/resources/fcvalid/" + name + ".fc";
  }

  private String invalidFC(String name) {
    return "src/test/resources/fcinvalid/" + name + ".fc";
  }

}
