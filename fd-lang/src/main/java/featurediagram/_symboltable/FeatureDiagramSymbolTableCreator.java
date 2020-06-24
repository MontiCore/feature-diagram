/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import de.monticore.symboltable.ImportStatement;
import featurediagram.FeatureDiagramMill;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._ast.ASTFeatureTreeRule;
import featurediagram._ast.ASTGroupPart;
import net.sourceforge.plantuml.Log;

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
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";

    FeatureDiagramArtifactScope artifactScope = FeatureDiagramMill
        .featureDiagramArtifactScopeBuilder()
        .setPackageName(packageName)
        .setEnclosingScopeAbsent()
        .build();
    handleImportStatements(rootNode, artifactScope);

    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  /**
   * This method adds features on the right-hand side of feature tree rules to the symbl table
   *
   * @param node
   */
  @Override
  public void visit(ASTGroupPart node) {
    super.visit(node);
    createOrFindFeatureSymbolOnFirstOccurrence(node.getName());
  }

  /**
   * This method adds features on the left-hand side of feature tree rules to the symbl table
   *
   * @param node
   */
  @Override
  public void visit(ASTFeatureTreeRule node) {
    super.visit(node);
    createOrFindFeatureSymbolOnFirstOccurrence(node.getName());
  }

  protected void handleImportStatements(ASTFDCompilationUnit rootNode,
      FeatureDiagramArtifactScope artifactScope) {
    List<ImportStatement> importStatements = rootNode
        .getMCImportStatementList().stream()
        .map(i -> new ImportStatement(i.getQName(), i.isStar()))
        .collect(Collectors.toList());

    for (ImportStatement i : importStatements) {
      FeatureDiagramSymbol fd = artifactScope.resolveFeatureDiagram(i.getStatement()).orElse(null);
      if (null == fd) {
        Log.error("0xFD133 Cannot find feature diagram '" + i.getStatement()
            + "' that is imported in the feature diagram '"
            + rootNode.getFeatureDiagram().getName() + "'!");
        return;
      }
      if (!fd.isPresentAstNode()) {
        Log.error("0xFD133 Cannot find AST of feature diagram '" + i.getStatement()
            + "' that is imported in the feature diagram '"
            + rootNode.getFeatureDiagram().getName() + "'!");
        return;
      }
      rootNode.getFeatureDiagram().addAllFDElements(fd.getAstNode().getFDElementList());
    }
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

    // if a symbol was found, use this (is only the case in combination with language composition)
    if (resolvedFeatureSymbol.isPresent()) {
      symbol = resolvedFeatureSymbol.get();
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
