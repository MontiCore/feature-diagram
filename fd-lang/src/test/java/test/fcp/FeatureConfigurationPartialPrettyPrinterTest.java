/* (c) https://github.com/MontiCore/monticore */
package test.fcp;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.io.FileReaderWriter;
import org.junit.Before;
import org.junit.Test;
import test.AbstractLangTest;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FeatureConfigurationPartialPrettyPrinterTest extends AbstractLangTest {

  @Before
  public void initMills() {
    FeatureConfigurationPartialMill.init();
  }

  @Test
  public void testValidPFC() throws IOException{
    for (File validfd : Objects.requireNonNull(new File("src/test/resources/pfcvalid/").listFiles((file, s) -> s.endsWith(".fd")))) {
      testRoundtripPrint(validfd);
    }
  }

  protected void testRoundtripPrint(File f) throws IOException {
    String fileContent = FileReaderWriter.readFromFile(f.toPath()).replace("\r\n", "\n");
    Optional<ASTFCCompilationUnit> astOpt = FeatureConfigurationPartialMill.parser().parse(f.getAbsolutePath());
    if (astOpt.isEmpty()) {
      fail("Failed to parse " + f.getName());
    }
    String pretty = FeatureConfigurationPartialMill.prettyPrint(astOpt.get(), true);
    Optional<ASTFCCompilationUnit> prettyAstOpt = FeatureConfigurationPartialMill.parser().parse(f.getAbsolutePath());
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