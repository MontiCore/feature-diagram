/* (c) https://github.com/MontiCore/monticore */
package test.fd;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._symboltable.*;
import de.monticore.io.FileReaderWriter;
import de.monticore.symboltable.serialization.JsonPrinter;
import org.junit.Test;
import org.junit.Before;
import test.AbstractLangTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeatureDiagramDeSerTest extends AbstractLangTest {

  protected IFeatureDiagramArtifactScope setupSymbolTable(String modelFile) {
    ASTFDCompilationUnit ast = fdTool.parse("src/test/resources/" + modelFile);
    return fdTool.createSymbolTable(ast);
  }

  @Before
  public void initMills() {
    FeatureDiagramMill.init();
  }

  @Test
  public void testLoad() {
    FeatureDiagramSymbols2Json s2j = new FeatureDiagramSymbols2Json();
    IFeatureDiagramArtifactScope scope = s2j
        .load("src/test/resources/symbols/CarNavigation.fdsym");
    assertTrue(null != scope);
    assertEquals("CarNavigation", scope.getName());
    assertEquals("fdvalid", scope.getPackageName());
    assertEquals(0, scope.getImportsList().size());
    assertEquals(true, scope.getTopLevelSymbol().isPresent());
    assertEquals("CarNavigation", scope.getTopLevelSymbol().get().getName());
    assertEquals(1, scope.getLocalFeatureDiagramSymbols().size());
    assertEquals(0, scope.getLocalFeatureSymbols().size());

    FeatureDiagramSymbol actualSymbol = scope.getLocalFeatureDiagramSymbols().get(0);
    assertEquals("CarNavigation", actualSymbol.getName());
    assertEquals("CarNavigation", actualSymbol.getSpannedScope().getName());
    assertEquals(17, actualSymbol.getSpannedScope().getLocalFeatureSymbols().size());
    assertEquals(0, actualSymbol.getSpannedScope().getSubScopes().size());
    assertEquals(0, actualSymbol.getSpannedScope().getLocalFeatureDiagramSymbols().size());
    assertEquals("CarNavigation", actualSymbol.getSpannedScope().getSpanningSymbol().getName());
  }

  /**
   * Wrote this test instead of testStore
   */
  @Test
  public void testStore() {
    JsonPrinter.enableIndentation();
    IFeatureDiagramArtifactScope fdScope = setupSymbolTable("fdvalid/CarNavigation.fd");
    FeatureDiagramSymbols2Json s2j = new FeatureDiagramSymbols2Json();
    s2j.store(fdScope, "target/test-symbols/fdvalid/CarNavigation.fdsym");

    Path expectedPath = Paths.get("target/test-symbols/fdvalid/CarNavigation.fdsym");
    assertTrue(expectedPath.toFile().exists());

    String expected = "{\"generated-using\":\"www.MontiCore.detechnology\","
            + "\"name\":\"CarNavigation\","
            + "\"package\":\"fdvalid\","
            + "\"symbols\":["
            + "{\"kind\":\"de.monticore.featurediagram._symboltable.FeatureDiagramSymbol\","
            + "\"name\":\"CarNavigation\","
            + "\"spannedScope\":{"
            + "\"symbols\":["
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"CarNavigation\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"Display\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"GPS\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"PreinstalledMaps\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"Memory\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"VoiceControl\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"TouchControl\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"Small\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"Medium\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"Large\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"SmallScreen\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"LargeScreen\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"Europe\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"NorthAmerica\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"SouthAmerica\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"Asia\""
            + "},"
            + "{"
            + "\"kind\":\"de.monticore.featurediagram._symboltable.FeatureSymbol\","
            + "\"name\":\"Africa\""
            + "}]}}]}";
    String actual = FileReaderWriter.readFromFile(expectedPath);

    // ignore whitespace
    expected = expected.replaceAll("\\s", "");
    actual = actual.replaceAll("\\s", "");

    assertEquals(expected, actual);
  }

}
