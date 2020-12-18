/* (c) https://github.com/MontiCore/monticore */
package test.fcp;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.io.paths.ModelPath;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractLangTest;

import java.nio.file.Paths;

public class FeatureConfigurationPartialCoCoTest extends AbstractLangTest {

  @BeforeClass
  public static void initMills() {
    FeatureConfigurationPartialMill.reset();
    FeatureConfigurationMill.reset();
    FeatureConfigurationPartialMill.init();
    FeatureConfigurationPartialMill.globalScope();
    FeatureDiagramMill.init();
    FeatureDiagramMill.globalScope();
  }

  @Test
  public void testWrongBlock() {
    ASTFCCompilationUnit ast = setupSymbolTable("WrongBlock.fc");
    fcpTool.checkCoCos(ast);
    assertErrorCode("0xFC203");
  }

  protected ASTFCCompilationUnit setupSymbolTable(String modelFile) {
    ModelPath mp = new ModelPath(Paths.get("src/test/resources"));
    ASTFCCompilationUnit ast = fcpTool.parse("src/test/resources/pfcinvalid/" + modelFile, fcpParser);
    fcpTool.createSymbolTable(ast, mp);
    return ast;
  }
}
