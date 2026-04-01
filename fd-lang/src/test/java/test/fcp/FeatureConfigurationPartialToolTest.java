/* (c) https://github.com/MontiCore/monticore */

package test.fcp;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialTool;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.AbstractLangTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    // assure a clean globalScope afterwarts
    FeatureConfigurationPartialMill.globalScope().clear();
  }

  public void redirectSysOut() {
    originalOut = System.out;
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
  }

  @BeforeEach
  public void setup() {
    initMill();
    produceFDSymbol();
    redirectSysOut();
  }

  @AfterEach
  public void restoreSysOut() {
    System.setOut(originalOut);
  }

  @Test
  public void testHelp() {
    new FeatureConfigurationPartialTool().run(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar MCFeatureConfigurationPartial.jar"), printed);
    assertNoFindings();
  }

  @Test
  public void testHelpLong() {
    new FeatureConfigurationPartialTool().run(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar MCFeatureConfigurationPartial.jar"));
    assertNoFindings();
  }

  @Test
  public void testParseValidModel() {
    FeatureConfigurationPartialMill.reset();
    new FeatureConfigurationPartialTool().run(new String[] { "-i", validFC("BasicCarNavigation"), "-path", "src/test/resources"});
    new FeatureConfigurationPartialTool().run(new String[] { "-i", validFC("SelectImported"), "-path", "src/test/resources"});
    new FeatureConfigurationPartialTool().run(new String[] { "-input", validFC("SelectNone"), "-path", "src/test/resources"});
    new FeatureConfigurationPartialTool().run(new String[] { "-input", validFC("SelectOne"), "-path", "src/test/resources"});
    new FeatureConfigurationPartialTool().run(new String[] { "-input", validFC("SelectSome"), "-path", "src/test/resources"});
    assertNoFindings();
  }

  @Test
  public void testWithoutSetPath() {
    new FeatureConfigurationPartialTool().run(
        new String[] {
            "-i", "src/test/resources/phone/PremiumPhone.fc"
        });
    assertNoFindings();
  }

  @Test
  public void testSymbolTable() {
    new FeatureConfigurationPartialTool().run( new String[] {
        "-i", "src/test/resources/phone/PremiumPhone.fc",
        "-path", "src/test/resources/phone/",
        "-s", "testSymbolTable.pfcsymbols"
    });
    assertTrue(new File("target/testSymbolTable.pfcsymbols").exists());
    assertNoFindings();
  }

  @Test
  public void testSymbolTableWithoutArgs() {
    new FeatureConfigurationPartialTool().run( new String[] {
        "-i", "src/test/resources/phone/PremiumPhone.fc",
        "-path", "src/test/resources/phone/",
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
        + "      \"fullName\": \"PremiumPhone\",\n"
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
    assertNoFindings();
  }

  @Test
  public void testPrettyPrintToConsole() throws IOException {
    new FeatureConfigurationPartialTool().run(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "src/test/resources/",
        "-pp"
    });

    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationPartialMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue(astOpt.isPresent(), "Failed to parse");
    assertNoFindings();

    String printed = out.toString().trim();
    assertNotNull(printed);

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationPartialMill.parser().parse_String(printed);
    assertTrue(prettyAstOpt.isPresent(), "Failed to parse pretty: " + printed);
    assertNoFindings();

    if (!astOpt.get().deepEqualsWithComments(prettyAstOpt.get())) {
      assertEquals("Failed to deep equals", Files.readString(new File(validFC("BasicCarNavigation")).toPath()), printed);
      fail("Failed to deep equals"); // make sure to fail
    }
    assertNoFindings();
  }

  @Test
  public void testPrettyPrintToFile() throws IOException {
    new FeatureConfigurationPartialTool().run(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "src/test/resources/",
        "-pp", "BasicCarNavigationOut.fc"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(new File("target/BasicCarNavigationOut.fc").exists());
    assertNoFindings();

    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationPartialMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue(astOpt.isPresent(), "Failed to parse");
    assertNoFindings();

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationPartialMill.parser().parse("target/BasicCarNavigationOut.fc");
    assertTrue(prettyAstOpt.isPresent(), "Failed to parse pretty");
    assertNoFindings();

    if (!astOpt.get().deepEqualsWithComments(prettyAstOpt.get())) {
      assertEquals("Failed to deep equals",
              Files.readString(new File(validFC("BasicCarNavigation")).toPath()),
              Files.readString(new File("target/BasicCarNavigationOut.fc").toPath()));
      fail("Failed to deep equals"); // make sure to fail
    }
    assertNoFindings();
  }

  @Test
  public void testSetOutput() throws IOException {
    new FeatureConfigurationPartialTool().run(
        new String[] {
            "-i", validFC("BasicCarNavigation"),
            "-path", "src/test/resources",
            "-o", "target/mytarget",
            "-pp", "BasicCarNavigation.fc"});

    assertTrue(new File("target/mytarget/BasicCarNavigation.fc").exists());
    assertNoFindings();
    
    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationPartialMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue(astOpt.isPresent(), "Failed to parse");
    assertNoFindings();

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationPartialMill.parser().parse("target/mytarget/BasicCarNavigation.fc");
    assertTrue(prettyAstOpt.isPresent(), "Failed to parse pretty");
    assertNoFindings();

    if (!astOpt.get().deepEqualsWithComments(prettyAstOpt.get())) {
      assertEquals("Failed to deep equals",
              Files.readString(new File(validFC("BasicCarNavigation")).toPath()),
              Files.readString(new File("target/mytarget/BasicCarNavigation.fc").toPath()));
      fail("Failed to deep equals"); // make sure to fail
    }
    assertNoFindings();
  }

  @Test
  public void testSetOutput22() throws IOException {
    new FeatureConfigurationPartialTool().run(
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
