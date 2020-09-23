package fddiff;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FDSemDiff {

  private final FormulaFactory ff = new FormulaFactory();

  public Optional<FDSemDiffWitness> semDiff(FeatureDiagram fd1, FeatureDiagram fd2) {

    final Set<Feature> features = Sets.union(fd1.getFeatures(), fd2.getFeatures());
    final Map<Variable, Feature> vars = features.stream().collect(Collectors.toMap(
      f -> ff.variable(f.getName()), Function.identity()
    ));

    Formula phi_1 = getPhi(fd1);
    Formula phi_2 = getPhi(fd2);
    Formula phi = ff.and(phi_1, ff.not(phi_2));

    final SATSolver miniSat = MiniSat.miniSat(ff);
    miniSat.add(phi);
    miniSat.sat();
    final Assignment assignment = miniSat.model();

    Optional<FDSemDiffWitness> result = Optional.empty();
    if (assignment != null) {
      Set<Feature> selectedFeatures = assignment.positiveLiterals().stream().map(vars::get).filter(Objects::nonNull).collect(Collectors.toSet());
      result = Optional.of(new FDSemDiffWitness(selectedFeatures));
    }

    return result;
  }

  private Literal Var(Feature feature) {
    return ff.literal(feature.getName(), true);
  }

  private Formula getPhi(FeatureDiagram fd) {
    return ff.and(
      Lists.newArrayList(rootClause(fd), parentClauses(fd), mandatoryClauses(fd),
        orClauses(fd), xorClauses(fd), impliesClauses(fd), excludesClauses(fd))
           .stream().flatMap(List::stream).collect(Collectors.toList()));
  }

  private List<Formula> rootClause(FeatureDiagram fd) {
    return Collections.singletonList(ff.literal(fd.getRoot().getName(), true));
  }

  private List<Formula> parentClauses(FeatureDiagram fd) {
    List<Formula> clauses = new ArrayList<>();
    for (Feature f : fd.getFeatures()) {
      if (f.getParent() != null) {
        clauses.add(ff.implication(Var(f), Var(f.getParent())));
      }
    }
    return clauses;
  }

  private List<Formula> mandatoryClauses(FeatureDiagram fd) {
    List<Formula> clauses = new ArrayList<>();
    for (Map.Entry<Feature, Set<Feature>> entry : fd.getMandatory().entrySet()) {
      Feature f = entry.getKey();
      for (Feature g : entry.getValue()) {
        clauses.add(ff.implication(Var(f), Var(g)));
      }
    }
    return clauses;
  }

  private List<Formula> orClauses(FeatureDiagram fd) {
    List<Formula> clauses = new ArrayList<>();
    for (Map.Entry<Feature, Set<Feature>> entry : fd.getOr().entrySet()) {
      Feature p = entry.getKey();
      Formula r = ff.or(entry.getValue().stream().map(this::Var).collect(Collectors.toList()));
      clauses.add(ff.implication(Var(p), r));
    }
    return clauses;
  }

  private List<Formula> xorClauses(FeatureDiagram fd) {
    List<Formula> clauses = new ArrayList<>();
    for (Map.Entry<Feature, Set<Feature>> entry : fd.getXor().entrySet()) {
      Feature p = entry.getKey();
      Set<Literal> G = entry.getValue().stream().map(this::Var).collect(Collectors.toSet());
      Formula min1 = ff.or(G);
      Formula max1 = ff.and(Sets.combinations(G, 2).stream().map(ff::and).map(ff::not).collect(Collectors.toList()));
      clauses.add(ff.implication(Var(p), ff.and(min1, max1)));
    }
    return clauses;
  }

  private List<Formula> impliesClauses(FeatureDiagram fd) {
    List<Formula> clauses = new ArrayList<>();
    for (Map.Entry<Feature, Set<Feature>> entry : fd.getImplies().entrySet()) {
      Feature f = entry.getKey();
      for (Feature g : entry.getValue()) {
        clauses.add(ff.implication(Var(f), Var(g)));
      }
    }
    return clauses;
  }

  private List<Formula> excludesClauses(FeatureDiagram fd) {
    List<Formula> clauses = new ArrayList<>();
    for (Map.Entry<Feature, Set<Feature>> entry : fd.getExcludes().entrySet()) {
      Feature f = entry.getKey();
      for (Feature g : entry.getValue()) {
        clauses.add(ff.not(ff.and(Var(f), Var(g))));
      }
    }
    return clauses;
  }

}
