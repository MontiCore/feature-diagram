/* (c) https://github.com/MontiCore/monticore */
package test.fc;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featureconfiguration._symboltable.IFeatureConfigurationArtifactScope;
import de.monticore.io.paths.MCPath;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FeatureConfigurationSymbolTableTest extends AbstractLangTest {

  protected IFeatureConfigurationArtifactScope setupSymbolTable(String modelFile) {
    MCPath mp = new MCPath(Paths.get("src", "test", "resources"));
    ASTFCCompilationUnit ast = fcTool.parse(modelFile);
    return fcTool.createSymbolTable(ast, mp);
  }

  @BeforeClass
  public static void initMill(){
    FeatureConfigurationMill.init();
  }

  @Test
  public void testSymbolTableCreation() {
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/BasicCarNavigation.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/PremiumCarNavigation.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/SelectImported.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/SelectNone.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/SelectOne.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/SelectSome.fc"));
    assertNotNull(setupSymbolTable("src/test/resources/fcvalid/StarImport.fc"));
  }

  @Test
  public void testDetail() {
    String model = "src/test/resources/fcvalid/BasicCarNavigation.fc";
    IFeatureConfigurationArtifactScope scope = setupSymbolTable(model);

    assertTrue(null != scope);
    FeatureConfigurationSymbol fd = scope.resolveFeatureConfiguration("BasicCarNavigation")
        .orElse(null);
    assertTrue(null != fd);
    assertEquals("BasicCarNavigation", fd.getName());
    assertEquals("CarNavigation", fd.getFeatureDiagram().getName());

    assertEquals(7, fd.sizeSelectedFeatures());
    List<String> selectedFeatureNames =
      fd.getSelectedFeaturesList().stream().map(f -> f.getName())
        .collect(Collectors.toList());

    assertTrue(selectedFeatureNames.contains("CarNavigation"));
    assertTrue(selectedFeatureNames.contains("VoiceControl"));
    assertTrue(selectedFeatureNames.contains("Display"));
    assertTrue(selectedFeatureNames.contains("SmallScreen"));
    assertTrue(selectedFeatureNames.contains("GPS"));
    assertTrue(selectedFeatureNames.contains("Memory"));
    assertTrue(selectedFeatureNames.contains("Small"));
  }

  @Test
  public void testImport() {
    String model = "src/test/resources/fcvalid/SelectImported.fc";
    IFeatureConfigurationArtifactScope scope = setupSymbolTable(model);

    assertTrue(null != scope);
    FeatureConfigurationSymbol fc = scope.resolveFeatureConfiguration("SelectImported")
        .orElse(null);
    assertTrue(null != fc);
    assertEquals(2, fc.sizeSelectedFeatures());
    List<String> selectedFeatureNames =
      fc.getSelectedFeaturesList().stream().map(f -> f.getName())
        .collect(Collectors.toList());

    assertTrue(selectedFeatureNames.contains("AA"));
    assertTrue(selectedFeatureNames.contains("B"));
  }

}