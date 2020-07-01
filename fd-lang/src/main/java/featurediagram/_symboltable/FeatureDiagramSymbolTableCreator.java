/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;
import featurediagram.FeatureDiagramMill;
import featurediagram._ast.ASTFDCompilationUnit;
import featurediagram._ast.ASTFeatureTreeRule;
import featurediagram._ast.ASTGroupPart;

import java.util.Deque;

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
        .build();
    handleImportStatements(rootNode);

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
    createFeatureSymbolOnFirstOccurrence(node.getName());
  }

  /**
   * This method adds features on the left-hand side of feature tree rules to the symbl table
   *
   * @param node
   */
  @Override
  public void visit(ASTFeatureTreeRule node) {
    super.visit(node);
    createFeatureSymbolOnFirstOccurrence(node.getName());
  }

  /**
   * This method iterates over imported feature diagrams through import statements and
   * for each of these, adds all elements of the imported feature diagram to the AST of the
   * current feature diagram.
   *
   * @param rootNode
   */
  protected void handleImportStatements(ASTFDCompilationUnit rootNode) {
    for (ASTMCImportStatement i : rootNode.getMCImportStatementList()) {
      if (i.isStar()) {
        Log.error("0xFD132 Feature diagrams may not use stars '*' in import statements!");
        continue;
      }
      FeatureDiagramSymbol fd = getFirstCreatedScope()
          .resolveFeatureDiagram(i.getQName()).orElse(null);
      if (null == fd) {
        Log.error("0xFD133 Cannot find imported feature diagram '" + i.getQName() + "' in '"
            + rootNode.getFeatureDiagram().getName() + "'!");
        continue;
      }
      if (!fd.isPresentAstNode()) {
        Log.error("0xFD134 Cannot find AST of imported feature diagram '" + i.getQName() + "' in '"
            + rootNode.getFeatureDiagram().getName() + "'!");
        continue;
      }
      rootNode.getFeatureDiagram().addAllFDElements(fd.getAstNode().getFDElementList());
    }
  }

  protected void createFeatureSymbolOnFirstOccurrence(String name) {
    // if this feature name has already occured in the current feature model, stop
    if (!getCurrentScope().get().resolveFeatureLocally(name).isPresent()) {
      //otherwise, create new FeatureSymbol
      addToScope(FeatureDiagramMill
          .featureSymbolBuilder()
          .setName(name)
          .build());
    }
  }

}
