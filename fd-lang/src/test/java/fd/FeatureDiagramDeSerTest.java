/* (c) https://github.com/MontiCore/monticore */
package fd;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import test.AbstractTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FeatureDiagramDeSerTest extends AbstractTest {

  protected FeatureDiagramArtifactScope setupSymbolTable(String modelFile, ModelPath mp)
      throws IOException {
    ASTFDCompilationUnit ast = new FeatureDiagramParser().parse(modelFile).orElse(null);
    assertNotNull(ast);
    FeatureDiagramGlobalScope globalScope = FeatureDiagramMill
        .featureDiagramGlobalScopeBuilder()
        .setModelFileExtension("fd")
        .setModelPath(mp)
        .build();
    FeatureDiagramSymbolTableCreatorDelegator symbolTable = FeatureDiagramMill
        .featureDiagramSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();
    return symbolTable.createFromAST(ast);
  }

  protected FeatureDiagramArtifactScope setupSymbolTable(String modelFile)
      throws IOException {
    return setupSymbolTable(modelFile, new ModelPath());
  }

  @Ignore //solange DeSer WIP ist
  @Test
  public void testSerializeDeserialize() throws IOException {
    String model = "src/test/resources/fdvalid/BasicElements.fd";
    FeatureDiagramArtifactScope scope = setupSymbolTable(model);
    assertTrue(null != scope);
    String serialized = new FeatureDiagramScopeDeSer().serialize(scope);
    System.out.println(serialized);
    assertTrue(null != serialized);

    FeatureDiagramGlobalScope gs = FeatureDiagramMill
        .featureDiagramGlobalScopeBuilder()
        .setModelFileExtension("fd")
        .setModelPath(new ModelPath())
        .build();
    IFeatureDiagramScope deserializedScope = new FeatureDiagramScopeDeSer()
        .deserialize(serialized);
    assertTrue(deserializedScope instanceof FeatureDiagramArtifactScope);
    FeatureDiagramArtifactScope deserialized = (FeatureDiagramArtifactScope) deserializedScope;

    assertEquals(scope.getName(), deserialized.getName());
    assertEquals(scope.getPackageName(), deserialized.getPackageName());
    assertEquals(scope.getImportList().size(), deserialized.getImportList().size());
    assertEquals(scope.getTopLevelSymbol().isPresent(),
        deserialized.getTopLevelSymbol().isPresent());
    assertEquals(scope.getTopLevelSymbol().get().getName(),
        deserialized.getTopLevelSymbol().get().getName());
    assertEquals(scope.getLocalFeatureDiagramSymbols().size(),
        deserialized.getLocalFeatureDiagramSymbols().size());
    assertEquals(scope.getLocalFeatureSymbols().size(),
        deserialized.getLocalFeatureSymbols().size());

    FeatureDiagramSymbol expectedSymbol = scope.getLocalFeatureDiagramSymbols().get(0);
    FeatureDiagramSymbol actualSymbol = deserialized.getLocalFeatureDiagramSymbols().get(0);
    assertEquals(expectedSymbol.getName(), actualSymbol.getName());
    assertEquals(expectedSymbol.getSpannedScope().getName(),
        actualSymbol.getSpannedScope().getName());
    assertEquals(expectedSymbol.getSpannedScope().getLocalFeatureSymbols().size(),
        actualSymbol.getSpannedScope().getLocalFeatureSymbols().size());
    assertEquals(expectedSymbol.getSpannedScope().getSubScopes().size(),
        actualSymbol.getSpannedScope().getSubScopes().size());
    assertEquals(expectedSymbol.getSpannedScope().getLocalFeatureDiagramSymbols().size(),
        actualSymbol.getSpannedScope().getLocalFeatureDiagramSymbols().size());
    assertEquals(expectedSymbol.getSpannedScope().getSpanningSymbol().getName(),
        actualSymbol.getSpannedScope().getSpanningSymbol().getName());
  }

  @Test
  public void testDeSer() throws IOException {
    FeatureDiagramArtifactScope fdScope = setupSymbolTable(
        "src/test/resources/fdvalid/CarNavigation.fd");
    new FeatureDiagramScopeDeSer().store(fdScope, Paths.get("target/test-symbols"));
    assertTrue(new File("target/test-symbols/CarNavigation.fdsym").exists());
  }

}
