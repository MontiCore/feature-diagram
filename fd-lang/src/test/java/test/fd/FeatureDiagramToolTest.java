/* (c) https://github.com/MontiCore/monticore */

package test.fd;

import de.monticore.featurediagram.FeatureDiagramTool;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class FeatureDiagramToolTest {

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
    FeatureDiagramTool.main(new String[] { "-h" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureDiagramTool.jar"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testHelpLong() {
    FeatureDiagramTool.main(new String[] { "-help" });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertTrue(printed.startsWith("usage: java -jar FeatureDiagramTool.jar"));
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
    FeatureDiagramTool.main( new String[] {
        "-i", validFD("BasicElements"),
        "-s", "testSymbolTable.symbols"
    });
    assertTrue(new File("target/testSymbolTable.symbols").exists());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSymbolTableWithoutArgs() {
    FeatureDiagramTool.main( new String[] {
        "-i", validFD("BasicElements"),
        "-s"
    });

    String printed = out.toString().trim();
    assertEquals("{\n"
        + "  \"name\": \"BasicElements\",\n"
        + "      \"package\": \"fdvalid\",\n"
        + "      \"symbols\": [\n"
        + "      {\n"
        + "        \"kind\": \"de.monticore.featurediagram._symboltable.FeatureDiagramSymbol\",\n"
        + "          \"name\": \"BasicElements\",\n"
        + "          \"features\": [\n"
        + "          \"A\",\n"
        + "          \"B\",\n"
        + "          \"C\",\n"
        + "          \"D\",\n"
        + "          \"E\",\n"
        + "          \"F\",\n"
        + "          \"G\",\n"
        + "          \"H\",\n"
        + "          \"I\",\n"
        + "          \"J\",\n"
        + "          \"K\",\n"
        + "          \"L\",\n"
        + "          \"M\"\n"
        + "        ]\n"
        + "      }\n"
        + "    ]\n"
        + "  }", printed);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testPrettyPrint() {
    FeatureDiagramTool.main(new String[] {
        "-i", validFD("BasicElements"),
        "-pp"
    });

    String printed = out.toString().trim();
    assertNotNull(printed);
    assertEquals("/* (c) https://github.com/MontiCore/monticore */\n"
        + "package fdvalid;\n"
        + "\n"
        + "featurediagram BasicElements {\n"
        + "  A -> B & C? & D;\n"
        + "  A -> E ^ F ^ G;\n"
        + "  C -> H | I | J;\n"
        + "  D -> [0..*] of {K, L, M};\n"
        + "  M requires E;\n"
        + "  C excludes I;\n"
        + "  E excludes J;\n"
        + "}", printed);
    assertEquals(0, Log.getErrorCount());
  }

  private String validFD(String name) {
    return "src/test/resources/fdvalid/" + name + ".fd";
  }

  private String invalidFD(String name) {
    return "src/test/resources/fdinvalid/" + name + ".fd";
  }

}
