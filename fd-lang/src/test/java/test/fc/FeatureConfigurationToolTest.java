/* (c) https://github.com/MontiCore/monticore */

package test.fc;

import de.monticore.featureconfiguration.FeatureConfigurationTool;
import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
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

public class FeatureConfigurationToolTest extends AbstractLangTest {

  protected PrintStream originalOut;

  protected ByteArrayOutputStream out;


  public void initMill(){
    FeatureConfigurationMill.init();
  }

  public void produceFDSymbol(){
    //Process FD first to obtain stored FD symbol. Otherwise, all test cases would yield a warning
    fdTool.run("src/test/resources/fdvalid/CarNavigation.fd",
        Paths.get("target/symbols"));
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
    new FeatureConfigurationTool().run(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar MCFeatureConfiguration.jar"));
    assertNoFindings();
  }

  @Test
  public void testHelpLong() {
    new FeatureConfigurationTool().run(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar MCFeatureConfiguration.jar"));
    assertNoFindings();
  }

  @Test
  public void testParseValidModel() {
    FeatureConfigurationTool
        .main(new String[] { "-i", validFC("BasicCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationTool
        .main(new String[] { "-i", validFC("PremiumCarNavigation"), "-path", "src/test/resources"});
    FeatureConfigurationTool
        .main(new String[] { "-input", validFC("SelectImported"), "-path", "src/test/resources"});
    FeatureConfigurationTool
        .main(new String[] { "-input", validFC("SelectNone"), "-path", "src/test/resources"});
    FeatureConfigurationTool
        .main(new String[] { "-input", validFC("SelectOne"), "-path", "src/test/resources"});
    FeatureConfigurationTool
        .main(new String[] { "-input", validFC("SelectSome"), "-path", "src/test/resources"});
    assertNoFindings();
  }

  @Test
  public void testWithoutSetPath() {
    new FeatureConfigurationTool().run(
        new String[] {
            "-i", "src/test/resources/phone/BasicPhone.fc"
        });
    assertNoFindings();
  }

  @Test
  public void testPrettyPrintToConsole() throws IOException  {
    new FeatureConfigurationTool().run(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "target/symbols",
        "-pp"
    });

    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue(astOpt.isPresent(), "Failed to parse");
    assertNoFindings();

    String printed = out.toString().trim();
    assertNotNull(printed);

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationMill.parser().parse_String(printed);
    assertTrue(prettyAstOpt.isPresent(), "Failed to parse pretty: " + printed);
    assertNoFindings();

    if (!astOpt.get().deepEqualsWithComments(prettyAstOpt.get())) {
      assertEquals(Files.readString(new File(validFC("BasicCarNavigation")).toPath()), printed, "Failed to deep equals");
      fail("Failed to deep equals"); // make sure to fail
    }
    assertNoFindings();
  }

  @Test
  public void testSymbolTable() {
    new FeatureConfigurationTool().run( new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "target/symbols",
        "-s", "testSymbolTable.fcsymbols"
    });
    assertTrue(new File("target/testSymbolTable.fcsymbols").exists());
    assertNoFindings();
  }

  @Test
  public void testSymbolTableWithoutArgs() {
    new FeatureConfigurationTool().run( new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "target/symbols",
        "-s"
    });

    String printed = out.toString().trim();
    assertEquals("{\n"
        + "  \"generated-using\": \"www.MontiCore.de technology\",\n"
        + "  \"name\": \"BasicCarNavigation\",\n"
        + "  \"package\": \"fcvalid\",\n"
        + "  \"symbols\": [\n"
        + "    {\n"
        + "      \"kind\": \"de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol\",\n"
        + "      \"name\": \"BasicCarNavigation\",\n"
        + "      \"fullName\": \"fcvalid.BasicCarNavigation\",\n"
        + "      \"packageName\": \"fcvalid\",\n"
        + "      \"featureDiagram\": \"fdvalid.CarNavigation\",\n"
        + "      \"selectedFeatures\": [\n"
        + "        \"CarNavigation\",\n"
        + "        \"Display\",\n"
        + "        \"GPS\",\n"
        + "        \"Memory\",\n"
        + "        \"VoiceControl\",\n"
        + "        \"Small\",\n"
        + "        \"SmallScreen\"\n"
        + "      ]\n"
        + "    }\n"
        + "  ]\n"
        + "}", printed);
    assertNoFindings();
  }

  @Test
  public void testPrettyPrintToFile() throws IOException{
    new FeatureConfigurationTool().run(new String[] {
        "-i", validFC("BasicCarNavigation"),
        "-path", "src/test/resources", "target/symbols",
        "-pp", "BasicCarNavigationOut.fc"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(new File("target/BasicCarNavigationOut.fc").exists());
    assertNoFindings();
    
    assertTrue(new File("target/BasicCarNavigationOut.fc").exists());
    assertNoFindings();

    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue(astOpt.isPresent(), "Failed to parse");
    assertNoFindings();

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationMill.parser().parse("target/BasicCarNavigationOut.fc");
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
    new FeatureConfigurationTool().run(
        new String[] {
            "-i", validFC("BasicCarNavigation"),
            "-path", "src/test/resources",
            "-o", "target/mytarget",
            "-pp", "BasicCarNavigation.fc"});

    assertTrue(new File("target/mytarget/BasicCarNavigation.fc").exists());
    assertNoFindings();

    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationMill.parser().parse(validFC("BasicCarNavigation"));
    assertTrue(astOpt.isPresent(), "Failed to parse");
    assertNoFindings();

    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationMill.parser().parse("target/mytarget/BasicCarNavigation.fc");
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




  private String validFC(String name) {
    return "src/test/resources/fcvalid/" + name + ".fc";
  }

}
