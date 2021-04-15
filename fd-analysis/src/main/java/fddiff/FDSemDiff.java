/* (c) https://github.com/MontiCore/monticore */

package fddiff;

import com.google.common.collect.Sets;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatureConfigurationBuilder;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._ast.ASTSelectBuilder;
import de.monticore.featureconfigurationpartial._ast.ASTUnselectBuilder;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
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
   * This method uses the open-world semantics of FDs as presented here:
   * https://se-rwth.de/publications/Semantic-Evolution-Analysis-of-Feature-Models.pdf
   *
   * @param fd1 The first feature diagram
   * @param fd2 The second feature diagram
   * @return The (optional) semantic diff witness
   */
  public Optional<ASTFeatureConfiguration> semDiffOpenWorld(ASTFeatureDiagram fd1, ASTFeatureDiagram fd2) {
    return semDiff(fd1, fd2, false);
  }

  /**
   * Calculates the semantic difference witness between two feature diagrams.
   * This is done by transforming both feature diagrams into propositional formulas, phi_1 and phi_2,
   * and then checking if the formula phi_1 AND NOT phi_2 is satisfiable.
   * If so, a feature configuration, valid in fd1, but invalid in fd2, is returned.
   * Otherwise an empty optional is returned: fd1 is a refinement of fd2,
   * which means that every product of fd1 is also a product of fd2
   *
   * This method uses the closed-world semantics of FDs as presented here:
   * https://se-rwth.de/publications/Semantic-Evolution-Analysis-of-Feature-Models.pdf
   *
   * @param fd1 The first feature diagram
   * @param fd2 The second feature diagram
   * @return The (optional) semantic diff witness
   */
  public Optional<ASTFeatureConfiguration> semDiffClosedWorld(ASTFeatureDiagram fd1, ASTFeatureDiagram fd2) {
    return semDiff(fd1, fd2, true);
  }

  /**
   * Calculates the semantic difference witness between two feature diagrams.
   *
   * Either uses open-world or close-world semantics of FDs as presented here:
   * https://se-rwth.de/publications/Semantic-Evolution-Analysis-of-Feature-Models.pdf
   *
   * @param fd1 The first feature diagram
   * @param fd2 The second feature diagram
   * @param closedWorld if true, then closed-world semantics is used, otherwise open-world semantics is used
   * @return The (optional) semantic diff witness
   */
  private Optional<ASTFeatureConfiguration> semDiff(ASTFeatureDiagram fd1, ASTFeatureDiagram fd2, boolean closedWorld) {
    final FormulaFactory ff = new FormulaFactory();
    final Set<String> features = Sets.union(Sets.newHashSet(fd1.getAllFeatures()), Sets.newHashSet(fd2.getAllFeatures()));
    final Map<Variable, String> vars = features.stream().collect(Collectors.toMap(
      ff::variable, Function.identity()
    ));

    FeatureDiagramTraverser traverser = FeatureDiagramMill.traverser();
    FD2Formula trafo = new FD2Formula(ff);
    traverser.add4FeatureDiagram(trafo);
    // traverser.setFeatureDiagramHandler(trafo);
    // trafo.setTraverser(traverser);

    fd1.accept(traverser);
    Formula phi_1 = trafo.getFormula();
    fd2.accept(traverser);
    Formula phi_2 = trafo.getFormula();

    if(closedWorld) {
      // features of FD1 that are not features of FD2 must not be selected in FD2
      Set<String> featuresInFD1NotInFD2 = new HashSet<>(fd1.getAllFeatures());
      featuresInFD1NotInFD2.removeAll(fd2.getAllFeatures());
      for (String feature : featuresInFD1NotInFD2) {
        // This feature must not be chosen for FD2
        Formula notFeature = ff.literal(feature, false);
        phi_2 = ff.and(phi_2, notFeature);
      }

      // features of FD2 that are not features of FD1 must not be selected in FD1
      Set<String> featuresInFD2NotInFD1 = new HashSet<>(fd2.getAllFeatures());
      featuresInFD2NotInFD1.removeAll(fd1.getAllFeatures());
      for (String feature : featuresInFD2NotInFD1) {
        // This feature must not be chosen for fd1
        Formula notFeature = ff.literal(feature, false);
        phi_1 = ff.and(phi_1, notFeature);
      }
    }

    Formula phi = ff.and(phi_1, ff.not(phi_2));

    final SATSolver miniSat = MiniSat.miniSat(ff);
    miniSat.add(phi);
    miniSat.sat();
    final Assignment assignment = miniSat.model();

    Optional<ASTFeatureConfiguration> result = Optional.empty();
    if (assignment != null) {
      Set<String> selectedFeatures = assignment.positiveLiterals().stream().map(vars::get).filter(Objects::nonNull).collect(Collectors.toSet());
      return Optional.of(calculateConfiguration(selectedFeatures, new HashSet<>(fd1.getAllFeatures()), fd1.getName()));
    }

    return result;
  }

  private ASTFeatureConfiguration calculateConfiguration(Set<String> selectedFeatures, Set<String> allFeatures, String fdName) {
    ASTFeatureConfigurationBuilder builder = FeatureConfigurationPartialMill
      .featureConfigurationBuilder();
    ASTSelectBuilder selectBuilder = FeatureConfigurationPartialMill.selectBuilder();
    ASTUnselectBuilder unselectBuilder = FeatureConfigurationPartialMill.unselectBuilder();
    for (String feature : selectedFeatures) {
          selectBuilder.addName(feature);
    }
    HashSet<String> unselectedFeatures = new HashSet<>(allFeatures);
    unselectedFeatures.removeAll(selectedFeatures);
    for(String feature : unselectedFeatures) {
      unselectBuilder.addName(feature);
    }
    return builder
      .setName("witness")
      .setFdName(fdName)
      .addFCElement(selectBuilder.build())
      .addFCElement(unselectBuilder.build())
      .build();
  }

}
