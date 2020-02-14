/* (c) https://github.com/MontiCore/monticore */

package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTFDElement;
import featurediagram._ast.ASTFeatureDiagram;
import featurediagram._ast.ASTRootFeature;

import java.util.Optional;

public class SingleRootFeature implements FeatureDiagramASTFeatureDiagramCoCo {

  @Override public void check(ASTFeatureDiagram node) {

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
}
