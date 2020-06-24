/* (c) https://github.com/MontiCore/monticore */
package featureconfigurationpartial._symboltable;

import de.se_rwth.commons.logging.Log;
import featureconfiguration._ast.ASTFeatureConfiguration;
import featureconfigurationpartial._ast.ASTSelect;
import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._symboltable.FeatureSymbol;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class FeatureConfigurationPartialSymbolTableCreator extends FeatureConfigurationPartialSymbolTableCreatorTOP{


  public FeatureConfigurationPartialSymbolTableCreator(IFeatureConfigurationPartialScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureConfigurationPartialSymbolTableCreator(Deque<? extends IFeatureConfigurationPartialScope> scopeStack) {
    super(scopeStack);
  }

  private List<FeatureSymbol> selectedSymbols = new ArrayList<>();

  private FeatureDiagramSymbol fd;

  @Override
  public void visit(ASTSelect node) {
    super.visit(node);
    node.streamNames().forEach(name ->{
      Optional<FeatureSymbol> optFeature = fd.getSpannedScope().resolveFeature(name);
      if(optFeature.isPresent()){
        selectedSymbols.add(optFeature.get());
      }else {
        Log.error("0xFC001 The selected Feature "+ name + " does not exist in Feature Model "+ fd.getFullName());  //TODO
      }
    });
  }

  @Override
  public void endVisit(ASTFeatureConfiguration node) {
    super.endVisit(node);
    node.getSymbol().setSelectedFeatureList(selectedSymbols);
  }

  @Override
  public void visit(ASTFeatureConfiguration node) {
    super.visit(node);
    fd = node.getFdNameSymbol();
  }

}
