/* (c) https://github.com/MontiCore/monticore */

package test.fc;

import de.monticore.featureconfiguration.FeatureConfigurationCLI;
import de.monticore.featureconfiguration.FeatureConfigurationMill;
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

public class FeatureConfigurationToolTest extends AbstractLangTest {

  protected PrintStream originalOut;

  protected ByteArrayOutputStream out;

  @BeforeClass
  public static void initMill(){
    FeatureConfigurationMill.init();
  }

  @Before
  public void produceFDSymbol(){
    //Process FD first to obtain stored FD symbol. Otherwise, all test cases would yield a warning
    fdTool.run("src/test/resources/fdvalid/CarNavigation.fd",
        Paths.get("target/symbols"), fdParser, fdDeSer);
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
    FeatureConfigurationCLI.main(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureConfigurationTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testHelpLong() {
    FeatureConfigurationCLI.main(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureConfigurationTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParseValidModel() {
    FeatureConfigurationCLI
        .main(new String[] { "-i", validFC("BasicCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationCLI
        .main(new String[] { "-i", validFC("PremiumCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationCLI
        .main(new String[] { "-input", validFC("SelectImported"), "-path", "src/test/resources"});
    FeatureConfigurationCLI
        .main(new String[] { "-input", validFC("SelectNone"), "-path", "src/test/resources"});
    FeatureConfigurationCLI
        .main(new String[] { "-input", validFC("SelectOne"), "-path", "src/test/resources"});
    FeatureConfigurationCLI
        .main(new String[] { "-input", validFC("SelectSome"), "-path", "src/test/resources"});
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testWithoutSetPath() {
    FeatureConfigurationCLI.main(
        new String[] {
            "-i", "src/test/resources/phone/BasicPhone.fc"
        });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToConsole() {
    FeatureConfigurationCLI.main(new String[] {
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
        + "  CarNavigation,VoiceControl,Display,SmallScreen,GPS,Memory,Small\n"
        + "}", printed);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSymbolTable() {
    FeatureConfigurationCLI.main( new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "target/symbols",
        "-s", "testSymbolTable.fcsymbols"
    });
    assertTrue(new File("target/testSymbolTable.fcsymbols").exists());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSymbolTableWithoutArgs() {
    FeatureConfigurationCLI.main( new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "target/symbols",
        "-s"
    });

    String printed = out.toString().trim();
    assertEquals("{\n"
        + "  \"generated-using\": \"www.MontiCore.de technology\",\n"
        + "  \"name\": \"BasicCarNavigation\",\n"
        + "      \"package\": \"fcvalid\",\n"
        + "      \"symbols\": [\n"
        + "      {\n"
        + "        \"kind\": \"de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol\",\n"
        + "        \"name\": \"BasicCarNavigation\",\n"
        + "          \"featureDiagram\": \"fdvalid.CarNavigation\",\n"
        + "          \"selectedFeatures\": [\n"
        + "          \"CarNavigation\",\n"
        + "          \"Display\",\n"
        + "          \"GPS\",\n"
        + "          \"Memory\",\n"
        + "          \"VoiceControl\",\n"
        + "          \"Small\",\n"
        + "          \"SmallScreen\"\n"
        + "        ]\n"
        + "      }\n"
        + "    ]\n"
        + "  }", printed);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToFile() {
    FeatureConfigurationCLI.main(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "src/test/resources:target/symbols",
        "-pp", "BasicCarNavigationOut.fc"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(new File("target/BasicCarNavigationOut.fc").exists());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSetOutput() {
    FeatureConfigurationCLI.main(
        new String[] {
            "-i", validFC("BasicCarNavigation"),
            "-path", "src/test/resources",
            "-o", "target/mytarget",
            "-pp", "BasicCarNavigation.fc"});

    assertTrue(new File("target/mytarget/BasicCarNavigation.fc").exists());
    assertEquals(0, Log.getErrorCount());
  }




  private String validFC(String name) {
    return "src/test/resources/fcvalid/" + name + ".fc";
  }

}
