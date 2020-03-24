/* (c) https://github.com/MontiCore/monticore */
package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.*;

import java.util.*;
import java.util.stream.Collectors;

public class HasTreeShape
    implements FeatureDiagramASTFeatureDiagramCoCo, FeatureDiagramASTFeatureTreeRuleCoCo {

  @Override public void check(ASTFeatureDiagram node) {
    checkSingleRootFeature(node);
    checkParents(node);
  }

  protected void checkSingleRootFeature(ASTFeatureDiagram node) {
    Optional<ASTRootFeature> root = Optional.empty();
    for (ASTFDElement astfdElement : node.getFDElementList()) {
      if (astfdElement instanceof ASTRootFeature) {
        if (root.isPresent()) {
          String name1 = ((ASTRootFeature) astfdElement).getFeature().getName();
          String name2 = root.get().getFeature().getName();
          Log.error("0xFD0001 Feature diagrams must not contain more than one root feature! '"
              + node.getName() + "' contains conflicting root features '" + name1 + "' and '"
              + name2 + "'.", astfdElement.get_SourcePositionStart());
        }
        else {
          root = Optional.of((ASTRootFeature) astfdElement);
        }
      }
    }
    if (!root.isPresent()) {
      Log.error("0xFD0002 Feature diagrams must contain a root feature! '"
          + node.getName() + "' contains no root feature.", node.get_SourcePositionStart());
    }
  }

  protected void checkParents(ASTFeatureDiagram node) {
    if (!node.isPresentSymbol() || null == node.getSymbol()) {
      Log.error("0xF0004 Feature diagram symbol table has to be created before cocos are checked!",
          node.get_SourcePositionStart());
    }

    //get all feature names used in the feature diagram
    Set<String> features = getAllFeatureNames(node);

    //from these, remove all features that have a parent feature
    Map<String, String> parents = getAllParents(node);
    features.removeAll(parents.keySet());

    //remove the root feature(s), which must not have a parent
    for (ASTFDElement e : node.getFDElementList()) {
      if (e instanceof ASTRootFeature) {
        String rootFeature = ((ASTRootFeature) e).getFeature().getName();
        features.remove(rootFeature);
      }
    }

    for (String feature : features) {
      Log.error("0xFD0007 Each feature except the root feature must have a parent feature! '"
              + feature + "' does not have a parent feature",
          node.get_SourcePositionStart());
    }
  }

  @Override public void check(ASTFeatureTreeRule node) {
    String lhs = node.getName();
    for (ASTFeature f : node.getFeatureGroup().getFeatureList()) {
      if (f.getName().equals(lhs)) {
        Log.error("0xFD0003 Feature diagram rules must not introduce self loops!",
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
      for (ASTFeature f : rule.getFeatureGroup().getFeatureList()) {
        String featureName = f.getName();
        if (parents.containsKey(featureName)) {
          //get the parent name that was found
          String parentName2 = parents.get(featureName);
          if (!parentName.equals(parentName2)) {
            Log.error("0xFD0008 A feature must not have more than one parent feature! '"
                    + featureName + "' has parents '" + parentName + "' and '" + parentName2 + "'",
                node.get_SourcePositionStart());
          }
        }
        else {
          if (node.getSpannedScope().resolveFeatureMany(parentName).isEmpty()) {
            Log.error("0xFD0010 The feature '" + featureName + "' has the unknown parent feature '"
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
