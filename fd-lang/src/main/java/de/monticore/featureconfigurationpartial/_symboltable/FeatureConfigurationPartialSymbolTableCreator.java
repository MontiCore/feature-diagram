/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfigurationpartial._symboltable;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbolTableCreator;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * This class builds up the symbols and scopes from an AST of an FD model.
 */
public class FeatureConfigurationPartialSymbolTableCreator
    extends FeatureConfigurationPartialSymbolTableCreatorTOP {

  protected List<String> featureNameList = new ArrayList<>();

  public FeatureConfigurationPartialSymbolTableCreator(
      IFeatureConfigurationPartialScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureConfigurationPartialSymbolTableCreator(
      Deque<? extends IFeatureConfigurationPartialScope> scopeStack) {
    super(scopeStack);
  }

  /**
   * Create the symbl table for a passed AST of a PartialFC model.
   *
   * @param rootNode
   * @return
   */
  @Override public IFeatureConfigurationPartialArtifactScope createFromAST(
      ASTFCCompilationUnit rootNode) {
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";

    IFeatureConfigurationPartialArtifactScope artifactScope = FeatureConfigurationPartialMill
        .featureConfigurationPartialArtifactScopeBuilder()
        .setImportsList(new ArrayList<>())
        .setPackageName(packageName)
        .build();

    putOnStack(artifactScope);
    FeatureConfigurationSymbolTableCreator.handleImportStatements(rootNode);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  /**
   * collect names of selected features
   *
   * @param node
   */
  @Override
  public void visit(ASTSelect node) {
    super.visit(node);
    featureNameList.addAll(node.getNameList());
  }

  /**
   * Resolve FeatureSymbols and add these to the FeatureConfigurationSymbol.
   * The FeatureDiagramSymbols is set in the FeatureConfigurationSymbolTableCreator.
   *
   * @param node
   */
  @Override
  public void endVisit(ASTFeatureConfiguration node) {
    super.endVisit(node);
    FeatureDiagramSymbol fd = node.getSymbol().getFeatureDiagram();

    List<FeatureSymbol> selectedSymbols = new ArrayList<>();
    if (null != fd) {
      for (FeatureSymbol symbol : fd.getAllFeatures()) {
        if (featureNameList.contains(symbol.getName())) {
          featureNameList.remove(symbol.getName());
          selectedSymbols.add(symbol);
        }
      }
      for (String name : featureNameList) {
        Log.error("0xFC001 The selected Feature " + name + " does not exist in Feature Model " + fd
            .getFullName());
      }
    }

    node.getSymbol().setSelectedFeaturesList(selectedSymbols);
  }

}
