/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import de.monticore.symboltable.ImportStatement;
import featurediagram.FeatureDiagramMill;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._ast.ASTFeatureTreeRule;
import featurediagram._ast.ASTGroupPart;
import featurediagram._visitor.SubFeatureFinder;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FeatureDiagramSymbolTableCreator extends FeatureDiagramSymbolTableCreatorTOP {

  public FeatureDiagramSymbolTableCreator(IFeatureDiagramScope enclosingScope) {
    super(enclosingScope);
  }

  public FeatureDiagramSymbolTableCreator(Deque<? extends IFeatureDiagramScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public FeatureDiagramArtifactScope createFromAST(ASTFDCompilationUnit rootNode) {
    List<ImportStatement> importStatements = rootNode
        .getMCImportStatementList().stream()
        .map(i -> new ImportStatement(i.getQName(), i.isStar()))
        .collect(Collectors.toList());
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";

    FeatureDiagramArtifactScope artifactScope = FeatureDiagramMill
        .featureDiagramArtifactScopeBuilder()
        .addAllImports(importStatements)
        .setPackageName(packageName)
        .setEnclosingScopeAbsent()
        .build();

    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  /**
   * This method adds features on the right-hand side of feature tree rules to the symbl table
   *
   * @param node
   */
  @Override public void visit(ASTGroupPart node) {
    super.visit(node);
    createOrFindFeatureSymbolOnFirstOccurrence(node.getName());
  }

  /**
   * This method adds features on the left-hand side of feature tree rules to the symbl table
   *
   * @param node
   */
  @Override public void visit(ASTFeatureTreeRule node) {
    super.visit(node);
    createOrFindFeatureSymbolOnFirstOccurrence(node.getName());
  }

  protected void createOrFindFeatureSymbolOnFirstOccurrence(String name) {
    IFeatureDiagramScope encScope = getCurrentScope().get();
    // if this feature name has already occured in the current feature model, stop
    if (encScope.resolveFeatureLocally(name).isPresent()) {
      return;
    }

    // else, we need to find an existing or create a new feature symbol for this name
    FeatureSymbol symbol;

    // try to find existing feature symbol
    Optional<FeatureSymbol> resolvedFeatureSymbol = encScope.resolveFeature(name);

    // if a symbol was found, use this and also add transitive Subfeatures
    if (resolvedFeatureSymbol.isPresent()) {
      symbol = resolvedFeatureSymbol.get();
      addSubfeaturesRecursively(encScope, symbol);
    }

    // else, create new symbol
    else {
      symbol = FeatureDiagramMill
          .featureSymbolBuilder()
          .setName(name)
          .build();
    }

    //connect symbol with environment
    addToScope(symbol);
  }

  private void addSubfeaturesRecursively(IFeatureDiagramScope encScope, FeatureSymbol symbol){
    if(symbol.getEnclosingScope() != encScope) {
      List<String> subfeatures = new SubFeatureFinder().getAllSubfeatures(symbol);
      subfeatures.forEach(subfeature -> {
        Optional<FeatureSymbol> subFeatureSybolOpt = symbol.getEnclosingScope().resolveFeature(subfeature);
        if (subFeatureSybolOpt.isPresent()) {
          addSubfeaturesRecursively(subFeatureSybolOpt.get().getEnclosingScope(), subFeatureSybolOpt.get());
          addToScope(subFeatureSybolOpt.get());
        }
      });
    }
  }

}
