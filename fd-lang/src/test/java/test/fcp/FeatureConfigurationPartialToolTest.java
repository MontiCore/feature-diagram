/* (c) https://github.com/MontiCore/monticore */

package test.fcp;

import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialCLI;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FeatureConfigurationPartialToolTest extends AbstractLangTest {

  protected PrintStream originalOut;

  protected ByteArrayOutputStream out;

  @BeforeClass
  public static void initMill(){
    FeatureConfigurationPartialMill.init();
  }

  @Before
  public void produceFDSymbol(){
    //Process FD first to obtain stored FD symbol. Otherwise, all test cases would yield a warning
    fdTool.run("src/test/resources/fdvalid/CarNavigation.fd",
        Paths.get("target/symbols"),
        fdParser, fdDeSer);

    fdTool.run("src/test/resources/phone/Phone.fd",
        Paths.get("target/symbols"),
        fdParser, fdDeSer);
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
    FeatureConfigurationPartialCLI.main(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureConfigurationPartialTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testHelpLong() {
    FeatureConfigurationPartialCLI.main(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureConfigurationPartialTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParseValidModel() {
    FeatureConfigurationPartialCLI
        .main(new String[] { "-i", validFC("BasicCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationPartialCLI
        .main(new String[] { "-i", validFC("SelectImported"), "-path", "src/test/resources"});
    FeatureConfigurationPartialCLI
        .main(new String[] { "-input", validFC("SelectNone"), "-path", "src/test/resources"});
    FeatureConfigurationPartialCLI
        .main(new String[] { "-input", validFC("SelectOne"), "-path", "src/test/resources"});
    FeatureConfigurationPartialCLI
        .main(new String[] { "-input", validFC("SelectSome"), "-path", "src/test/resources"});
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testWithoutSetPath() {
    FeatureConfigurationPartialCLI.main(
        new String[] {
            "-i", "src/test/resources/phone/PremiumPhone.fc"
        });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSymbolTable() {
    FeatureConfigurationPartialCLI.main( new String[] {
        "-i", "src/test/resources/phone/PremiumPhone.fc",
        "-path", "target/symbols",
        "-s", "testSymbolTable.pfcsymbols"
    });
    assertTrue(new File("target/testSymbolTable.pfcsymbols").exists());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSymbolTableWithoutArgs() {
    FeatureConfigurationPartialCLI.main( new String[] {
        "-i", "src/test/resources/phone/PremiumPhone.fc",
        "-path", "target/symbols",
        "-s"
    });

    String printed = out.toString().trim();
    assertEquals("{\n"
        + "  \"generated-using\": \"www.MontiCore.de technology\",\n"
        + "  \"name\": \"PremiumPhone\"  ,\n"
        + "    \"symbols\": [\n"
        + "    {\n"
        + "      \"kind\": \"de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol\",\n"
        + "      \"name\": \"PremiumPhone\",\n"
        + "      \"featureDiagram\": \"Phone\",\n"
        + "        \"selectedFeatures\": [\n"
        + "        \"Phone\",\n"
        + "        \"Memory\",\n"
        + "        \"OS\",\n"
        + "        \"Camera\",\n"
        + "        \"Screen\",\n"
        + "        \"Internal\",\n"
        + "        \"External\",\n"
        + "        \"Medium\",\n"
        + "        \"Large\",\n"
        + "        \"FruitOS\",\n"
        + "        \"Flexible\",\n"
        + "        \"FullHD\"\n"
        + "      ]\n"
        + "    }\n"
        + "  ]\n"
        + "}", printed);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToConsole() {
    FeatureConfigurationPartialCLI.main(new String[] {
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
    FeatureConfigurationPartialCLI.main(new String[] {
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
    FeatureConfigurationPartialCLI.main(
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
