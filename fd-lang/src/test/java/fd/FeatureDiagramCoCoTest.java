/* (c) https://github.com/MontiCore/monticore */
package fd;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._cocos.FeatureDiagramCoCos;
import featurediagram._parser.FeatureDiagramParser;
import featurediagram._symboltable.FeatureDiagramGlobalScope;
import featurediagram._symboltable.FeatureDiagramLanguage;
import featurediagram._symboltable.FeatureDiagramSymbolTableCreatorDelegator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FeatureDiagramCoCoTest {

  @BeforeClass
  public static void disableFailQuick() {
//        Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    LogStub.init();
  }

  @Before
  public void clearFindings() {
    Log.getFindings().clear();
  }

  @Test
  public void testValid() throws IOException {
    String dir = "src/test/resources/fdvalid/";
    FeatureDiagramCoCos.checkAll(readFile(dir + "BasicElements.fd"));
    FeatureDiagramCoCos.checkAll(readFile(dir + "Car.fd"));
    FeatureDiagramCoCos.checkAll(readFile(dir + "GraphLibrary.fd"));
    FeatureDiagramCoCos.checkAll(readFile(dir + "Phone.fd"));
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testCTCFeatureDoesNotExist() throws IOException {
    testCoCo("CTCFeatureDoesNotExist.fd", "0xFD0006", "0xFD0006");
  }

  @Test
  public void testFeatureCycle() throws IOException {
    testCoCo("FeatureCycle.fd", "0xFD0008");
  }

  @Test
  public void testFeatureForest() throws IOException {
    testCoCo("FeatureForest.fd", "0xFD0010", "0xFD0010");
  }

  @Test
  public void testNonUniqueNameInGroup() throws IOException {
    testCoCo("NonUniqueNameInGroup.fd", "0xFD0009");
  }

  @Test
  public void testNoRoots() throws IOException {
    testCoCo("NoRoots.fd", "0xFD0002", "0xFD0010");
  }

  @Ignore //TODO: Is this coco necessary in general?
  @Test
  public void testSelfLoopInCTC() throws IOException {
    testCoCo("SelfLoopInCTC.fd", "FD0006");
  }

  @Test
  public void testSelfLoopInGroup() throws IOException {
    testCoCo("SelfLoopInGroup.fd", "0xFD0003");
  }

  @Test
  public void testTwoParents() throws IOException {
    testCoCo("TwoParents.fd", "0xFD0008");
  }

  @Test
  public void testInvalidParent() throws IOException {
    testCoCo("InvalidParent.fd", "0xFD0008");
  }

  @Test
  public void testTwoRoots() throws IOException {
    testCoCo("TwoRoots.fd", "0xFD0001");
  }

  protected void testCoCo(String modelName, String... errorCode) throws IOException {
    FeatureDiagramCoCos.checkAll(readFile("src/test/resources/fdinvalid/" + modelName));
    assertErrorCode(errorCode);
  }

  protected void assertErrorCode(String... errorCodes) {
    for (String errorCode : errorCodes) {
      assertErrorCode(errorCode);
    }
    for (Finding finding : Log.getFindings()) {
      if (finding.isError()) {
        fail("Found error '" + finding.getMsg() + "' that was not expected!");
      }
    }
  }

  protected void assertErrorCode(String errorCode) {
    for (Finding finding : Log.getFindings()) {
      if (finding.getMsg().startsWith(errorCode)) {
        //remove finding to enable finding the same error code multiple times
        Log.getFindings().remove(finding);
        return;
      }
    }
    fail("Expected to find an error with the code '" + errorCode + "', but it did not occur!");
  }

  protected ASTFDCompilationUnit readFile(String modelFile, ModelPath mp)
      throws IOException {
    ASTFDCompilationUnit ast = new FeatureDiagramParser().parse(modelFile).orElse(null);
    assertNotNull(ast);
    FeatureDiagramLanguage lang = new FeatureDiagramLanguage();
    FeatureDiagramGlobalScope globalScope = new FeatureDiagramGlobalScope(mp, lang);
    FeatureDiagramSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    symbolTable.createFromAST(ast);
    return ast;
  }

  protected ASTFDCompilationUnit readFile(String modelName)
      throws IOException {
    return readFile(modelName, new ModelPath());
  }

}
