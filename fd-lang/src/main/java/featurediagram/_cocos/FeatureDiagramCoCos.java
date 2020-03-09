/* (c) https://github.com/MontiCore/monticore */

package featurediagram._cocos;

import featurediagram._ast.ASTFeatureDiagramNode;

public class FeatureDiagramCoCos {

  public static FeatureDiagramCoCoChecker getCheckerForAllCoCos() {
    FeatureDiagramCoCoChecker checker = new FeatureDiagramCoCoChecker();
    checker.addCoCo(new NoSelfLoopsInFeatureTree());
    checker.addCoCo(new SingleRootFeature());
//    checker.addCoCo(new UniqueFeatureNames());
    checker.addCoCo(new CTCFeatureNamesExist());
    checker.addCoCo(new FeatureParents());
    checker.addCoCo(new NonUniqueNameInGroup());
    return checker;
  }

  public static void checkAll(ASTFeatureDiagramNode node) {
    getCheckerForAllCoCos().checkAll(node);
  }

}