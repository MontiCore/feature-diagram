/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdconformance;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.umlstereotype._ast.ASTStereotype;
import java.util.List;

public class AddOptionalStereotypeTrafo
    implements CDBasisVisitor2, CDAssociationVisitor2, CDInterfaceAndEnumVisitor2 {
  private final List<String> elementList;

  AddOptionalStereotypeTrafo(List<String> elementList) {
    this.elementList = elementList;
  }

  @Override
  public void visit(ASTCDClass node) {
    if (elementList.contains(node.getName())) {
      node.getModifier().setStereotype(buildOptionalStereoType());
    }
  }

  @Override
  public void visit(ASTCDInterface node) {
    if (elementList.contains(node.getName())) {
      node.getModifier().setStereotype(buildOptionalStereoType());
    }
  }

  @Override
  public void visit(ASTCDEnum node) {
    if (elementList.contains(node.getName())) {
      node.getModifier().setStereotype(buildOptionalStereoType());
    }
  }

  @Override
  public void visit(ASTCDAssociation node) {
    if (node.isPresentName() && elementList.contains(node.getName())) {
      node.getModifier().setStereotype(buildOptionalStereoType());
    }
  }

  private ASTStereotype buildOptionalStereoType() {
    String optionalTag = "optional";
    return CD4CodeMill.stereotypeBuilder()
        .addValues(
            CD4CodeMill.stereoValueBuilder()
                .setName(optionalTag)
                .setContent(optionalTag)
                .setText(CD4CodeMill.stringLiteralBuilder().setSource(optionalTag).build())
                .build())
        .build();
  }
}
