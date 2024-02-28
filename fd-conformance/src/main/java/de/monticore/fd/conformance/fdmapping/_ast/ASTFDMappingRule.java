/* (c) https://github.com/MontiCore/monticore */

package de.monticore.fd.conformance.fdmapping._ast;

import java.util.List;
import java.util.stream.Collectors;

public class ASTFDMappingRule extends ASTFDMappingRuleTOP {
  public List<String> getLeftFeatures() {
    return this.getLeftSide().getFDMapElementList().stream()
        .map(ASTFDMapElement::getName)
        .collect(Collectors.toList());
  }

  public String getRightFeature() {
    return this.getRightSide().name;
  }
}
