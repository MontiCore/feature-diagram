/* (c) https://github.com/MontiCore/monticore */

package mcfdtool.solver;

import mcfdtool.transform.flatzinc.FlatZincModel;
import org.apache.commons.io.IOUtils;
import org.chocosolver.parser.flatzinc.Flatzinc;
import org.chocosolver.parser.flatzinc.FznSettings;
import org.chocosolver.parser.flatzinc.ast.Datas;
import org.chocosolver.solver.Model;

/**
 * This class wraps The Choco solver and thus simplifies its use for the MontiCore FDL.
 */
public class ChocoWrapper extends Flatzinc {

  /**
   * Translate a passed FlatZinc model into the Choco representation
   *
   * @param model
   * @return
   */
  public Model parse(FlatZincModel model) {
    Model fznModel = new Model();
    fznModel.set(new FznSettings());
    this.parse(fznModel, new Datas(), IOUtils.toInputStream(model.print()));
    return fznModel;
  }

  /**
   * Set the action if solving is interrupted for Choco. Without this, lots of unnecessary
   * exceptions tend to be printed to the console
   *
   * @return
   */
  @Override
  public Thread actionOnKill() {
    return new Thread();
  }
}
