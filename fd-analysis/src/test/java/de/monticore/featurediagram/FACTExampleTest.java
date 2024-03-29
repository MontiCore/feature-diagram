/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import mcfdtool.FACT;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * !If this test does not compile anymore or produces errors,
 *  the Readme.md has to be adjusted accordingly!
 */
public class FACTExampleTest extends AbstractTest {

  @Test
  public void test(){
    factExampleInReadme();
    assertEquals(0, Log.getErrorCount());
  }

  public void factExampleInReadme(){
    FACT tool = new FACT();
    MCPath mp = new MCPath();
    mp.addEntry(Paths.get("target"));

    ASTFeatureDiagram fd = tool.readFeatureDiagram("src/test/resources/fdvalid/CarNavigation.fd", "target", mp);
    ASTFeatureConfiguration fc = tool.readFeatureConfiguration("src/test/resources/Basic.fc", mp);
    boolean result = tool.execIsValid(fd, fc);

    if(result){
      System.out.println("Is valid!");
    }
    else{
      System.out.println("Is invalid!");
    }
  }

}
