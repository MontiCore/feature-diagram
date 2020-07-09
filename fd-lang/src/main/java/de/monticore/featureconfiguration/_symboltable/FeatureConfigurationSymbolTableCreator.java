/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration._symboltable;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class FeatureConfigurationSymbolTableCreator
    extends FeatureConfigurationSymbolTableCreatorTOP {

  public FeatureConfigurationSymbolTableCreator(IFeatureConfigurationScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureConfigurationSymbolTableCreator(
      Deque<? extends IFeatureConfigurationScope> scopeStack) {
    super(scopeStack);
  }

  private List<FeatureSymbol> selectedSymbols = new ArrayList<>();

  private FeatureDiagramSymbol fd;

  @Override
  public void visit(ASTFeatures node) {
    super.visit(node);
    for (String name : node.getNameList()) {
      Optional<FeatureSymbol> optFeature = fd.getSpannedScope().resolveFeature(name);
      if (optFeature.isPresent()) {
        selectedSymbols.add(optFeature.get());
      }
      else {
        Log.error("0xFC001 The selected Feature " + name + " does not exist in Feature Model " + fd
            .getFullName());
      }
    }
  }

  @Override
  public void endVisit(ASTFeatureConfiguration node) {
    super.endVisit(node);
    node.getSymbol().setSelectedFeatureList(selectedSymbols);
    node.getSymbol().setFeatureDiagram(fd);
  }

  @Override
  public void visit(ASTFeatureConfiguration node) {
    super.visit(node);
    if (node.isPresentFdNameSymbol()) {
      fd = node.getFdNameSymbol();
    }
    else {
      Log.error(
          "0xFC002 The feature configuration `" + node.getName() + "` uses the feature model '"
              + node.getFdName() + "' that cannot be resolved!");
    }
  }

}
