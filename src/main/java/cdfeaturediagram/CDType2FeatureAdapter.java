package cdfeaturediagram;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import featurediagram._symboltable.FeatureSymbol;

public class CDType2FeatureAdapter extends FeatureSymbol {

  protected CDTypeSymbol adaptee;

  public CDType2FeatureAdapter(CDTypeSymbol adaptee) {
    super(adaptee.getName());
//    this.adaptee = adaptee;
//    this.setAccessModifier(adaptee.getAccessModifier());
//    this.setFullName(adaptee.getFullName());
//    this.setPackageName(adaptee.getPackageName());
  }

}
