/* (c) https://github.com/MontiCore/monticore */
package test.fd;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._cocos.FeatureDiagramCoCos;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.FeatureDiagramGlobalScope;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbolTableCreatorDelegator;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;
import test.AbstractTest;

import java.io.IOException;

import static org.junit.Assert.*;

public class FeatureDiagramCoCoTest extends AbstractTest {

  @Test
  public void testValid() throws IOException {
    String dir = "src/test/resources/fdvalid/";
    FeatureDiagramCoCos.checkAll(readFile(dir + "BasicElements.fd"));
    FeatureDiagramCoCos.checkAll(readFile(dir + "Car.fd"));
    FeatureDiagramCoCos.checkAll(readFile(dir + "CarNavigation.fd"));
    FeatureDiagramCoCos.checkAll(readFile(dir + "GraphLibrary.fd"));
    FeatureDiagramCoCos.checkAll(readFile(dir + "Phone.fd"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testCTCFeatureDoesNotExist() throws IOException {
    testCoCo("CTCFeatureDoesNotExist.fd", "0xFD006", "0xFD006");
  }

  @Test
  public void testFeatureCycle() throws IOException {
    testCoCo("FeatureCycle.fd", "0xFD008");
  }

  @Test
  public void testNonUniqueNameInGroup() throws IOException {
    testCoCo("NonUniqueNameInGroup.fd", "0xFD009");
  }

  @Test
  public void testSelfLoopInGroup() throws IOException {
    testCoCo("SelfLoopInGroup.fd", "0xFD007", "0xFD008");
  }

  @Test
  public void testTwoParents() throws IOException {
    testCoCo("TwoParents.fd", "0xFD008");
  }

  @Test
  public void testTwoRoots() throws IOException {
    testCoCo("TwoRoots.fd", "0xFD001", "0xFD004", "0xFD004");
  }

  protected void testCoCo(String modelName, String... errorCode) throws IOException {
    FeatureDiagramCoCos.checkAll(readFile("src/test/resources/fdinvalid/" + modelName));
    assertErrorCode(errorCode);
  }

  protected ASTFDCompilationUnit readFile(String modelFile, ModelPath mp)
      throws IOException {
    ASTFDCompilationUnit ast = new FeatureDiagramParser().parse(modelFile).orElse(null);
    assertNotNull(ast);
    IFeatureDiagramGlobalScope globalScope = FeatureDiagramMill
        .featureDiagramGlobalScopeBuilder()
        .setModelPath(mp)
        .setModelFileExtension("fd")
        .build();

    FeatureDiagramSymbolTableCreatorDelegator symbolTable = FeatureDiagramMill
        .featureDiagramSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();

    symbolTable.createFromAST(ast);
    return ast;
  }

  protected ASTFDCompilationUnit readFile(String modelName)
      throws IOException {
    return readFile(modelName, new ModelPath());
  }

}
