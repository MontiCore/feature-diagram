/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.solver;

import de.se_rwth.commons.logging.Log;
import mcfdtool.transform.flatzinc.FlatZincModel;
import org.apache.commons.io.IOUtils;
import org.chocosolver.parser.flatzinc.Flatzinc;
import org.chocosolver.parser.flatzinc.FznSettings;
import org.chocosolver.parser.flatzinc.ast.Datas;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.*;

/**
 * This class implements the interface ISolver of the MontiCore FDL
 * and uses the Choco Solver to realize the solving of flatzinc models.
 * The result of solving is a configuration of valuations of the variables
 * of the flat zinc model.
 */
public class ChocoSolver implements ISolver {

  protected ChocoWrapper choco = new ChocoWrapper();

  @Override
  public boolean hasSolution(FlatZincModel model) {
    Model fzn = choco.parse(model);
    return fzn.getSolver().solve();
  }

  @Override
  public Map<String, Integer> getAnySolution(FlatZincModel model) {
    Model fzn = choco.parse(model);
    if (fzn.getSolver().solve()) {
      return getSolverResult(fzn);
    }
    return null;
  }

  @Override
  public List<Map<String, Integer>> getAllSolutions(FlatZincModel model) {
    Set<Map<String, Integer>> results = new HashSet<>(); //Set to filter out duplicates
    Model fzn = choco.parse(model);
    while (fzn.getSolver().solve()) {
      results.add(getSolverResult(fzn));
    }
    return new ArrayList<>(results);
  }

  /**
   * Collects the result after a solver on the passed model has been executed.
   * The result is returned in form of a map from variable name to (integer) variable valuation
   *
   * @param model
   * @return
   */
  protected Map<String, Integer> getSolverResult(Model model) {
    Map<String, Integer> result = new HashMap<>();
    for (Variable var : model.getVars()) {
      boolean isInt = (var.getTypeAndKind() & Variable.INT) != 0;
      if (isInt) {
        result.put(var.getName(), ((IntVar) var).getValue());
      }
    }
    return result;
  }

}