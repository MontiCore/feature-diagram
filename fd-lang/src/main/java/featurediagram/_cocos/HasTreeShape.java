/* (c) https://github.com/MontiCore/monticore */
package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTFeatureDiagram;
import featurediagram._ast.ASTFeatureTreeRule;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureNamesCollector;

import java.util.*;
import java.util.stream.Collectors;

public class HasTreeShape
        implements FeatureDiagramASTFeatureDiagramCoCo, FeatureDiagramASTFeatureTreeRuleCoCo {


  protected FeatureNamesCollector collector = new FeatureNamesCollector();

  @Override
  public void check(ASTFeatureDiagram node) {
    checkParents(node);
  }

  private String calculateRootFeature(ASTFeatureDiagram node){
    node.accept(collector);
    List<String> rootfeatures = collector.getOccurrences(FeatureNamesCollector.Occurrence.LEFT);
    if(rootfeatures.size() == 0){
      Log.error("0xFD003 Featurediagram" + node.getName() +
          "has no root node.");
      return "";
    }
    if (rootfeatures.size() > 1) {
      Log.error("0xFD001 Featurediagram" + node.getName() +
          "has multiple root nodes.");
      return "";
    }
    return rootfeatures.get(0);
  }


  protected void checkParents(ASTFeatureDiagram node) {
    if (!node.isPresentSymbol() || null == node.getSymbol()) {
      Log.error("0xFD005 Feature diagram symbol table has to be created before cocos are checked!",
              node.get_SourcePositionStart());
    }

    //get all feature names used in the feature diagram
    Set<String> features = getAllFeatureNames(node);

    //from these, remove all features that have a parent feature
    Map<String, String> parents = getAllParents(node);
    features.removeAll(parents.keySet());

    //remove the root feature, which must not have a parent
    String rootFeature =  calculateRootFeature(node);
    features.remove(rootFeature);

    for (String feature : features) {
      Log.error("0xFD004 Each feature except the root feature must have a parent feature! '"
                      + feature + "' does not have a parent feature",
              node.get_SourcePositionStart());
    }
  }

  @Override
  public void check(ASTFeatureTreeRule node) {
    String lhs = node.getName();
    for (FeatureSymbol f : node.getFeatureGroup().getSubFeatureSymbols()) {
      if (f.getName().equals(lhs)) {
        Log.error("0xFD007 Feature diagram rules must not introduce self loops!",
                node.get_SourcePositionStart());
      }
    }
  }

  protected Set<String> getAllFeatureNames(ASTFeatureDiagram node) {
    return node.getSymbol()
            .getAllFeatures()
            .stream().map(f -> f.getName())
            .collect(Collectors.toSet());
  }

  /**
   * Creates a map that maps all feature names of the passed feature diagram to
   * their parent feature's name. Throws an error if a feature has more than one parent.
   *
   * @param node
   * @return
   */
  protected Map<String, String> getAllParents(ASTFeatureDiagram node) {
    Map<String, String> parents = new HashMap<>();
    List<ASTFeatureTreeRule> featureTreeRules = node.getFDElementList().stream()
            .filter(e -> e instanceof ASTFeatureTreeRule)
            .map(e -> (ASTFeatureTreeRule) e)
            .collect(Collectors.toList());

    for (ASTFeatureTreeRule rule : featureTreeRules) {
      String parentName = rule.getName();
      for (FeatureSymbol f : rule.getFeatureGroup().getSubFeatureSymbols()) {
        String featureName = f.getName();
        if (parents.containsKey(featureName)) {
          //get the parent name that was found
          String parentName2 = parents.get(featureName);
          if (!parentName.equals(parentName2)) {
            Log.error("0xFD008 A feature must not have more than one parent feature! '"
                            + featureName + "' has parents '" + parentName + "' and '" + parentName2 + "'",
                    node.get_SourcePositionStart());
          }
        } else {
          if (node.getSpannedScope().resolveFeatureMany(parentName).isEmpty()) {
            Log.error("0xFD010 The feature '" + featureName + "' has the unknown parent feature '"
                            + parentName + "'",
                    node.get_SourcePositionStart());
          }
          parents.put(featureName, parentName);
        }
      }
    }
    return parents;
  }
}
