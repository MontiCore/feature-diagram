/* (c) https://github.com/MontiCore/monticore */

package test.fd;

import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Optional;

import static org.junit.Assert.*;

public class FeatureDiagramToolTest extends AbstractLangTest {

  protected PrintStream originalOut;

  protected ByteArrayOutputStream out;

  @Before
  public void initMills() {
    FeatureDiagramMill.init();
  }

  @Before
  public void redirectSysOut() {
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
    FeatureDiagramTool.main(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar MCFeatureDiagram.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testHelpLong() {
    FeatureDiagramTool.main(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar MCFeatureDiagram.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testParseValidModel() {
    FeatureDiagramTool.main(new String[] { "-i", validFD("BasicElements") });
    FeatureDiagramTool.main(new String[] { "-i", validFD("GraphLibrary") });
    FeatureDiagramTool.main(new String[] { "-input", validFD("CarNavigation") });
    FeatureDiagramTool.main(new String[] { "-input", validFD("PhoneComplex") });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSetPath() {
    FeatureDiagramTool.main(
        new String[] {
            "-i", validFD("TransitiveImport"),
            "-path", "src/test/resources/",
            "-s", "testSetPath.symbols"
        });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSetOutput() {
    FeatureDiagramTool.main(
        new String[] {
            "-i", validFD("BasicElements"),
            "-o", "target/mytarget",
            "-s", "test.symbols"
        });

    assertTrue(new File("target/mytarget/test.symbols").exists());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSymbolTable() {
    FeatureDiagramTool.main(new String[] {
        "-i", validFD("BasicElements"),
        "-s", "testSymbolTable.symbols"
    });
    assertTrue(new File("target/testSymbolTable.symbols").exists());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSymbolTableWithoutArgs() {
    FeatureDiagramTool.main(new String[] {
            "-i", validFD("BasicElements"),
            "-s"
    });

    String printed = out.toString().trim();
    assertEquals("{\n"
            + "  \"generated-using\": \"www.MontiCore.de technology\",\n"
            + "  \"name\": \"BasicElements\",\n"
            + "  \"package\": \"fdvalid\",\n"
            + "  \"symbols\": [\n"
            + "    {\n"
            + "      \"kind\": \"de.monticore.featurediagram._symboltable.FeatureDiagramSymbol\",\n"
            + "      \"name\": \"BasicElements\",\n"
            + "      \"spannedScope\": {\n"
            + "        \"symbols\": [\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"A\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"B\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"C\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"D\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"E\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"F\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"G\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"H\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"I\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"J\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"K\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"L\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"kind\": \"de.monticore.featurediagram._symboltable.FeatureSymbol\",\n"
            + "            \"name\": \"M\"\n"
            + "          }\n"
            + "        ]\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}", printed);
    assertEquals(0, Log.getErrorCount());
  }


  @Test
  public void testPrettyPrint() throws IOException {
    FeatureDiagramTool.main(new String[] {
        "-i", validFD("BasicElements"),
        "-pp"
    });
    Optional<ASTFDCompilationUnit> astOpt = FeatureDiagramMill.parser().parse(validFD("BasicElements"));
    assertPresent(astOpt);
    assertEquals(0, Log.getErrorCount());

    String printed = out.toString().trim();
    assertNotNull(printed);

    Optional<ASTFDCompilationUnit> prettyAstOpt = FeatureDiagramMill.parser().parse_String(printed);
    assertPresent(prettyAstOpt);
    assertEquals(0, Log.getErrorCount());

    if (!astOpt.get().deepEqualsWithComments(prettyAstOpt.get())) {
      assertEquals("Failed to deep equals", Files.readString(new File(validFD("BasicElements")).toPath()), printed);
      fail("Failed to deep equals"); // make sure to fail
    }
    assertEquals(0, Log.getErrorCount());
  }

  private String validFD(String name) {
    return "src/test/resources/fdvalid/" + name + ".fd";
  }

  private String invalidFD(String name) {
    return "src/test/resources/fdinvalid/" + name + ".fd";
  }

}
