/* (c) https://github.com/MontiCore/monticore */

package test.fcp;

import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialTool;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FeatureConfigurationPartialToolTest {

  protected PrintStream originalOut;

  protected ByteArrayOutputStream out;

  @BeforeClass
  public static void produceFDSymbol(){
    //Process FD first to obtain stored FD symbol. Otherwise, all test cases would yield a warning
    FeatureDiagramTool.run("src/test/resources/fdvalid/CarNavigation.fd",
        Paths.get("target/symbols"),
        new ModelPath());

    FeatureDiagramTool.run("src/test/resources/phone/Phone.fd",
        Paths.get("target/symbols"),
        new ModelPath());
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
  public void testSymbolTable() {
    FeatureConfigurationPartialTool.main( new String[] {
        "-i", "src/test/resources/phone/PremiumPhone.fc",
        "-path", "target/symbols",
        "-s", "testSymbolTable.pfcsymbols"
    });
    assertTrue(new File("target/testSymbolTable.pfcsymbols").exists());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSymbolTableWithoutArgs() {
    FeatureConfigurationPartialTool.main( new String[] {
        "-i", "src/test/resources/phone/PremiumPhone.fc",
        "-path", "target/symbols",
        "-s"
    });

    String printed = out.toString().trim();
    assertEquals("{\n"
        + "      \"name\": \"PremiumPhone\",\n"
        + "      \"symbols\": [\n"
        + "      {\n"
        + "        \"kind\": \"de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol\",\n"
        + "        \"name\": \"PremiumPhone\",\n"
        + "          \"featureDiagram\": \"Phone\",\n"
        + "          \"selectedFeatures\": [\n"
        + "          \"Phone\",\n"
        + "          \"Memory\",\n"
        + "          \"OS\",\n"
        + "          \"Camera\",\n"
        + "          \"Screen\",\n"
        + "          \"Internal\",\n"
        + "          \"External\",\n"
        + "          \"Medium\",\n"
        + "          \"Large\",\n"
        + "          \"FruitOS\",\n"
        + "          \"Flexible\",\n"
        + "          \"FullHD\"\n"
        + "        ]\n"
        + "      }\n"
        + "    ]\n"
        + "  }", printed);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToConsole() {
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
        "-path", "target/symbols",
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

}