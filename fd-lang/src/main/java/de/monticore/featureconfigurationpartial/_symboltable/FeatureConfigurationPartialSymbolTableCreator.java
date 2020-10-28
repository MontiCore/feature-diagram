/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfigurationpartial._symboltable;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbol;
import de.monticore.featureconfiguration._symboltable.FeatureConfigurationSymbolTableCreator;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * This class builds up the symbols and scopes from an AST of an FD model.
 */
public class FeatureConfigurationPartialSymbolTableCreator
    extends FeatureConfigurationPartialSymbolTableCreatorTOP {

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
    String name = rootNode.getFeatureConfiguration().getName();

    //prohibit double creation of same symbol table
    IFeatureConfigurationPartialGlobalScope gs = FeatureConfigurationPartialMill
        .getFeatureConfigurationPartialGlobalScope();
    Optional<FeatureConfigurationSymbol> symbol =
        gs.resolveFeatureConfiguration(Names.getQualifiedName(packageName,name));
    if(symbol.isPresent()){
      gs.removeSubScope(symbol.get().getEnclosingScope());
    }

    IFeatureConfigurationPartialArtifactScope artifactScope = FeatureConfigurationPartialMill
        .featureConfigurationPartialArtifactScopeBuilder()
        .setPackageName(packageName)
        .build();

    putOnStack(artifactScope);
    FeatureConfigurationSymbolTableCreator.handleImportStatements(rootNode);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  /**
   * collect names of selected features. Resolve FeatureSymbols and add these to the
   * FeatureConfigurationSymbol.The FeatureDiagramSymbols is set in the
   * FeatureConfigurationSymbolTableCreator.
   *
   * @param node
   */
  @Override
  public void visit(ASTSelect node) {
    super.visit(node);
    List<String> selectedFeatureNames =  new ArrayList<>(node.getNameList());
    List<FeatureSymbol> selectedSymbols = new ArrayList<>();
    FeatureConfigurationSymbol fc = (FeatureConfigurationSymbol) node
        .getEnclosingScope()
        .getSpanningSymbol();
    FeatureDiagramSymbol fd = fc.getFeatureDiagram();
    if (null != fd) {
      for (FeatureSymbol symbol : fd.getAllFeatures()) {
        if (selectedFeatureNames.contains(symbol.getName())) {
          selectedFeatureNames.remove(symbol.getName());
          selectedSymbols.add(symbol);
        }
      }
      for (String name : selectedFeatureNames) {
        Log.error("0xFCA01 The selected Feature " + name + " does not exist in Feature Model " + fd
            .getFullName());
      }
      fc.setSelectedFeaturesList(selectedSymbols);
    }
  }

}
