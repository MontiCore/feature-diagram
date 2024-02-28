/* (c) https://github.com/MontiCore/monticore */

package de.monticore.fd.conformance.fdmapping._ast;

import de.monticore.fd.conformance.fdmapping.FDMappingMill;
import de.monticore.fd.conformance.fdmapping._visitor.FDMappingTraverser;
import de.monticore.fd.conformance.fdmapping._visitor.FDMappingVisitor2;
import java.util.ArrayList;
import java.util.List;

public class ASTFDMapping extends ASTFDMappingTOP {

  List<String> concreteFeatures;
  List<String> referenceFeatures;

  public List<String> getConcreteFeatures() {
    if (concreteFeatures == null) {
      collectFeatures();
    }
    return concreteFeatures;
  }

  public List<String> getReferenceFeatures() {
    if (referenceFeatures == null) {
      collectFeatures();
    }
    return referenceFeatures;
  }

  private void collectFeatures() {
    concreteFeatures = new ArrayList<>();
    referenceFeatures = new ArrayList<>();
    FDMappingVisitor2 featureCollector =
        new FDMappingVisitor2() {
          @Override
          public void visit(ASTFDMappingRule node) {
            node.getLeftSide().forEachFDMapElements(elem -> concreteFeatures.add(elem.getName()));
            referenceFeatures.add(node.getRightSide().getName());
          }
        };

    FDMappingTraverser traverser = FDMappingMill.traverser();
    traverser.add4FDMapping(featureCollector);
    this.accept(traverser);
  }
}
