/* (c) https://github.com/MontiCore/monticore */
package test.fd;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.io.FileReaderWriter;
import org.junit.Test;
import org.junit.Before;
import test.AbstractLangTest;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FeatureDiagramPrettyPrinterTest extends AbstractLangTest {

  @Before
  public void initMills() {
    FeatureDiagramMill.init();
  }

  @Test
  public void testValidFDs() throws IOException{
    for (File validfd : Objects.requireNonNull(new File("src/test/resources/fdvalid/").listFiles((file, s) -> s.endsWith(".fd")))) {
      testRoundtripPrint(validfd);
    }
  }

  protected void testRoundtripPrint(File f) throws IOException {
    String fileContent = FileReaderWriter.readFromFile(f.toPath()).replace("\r\n", "\n");
    Optional<ASTFDCompilationUnit> astOpt = FeatureDiagramMill.parser().parse(f.getAbsolutePath());
    if (astOpt.isEmpty()) {
      fail("Failed to parse " + f.getName());
    }
    String pretty = FeatureDiagramMill.prettyPrint(astOpt.get(), true);
    Optional<ASTFDCompilationUnit> prettyAstOpt = FeatureDiagramMill.parser().parse(f.getAbsolutePath());
    if (prettyAstOpt.isEmpty()) {
      assertEquals("Failed to parse pretty printed: " + f.getName(), fileContent, pretty); // throw equals exc to see the comparison
      fail("Failed to parse pretty printed"); // fail just in case
    }
    if (!prettyAstOpt.get().deepEquals(astOpt.get())) {
      assertEquals("Failed to deep equals: " + f.getName(), fileContent, pretty); // throw equals exc to see the comparison
      fail("Failed to deep equals"); // fail just in case
    }
  }


}