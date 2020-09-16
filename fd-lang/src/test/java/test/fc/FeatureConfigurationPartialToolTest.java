/* (c) https://github.com/MontiCore/monticore */

package test.fc;

import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialTool;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FeatureConfigurationPartialToolTest {

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
    FeatureConfigurationPartialTool.main(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureConfigurationPartialTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testHelpLong() {
    FeatureConfigurationPartialTool.main(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureConfigurationPartialTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParseValidModel() {
    FeatureConfigurationPartialTool.main(new String[] { "-i", validFC("BasicCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationPartialTool.main(new String[] { "-i", validFC("SelectImported"), "-path", "src/test/resources"});
    FeatureConfigurationPartialTool.main(new String[] { "-input", validFC("SelectNone"), "-path", "src/test/resources"});
    FeatureConfigurationPartialTool.main(new String[] { "-input", validFC("SelectOne"), "-path", "src/test/resources"});
    FeatureConfigurationPartialTool.main(new String[] { "-input", validFC("SelectSome"), "-path", "src/test/resources"});
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testWithoutSetPath() {
    FeatureConfigurationPartialTool.main(
        new String[] {
            "-i", "src/test/resources/phone/PremiumPhone.fc"
        });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToConsole() {
    //Process FD first to obtain stored FD symbol
    FeatureDiagramTool.run("src/test/resources/fdvalid/CarNavigation.fd",
        Paths.get("target/symbols"),
        new ModelPath());

    FeatureConfigurationPartialTool.main(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "target/symbols",
        "-pp"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertEquals("/* (c) https://github.com/MontiCore/monticore */\n"
        + "package fcvalid;\n"
        + "\n"
        + "import fdvalid.CarNavigation;\n"
        + "\n"
        + "featureconfig BasicCarNavigation for fdvalid.CarNavigation {\n"
        + "  select { CarNavigation,VoiceControl,Display,SmallScreen,GPS,Memory,Small }\n"
        + "  exclude { Large }\n"
        + "}", printed);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToFile() {
    FeatureConfigurationPartialTool.main(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "src/test/resources",
        "-pp", "BasicCarNavigationOut.fc"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(new File("target/BasicCarNavigationOut.fc").exists());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSetOutput() {
    FeatureConfigurationPartialTool.main(
        new String[] {
            "-i", validFC("BasicCarNavigation"),
            "-path", "src/test/resources",
            "-o", "target/mytarget",
            "-pp", "BasicCarNavigation.fc"});

    assertTrue(new File("target/mytarget/BasicCarNavigation.fc").exists());
    assertEquals(0, Log.getErrorCount());
  }




  private String validFC(String name) {
    return "src/test/resources/pfcvalid/" + name + ".fc";
  }

  private String invalidFC(String name) {
    return "src/test/resources/pfcinvalid/" + name + ".fc";
  }

}
