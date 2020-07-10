/* (c) https://github.com/MontiCore/monticore */
package fd;

import de.monticore.featurediagram.prettyprint.FeatureDiagramPrettyPrinter;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FeatureDiagramPrettyPrinterTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false); // Uncomment this to support finding reasons for failing tests
    //    LogStub.init();
  }

  @Before
  public void clearFindings() {
    Log.getFindings().clear();
  }

  protected String prettyprint(String modelFile) {
    try {
      ASTFDCompilationUnit ast = new FeatureDiagramParser()
          .parse("src/test/resources/" + modelFile)
          .orElse(null);
      return FeatureDiagramPrettyPrinter.print(ast);
    }
    catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    return "";
  }

  protected void testRoundtripPrint(String modelFile) {
    Path path = Paths.get("src/test/resources/", modelFile);
    String fileContent = FileReaderWriter.readFromFile(path).replace("\r\n", "\n");
    assertEquals(fileContent, prettyprint(modelFile));
  }

  @Test
  public void test() {
    testRoundtripPrint("fdvalid/BasicElements.fd");
    testRoundtripPrint("fdvalid/PhoneComplex.fd");
    testRoundtripPrint("fdvalid/CarNavigation.fd");
  }

}