/* (c) https://github.com/MontiCore/monticore */

package test.fcp;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialTool;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.AbstractLangTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class FeatureConfigurationPartialToolTest extends AbstractLangTest {

  protected PrintStream originalOut;

  protected ByteArrayOutputStream out;


  public void initMill(){
    FeatureConfigurationPartialMill.init();
  }

  public void produceFDSymbol(){
    //Process FD first to obtain stored FD symbol. Otherwise, all test cases would yield a warning
    fdTool.run("src/test/resources/fdvalid/CarNavigation.fd",
        Paths.get("target/symbols"));

    fdTool.run("src/test/resources/phone/Phone.fd",
        Paths.get("target/symbols"));
  }

  public void redirectSysOut() {
    originalOut = System.out;
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
  }

  @Before
  public void setup() {
    initMill();
    produceFDSymbol();
    redirectSysOut();
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
    assertTrue(printed, printed.startsWith("usage: java -jar MCFeatureConfigurationPartial.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testHelpLong() {
    FeatureConfigurationPartialTool.main(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar MCFeatureConfigurationPartial.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParseValidModel() {
    FeatureConfigurationPartialMill.reset();
    FeatureConfigurationPartialTool
        .main(new String[] { "-i", validFC("BasicCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationPartialTool
        .main(new String[] { "-i", validFC("SelectImported"), "-path", "src/test/resources"});
    FeatureConfigurationPartialTool
        .main(new String[] { "-input", validFC("SelectNone"), "-path", "src/test/resources"});
    FeatureConfigurationPartialTool
        .main(new String[] { "-input", validFC("SelectOne"), "-path", "src/test/resources"});
    FeatureConfigurationPartialTool
        .main(new String[] { "-input", validFC("SelectSome"), "-path", "src/test/resources"});
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
        + "  \"generated-using\": \"www.MontiCore.de technology\",\n"
        + "  \"name\": \"PremiumPhone\",\n"
        + "  \"symbols\": [\n"
        + "    {\n"
        + "      \"kind\": \"de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol\",\n"
        + "      \"name\": \"PremiumPhone\",\n"
        + "      \"featureDiagram\": \"Phone\",\n"
        + "      \"selectedFeatures\": [\n"
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
  public void testPrettyPrintToConsole() throws IOException {
    FeatureConfigurationPartialTool.main(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "target/symbols",
        "-pp"
    });

    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationPartialMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue("Failed to parse", astOpt.isPresent());
    assertEquals(0, Log.getErrorCount());

    String printed = out.toString().trim();
    assertNotNull(printed);

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationPartialMill.parser().parse_String(printed);
    assertTrue("Failed to parse pretty: " + printed, prettyAstOpt.isPresent());
    assertEquals(0, Log.getErrorCount());

    if (!astOpt.get().deepEqualsWithComments(prettyAstOpt.get())) {
      assertEquals("Failed to deep equals", Files.readString(new File(validFC("BasicCarNavigation")).toPath()), printed);
      fail("Failed to deep equals"); // make sure to fail
    }
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrintToFile() throws IOException {
    FeatureConfigurationPartialTool.main(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "target/symbols",
        "-pp", "BasicCarNavigationOut.fc"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(new File("target/BasicCarNavigationOut.fc").exists());
    assertEquals(0, Log.getErrorCount());

    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationPartialMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue("Failed to parse", astOpt.isPresent());
    assertEquals(0, Log.getErrorCount());

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationPartialMill.parser().parse("target/BasicCarNavigationOut.fc");
    assertTrue("Failed to parse pretty", prettyAstOpt.isPresent());
    assertEquals(0, Log.getErrorCount());

    if (!astOpt.get().deepEqualsWithComments(prettyAstOpt.get())) {
      assertEquals("Failed to deep equals",
              Files.readString(new File(validFC("BasicCarNavigation")).toPath()),
              Files.readString(new File("target/BasicCarNavigationOut.fc").toPath()));
      fail("Failed to deep equals"); // make sure to fail
    }
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSetOutput() throws IOException {
    FeatureConfigurationPartialTool.main(
        new String[] {
            "-i", validFC("BasicCarNavigation"),
            "-path", "src/test/resources",
            "-o", "target/mytarget",
            "-pp", "BasicCarNavigation.fc"});

    assertTrue(new File("target/mytarget/BasicCarNavigation.fc").exists());
    assertEquals(0, Log.getErrorCount());
    
    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationPartialMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue("Failed to parse", astOpt.isPresent());
    assertEquals(0, Log.getErrorCount());

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationPartialMill.parser().parse("target/mytarget/BasicCarNavigation.fc");
    assertTrue("Failed to parse pretty", prettyAstOpt.isPresent());
    assertEquals(0, Log.getErrorCount());

    if (!astOpt.get().deepEqualsWithComments(prettyAstOpt.get())) {
      assertEquals("Failed to deep equals",
              Files.readString(new File(validFC("BasicCarNavigation")).toPath()),
              Files.readString(new File("target/mytarget/BasicCarNavigation.fc").toPath()));
      fail("Failed to deep equals"); // make sure to fail
    }
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSetOutput22() throws IOException {
    FeatureConfigurationPartialTool.main(
            new String[] {
                    "-i", validFC("BasicCarNavigation"),
                    "-path", "src/test/resources",
                    "-o", "target/mytarget",
                    "-pp", "/tmp/BasicCarNavigation.fc"});

  }

  private String validFC(String name) {
    return "src/test/resources/pfcvalid/" + name + ".fc";
  }

}
