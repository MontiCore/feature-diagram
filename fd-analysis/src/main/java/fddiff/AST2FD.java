package fddiff;

import com.google.common.collect.Sets;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.featurediagram._ast.*;
import de.monticore.featurediagram._visitor.FeatureDiagramInheritanceVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AST2FD implements FeatureDiagramInheritanceVisitor {

  private Map<String, Feature> features;

  private Feature current;

  private Map<Feature, Set<Feature>> mandatory;

  private Map<Feature, Set<Feature>> or;

  private Map<Feature, Set<Feature>> xor;

  private Map<Feature, Set<Feature>> implies;

  private Map<Feature, Set<Feature>> excludes;

  public FeatureDiagram transform(ASTFeatureDiagram ast) {
    features = ast.getAllFeatures()
                  .stream()
                  .map(Feature::new)
                  .collect(Collectors.toMap(Feature::getName, Function.identity()));
    Feature root = features.get(ast.getRootFeature());
    mandatory = new HashMap<>();
    or = new HashMap<>();
    xor = new HashMap<>();
    implies = new HashMap<>();
    excludes = new HashMap<>();
    ast.accept(this);
    return FeatureDiagram.builder()
                         .features(Sets.newHashSet(features.values()))
                         .root(root)
                         .mandatory(mandatory)
                         .or(or)
                         .xor(xor)
                         .implies(implies)
                         .excludes(excludes)
                         .build();
  }

  @Override
  public void visit(ASTFeatureTreeRule node) {
    current = features.get(node.getName());
  }

  @Override
  public void visit(ASTAndGroup node) {
    Set<Feature> andGroup = node.getGroupPartList()
                                .stream()
                                .filter(g -> !g.isOptional())
                                .map(ASTGroupPart::getName)
                                .map(features::get)
                                .collect(Collectors.toSet());
    setParent(current, getFeatures(node));
    mandatory.put(current, andGroup);
  }

  @Override
  public void visit(ASTOrGroup node) {
    Set<Feature> orGroup = getFeatures(node);
    setParent(current, orGroup);
    or.put(current, orGroup);
  }

  @Override
  public void visit(ASTXorGroup node) {
    Set<Feature> xorGroup = getFeatures(node);
    setParent(current, xorGroup);
    xor.put(current, xorGroup);
  }

  @Override
  public void visit(ASTRequires node) {
    if (node.getLeft() instanceof ASTNameExpression && node.getRight() instanceof  ASTNameExpression) {
      Feature left = features.get(((ASTNameExpression) node.getLeft()).getName());
      Feature right = features.get(((ASTNameExpression) node.getRight()).getName());
      if (!implies.containsKey(left)) {
        implies.put(left, new HashSet<>());
      }
      implies.get(left).add(right);
    }
  }

  @Override
  public void visit(ASTExcludes node) {
    if (node.getLeft() instanceof ASTNameExpression && node.getRight() instanceof  ASTNameExpression) {
      Feature left = features.get(((ASTNameExpression) node.getLeft()).getName());
      Feature right = features.get(((ASTNameExpression) node.getRight()).getName());
      if (!excludes.containsKey(left)) {
        excludes.put(left, new HashSet<>());
      }
      excludes.get(left).add(right);
    }
  }

  private Set<Feature> getFeatures(ASTFeatureGroup ast) {
    return ast.getGroupPartList()
              .stream()
              .map(ASTGroupPart::getName)
              .map(features::get)
              .collect(Collectors.toSet());
  }

  private void setParent(Feature parent, Set<Feature> children) {
    children.forEach(f -> f.setParent(parent));
  }
}
