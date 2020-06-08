/* (c) https://github.com/MontiCore/monticore */
package fd;

import cdfeaturediagram.CDType2FeatureAdapter;
import cdfeaturediagram.CDTypeFeatureDiagramTool;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import featurediagram._symboltable.FeatureDiagramArtifactScope;
import featurediagram.FeatureDiagramMill;
import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._symboltable.FeatureSymbol;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CD4AFDSymbolTableTest {

  @BeforeClass
  public static void disableFailQuick() {
    //    Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    LogStub.init();
  }

  @AfterClass
  public static void resetMill() {
    //Important: reset Mill, because otherwise all "normal feature diagrams" cannot be processed
    FeatureDiagramMill.reset();
  }

  @Before
  public void clearFindings() {
    Log.getFindings().clear();
  }

  @Test
  public void test() throws IOException {
    String model = "src/test/resources/cd4a-fd/FooBar.fd";
    FeatureDiagramArtifactScope scope = CDTypeFeatureDiagramTool
        .run(model, "src/test/resources/cd4a-fd");

    assertTrue(null != scope);
    FeatureDiagramSymbol fd = scope.resolveFeatureDiagram("FooBar").orElse(null);
    assertTrue(null != fd);

    List<FeatureSymbol> foos = fd.getSpannedScope().resolveFeatureMany("Foo");
    assertEquals(1, foos.size());
    FeatureSymbol fooSymbol = foos.get(0);
    assertTrue(fooSymbol instanceof CDType2FeatureAdapter);
    assertEquals("Foo", fooSymbol.getName());
  }

}
