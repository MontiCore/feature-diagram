/* (c) https://github.com/MontiCore/monticore */

package fddiff;

import com.google.common.collect.Sets;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class performs a semantic differencing between two feature diagrams.
 * The semantics of a feature diagrams are the set of all valid feature configurations.
 * The semantic difference between two feature diagrams, fd1 and fd2, are the feature configurations that are
 * valid in fd1, but are not valid in fd2.
 */
public class FDSemDiff {

  /**
   * Calculates the semantic difference witness between two feature diagrams.
   * This is done by transforming both feature diagrams into propositional formulas, phi_1 and phi_2,
   * and then checking if the formula phi_1 AND NOT phi_2 is satisfiable.
   * If so, a feature configuration, valid in fd1, but invalid in fd2, is returned.
   * Otherwise an empty optional is returned: fd1 is a refinement of fd2,
   * which means that every feature configuration valid in fd1 is also valid in fd2
   *
   * @param fd1 The first feature diagram
   * @param fd2 The second feature diagram
   * @return The (optional) semantic diff witness
   */
  public Optional<FDSemDiffWitness> semDiff(ASTFeatureDiagram fd1, ASTFeatureDiagram fd2) {
    final FormulaFactory ff = new FormulaFactory();
    final Set<String> features = Sets.union(Sets.newHashSet(fd1.getAllFeatures()), Sets.newHashSet(fd1.getAllFeatures()));
    final Map<Variable, String> vars = features.stream().collect(Collectors.toMap(
            ff::variable, Function.identity()
    ));

    FD2Formula trafo = new FD2Formula(ff);

    Formula phi_1 = trafo.getFormula(fd1);
    Formula phi_2 = trafo.getFormula(fd2);
    Formula phi = ff.and(phi_1, ff.not(phi_2));

    final SATSolver miniSat = MiniSat.miniSat(ff);
    miniSat.add(phi);
    miniSat.sat();
    final Assignment assignment = miniSat.model();

    Optional<FDSemDiffWitness> result = Optional.empty();
    if (assignment != null) {
      Set<String> selectedFeatures = assignment.positiveLiterals().stream().map(vars::get).filter(Objects::nonNull).collect(Collectors.toSet());
      result = Optional.of(new FDSemDiffWitness(selectedFeatures));
    }

    return result;
  }

}
