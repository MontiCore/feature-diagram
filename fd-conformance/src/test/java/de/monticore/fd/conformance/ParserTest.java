/* (c) https://github.com/MontiCore/monticore */

package de.monticore.fd.conformance;

import de.monticore.fd.conformance.loader.FDLoader;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ParserTest extends FDAbstractTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Burger.fd",
        "Car.fd",
        "Computer.fd",
        "ConBurger.fd",
        "UnValidBurger.fd",
        "MusicAccount.fd",
      })
  public void ParseTest(String name) {
    ASTFDCompilationUnit fd = FDLoader.loadAndCheckFD(RELATIVE_MODEL_PATH + "parser/" + name);
    Assertions.assertNotNull(fd);
  }
}
