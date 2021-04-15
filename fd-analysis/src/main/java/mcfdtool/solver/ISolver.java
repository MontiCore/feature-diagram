/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.solver;

import mcfdtool.transform.flatzinc.FlatZincModel;

import java.util.List;
import java.util.Map;

/**
 * This interface prescibes the functionality of a solver in terms of using it for realizing
 * analyses on feature diagrams and feature configurations
 */
public interface ISolver {

  boolean hasSolution(FlatZincModel model);

  Map<String, Integer> getAnySolution(FlatZincModel model);

  List<Map<String, Integer>> getAllSolutions(FlatZincModel model);

}
