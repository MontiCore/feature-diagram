/* (c) https://github.com/MontiCore/monticore */

package fddiff;

import com.google.common.collect.Sets;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.featurediagram._ast.*;
import de.monticore.featurediagram._visitor.FeatureDiagramInheritanceVisitor;
import de.se_rwth.commons.logging.Log;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transforms a feature diagram into a propositional formula.
 * The following formulas are created and then combined by conjunction:
 *   * parent clause (if a child feature is selected, then the parent must also be selected)
 *   * and group clause (if a feature with an and group is selected, all subfeatures of that group must also be selected)
 *   * or group clause (if a feature with an or group is selected, at least one subfeature of that group must also be selected)
 *   * xor group clause (if a feature with a xor group is selected, exactly one subfeature of that group must also be selected)
 *   * requires clause (if a left-sided feature of a requires clause is selected, then the right-sided feature must also be selected)
 *   * excludes clause (both, the left- and right-sided feature, cannot be selected at the same time)
 */
class FD2Formula implements FeatureDiagramInheritanceVisitor {

  private final FormulaFactory ff;

  private List<Formula> formulas;

  private String current;

  FD2Formula(FormulaFactory ff) {
    this.ff = ff;
  }

  Formula getFormula(ASTFeatureDiagram fd) {
    formulas = new ArrayList<>();
    fd.accept(this);
    return ff.and(formulas);
  }

  private Literal Var(String feature) {
    return ff.literal(feature, true);
  }

  private Set<String> getFeatures(ASTFeatureGroup ast) {
    return ast.getGroupPartList()
            .stream()
            .map(ASTGroupPart::getName)
            .collect(Collectors.toSet());
  }

  @Override
  public void visit(ASTFeatureDiagram node) {
    formulas.add(Var(node.getRootFeature()));
  }

  @Override
  public void visit(ASTFeatureTreeRule node) {
    current = node.getName();
    for (ASTGroupPart groupPart : node.getFeatureGroup().getGroupPartList()) {
      formulas.add(ff.implication(Var(groupPart.getName()), Var(current)));
    }
  }

  @Override
  public void visit(ASTAndGroup node) {
    Set<String> andGroup = node.getGroupPartList()
            .stream()
            .filter(g -> !g.isOptional())
            .map(ASTGroupPart::getName)
            .collect(Collectors.toSet());
    for (String subFeature : andGroup) {
      formulas.add(ff.implication(Var(current), Var(subFeature)));
    }
  }

  @Override
  public void visit(ASTOrGroup node) {
    Set<String> orGroup = getFeatures(node);
    Formula r = ff.or(orGroup.stream().map(this::Var).collect(Collectors.toList()));
    formulas.add(ff.implication(Var(current), r));
  }

  @Override
  public void visit(ASTXorGroup node) {
    Set<String> xorGroup = getFeatures(node);
    Set<Literal> G = xorGroup.stream().map(this::Var).collect(Collectors.toSet());
    Formula min1 = ff.or(G);
    Formula max1 = ff.and(Sets.combinations(G, 2).stream().map(ff::and).map(ff::not).collect(Collectors.toList()));
    formulas.add(ff.implication(Var(current), ff.and(min1, max1)));
  }

  @Override
  public void visit(ASTRequires node) {
    if (isNameExpression(node.getLeft()) && isNameExpression(node.getRight())) {
      String left = ((ASTNameExpression) node.getLeft()).getName();
      String right = ((ASTNameExpression) node.getRight()).getName();
      formulas.add(ff.implication(Var(left), Var(right)));
    } else {
      Log.error("Expecting a single name expression on the left and right side of a requires statement.", node.get_SourcePositionStart());
    }
  }

  @Override
  public void visit(ASTExcludes node) {
    if (isNameExpression(node.getLeft()) && isNameExpression(node.getRight())) {
      String left = ((ASTNameExpression) node.getLeft()).getName();
      String right = ((ASTNameExpression) node.getRight()).getName();
      formulas.add(ff.not(ff.and(Var(left), Var(right))));
    } else {
      Log.error("Expecting a single name expression on the left and right side of an excludes statement.", node.get_SourcePositionStart());
    }
  }

  private boolean isNameExpression(ASTExpression expr) {
    return expr instanceof ASTNameExpression;
  }
}
