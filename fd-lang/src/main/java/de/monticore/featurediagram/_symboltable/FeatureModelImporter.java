/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static de.monticore.io.paths.ModelCoordinates.createQualifiedCoordinate;
import static de.monticore.io.paths.ModelCoordinates.getReader;

/**
 * This class realizes loading of feature diagram models, as the AST of imported feature diagrams
 * is required for realizing the import statements in the symbol table infrastructure.
 */
public class FeatureModelImporter {

  public static ASTFeatureDiagram importFD(List<ASTMCImportStatement> imports, String fdName) {
    for (ASTMCImportStatement i : imports) {

      // 1. throw error and ignore import if it is a star import
      if (i.isStar()) {
        Log.error("0xFD132 Feature diagrams may not use stars '*' in import statements!");
        continue;
      }

      //2. Check whether symbol + ast of the imported FD are already available in global scope
      Optional<FeatureDiagramSymbol> fdSymbol = FeatureDiagramMill.globalScope()
          .resolveFeatureDiagram(i.getQName());
      if (!fdSymbol.isPresent() || null == fdSymbol.get().getAstNode()) {
        //3. if no, load FD model and create symtab
        loadFeatureModel(i.getQName(), fdName);
      }

      //4. Check again, and if symbol is found, return the ast
      fdSymbol = FeatureDiagramMill.globalScope().resolveFeatureDiagram(i.getQName());
      if (fdSymbol.isPresent()) {
        return fdSymbol.get().getAstNode();
      }
    }
    return null;
  }

  public static FeatureDiagramSymbol loadFeatureModelSymbol(String qualifiedName, String callName) {
    // try finding symbol in symbol table
    IFeatureDiagramGlobalScope gs = FeatureDiagramMill.globalScope();
    Optional<FeatureDiagramSymbol> fdSym = gs.resolveFeatureDiagram(qualifiedName);
    if (fdSym.isPresent()) {
      return fdSym.get();
    }

    // if it was not found, try to construct from model
    return loadFeatureModel(qualifiedName, callName);
  }

  public static FeatureDiagramSymbol loadFeatureModel(String qualifiedName, String callName) {
    // 1. find fully qualified location of fd model in model path
    ModelCoordinate modelCoord = createQualifiedCoordinate(qualifiedName, "fd");
    ModelPath mp = FeatureDiagramMill.globalScope().getModelPath();
    mp.resolveModel(modelCoord);
    if (!modelCoord.hasLocation()) {
      Log.error("0xFD133 Cannot find the model of the imported feature diagram '"
          + qualifiedName + "' imported in '" + callName + "'!");
      return null;
    }

    // 2. try parsing the fd model
    FeatureDiagramParser parser = new FeatureDiagramParser();
    ASTFDCompilationUnit ast;
    try {
      ast = parser.parse(getReader(modelCoord)).orElse(null);
    }
    catch (IOException e) {
      Log.error("0xFD134 Cannot parse the imported feature diagram '" + qualifiedName +
          "' imported in '" + callName + "'!");
      return null;
    }

    // 3. then create the symbol table
    FeatureDiagramMill.scopesGenitorDelegator().createFromAST(ast);
    FeatureDiagramSymbol symbol = ast.getFeatureDiagram().getSymbol();
    if (null == symbol) {
      Log.error("0xFD135 Cannot create symbol table of the imported feature diagram '"
          + qualifiedName + "' imported in '" + callName + "'!");
    }

    return symbol;
  }
}
