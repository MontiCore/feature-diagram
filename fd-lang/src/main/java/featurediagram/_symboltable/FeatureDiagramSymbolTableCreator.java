/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import de.monticore.symboltable.ImportStatement;
import featurediagram.FeatureDiagramMill;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._ast.ASTFeatureTreeRule;
import featurediagram._ast.ASTGroupPart;

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

    // if a symbol was found, use this
    if (resolvedFeatureSymbol.isPresent()) {
      symbol = resolvedFeatureSymbol.get();

      // create AST frmo symbol, find feature tree with symbol as root
      // resolve all features from this tree, and add all symbols to scope
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

}
