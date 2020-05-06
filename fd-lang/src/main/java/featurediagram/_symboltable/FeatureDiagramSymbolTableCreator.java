/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;
import featurediagram._ast.*;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureDiagramSymbolTableCreator extends FeatureDiagramSymbolTableCreatorTOP {

  protected Map<String, List<FeatureGroup>> groups;

  protected List<FeatureSymbol> featureSymbols;

  public FeatureDiagramSymbolTableCreator(IFeatureDiagramScope enclosingScope) {
    super(enclosingScope);
    this.groups = new HashMap<>();
    this.featureSymbols = new ArrayList<>();
  }

  public FeatureDiagramSymbolTableCreator(Deque<? extends IFeatureDiagramScope> scopeStack) {
    super(scopeStack);
    this.groups = new HashMap<>();
    this.featureSymbols = new ArrayList<>();
  }

  @Override
  public FeatureDiagramArtifactScope createFromAST(ASTFDCompilationUnit rootNode) {
    List<ImportStatement> importStatements = rootNode
            .getMCImportStatementList().stream()
            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
            .collect(Collectors.toList());
    String packageName = rootNode.isPresentPackage() ? rootNode.getPackage().toString() : "";

    FeatureDiagramArtifactScope artifactScope = FeatureDiagramSymTabMill
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
   * This method is overriden to set the root feature as attribute
   *
   * @param symbol
   * @param ast
   */
  @Override
  protected void initialize_FeatureDiagram(FeatureDiagramSymbol symbol,
                                           ASTFeatureDiagram ast) {
    Optional<ASTFeatureTreeRule> root = ast.getFDElementList().stream()
            .filter(e -> e instanceof ASTFeatureTreeRule)
            .map(e -> (ASTFeatureTreeRule) e)
            .findFirst();
    FeatureSymbol rootLoader = FeatureDiagramSymTabMill
            .featureSymbolBuilder()
            .setEnclosingScope(this.getCurrentScope().get())
            .setName(root.get().getName())
            .build();
    symbol.setRootFeature(rootLoader);

  }

  @Override
  protected void initialize_Feature(FeatureSymbol symbol, ASTFeature ast) {
    //store all feature symbols to a list, to set their groups in the endVisit of the FD
    featureSymbols.add(symbol);
  }

  @Override
  public void endVisit(ASTFeatureDiagram node) {
    super.endVisit(node);
    for (FeatureSymbol symbol : featureSymbols) {
      if (groups.containsKey(symbol.getName())) {
        symbol.setChildrenList(groups.get(symbol.getName()));
      } else {
        symbol.setChildrenList(new ArrayList<>());
      }
    }
  }

  @Override
  public void visit(ASTFeatureTreeRule node) {
    super.visit(node);
    FeatureSymbolLoader parent = createFeatureSymbolLoader(node.getName());
    List<FeatureSymbolLoader> children = new ArrayList<>();
    for (String child : node.getFeatureGroup().getNameList()) {
      FeatureSymbolLoader fsl = createFeatureSymbolLoader(child);
      children.add(fsl);
    }
    GroupKind kind = getGroupKind(node.getFeatureGroup());
    if (GroupKind.CARDINALITY == kind) {
      ASTCardinalizedGroup g = (ASTCardinalizedGroup) node.getFeatureGroup();
      int min = g.getCardinality().getLowerBound();
      int max = g.getCardinality().getUpperBound();
      putFeatureGroup(new FeatureGroup(parent, children, min, max));
    } else {
      putFeatureGroup(new FeatureGroup(parent, children, kind));
    }

  }

  protected void putFeatureGroup(FeatureGroup group) {
    String parentName = group.getParent().getName();
    if (!groups.containsKey(parentName)) {
      groups.put(parentName, new ArrayList<>());
    }
    groups.get(parentName).add(group);
  }

  protected FeatureSymbolLoader createFeatureSymbolLoader(String featureName) {
    return FeatureDiagramSymTabMill
            .featureSymbolLoaderBuilder()
            .setEnclosingScope(getCurrentScope().get())
            .setName(featureName)
            .build();
  }

  protected GroupKind getGroupKind(ASTFeatureGroup featureGroup) {
    if (featureGroup instanceof ASTOrGroup) {
      return GroupKind.OR;
    } else if (featureGroup instanceof ASTXorGroup) {
      return GroupKind.XOR;
    } else if (featureGroup instanceof ASTAndGroup) {
      return GroupKind.AND;
    } else if (featureGroup instanceof ASTCardinalizedGroup) {
      return GroupKind.CARDINALITY;
    } else {
      Log.error("0xFD0004 Unknown feature group kind '" + featureGroup.getClass().getName() + "'");
      return null;
    }
  }
}
