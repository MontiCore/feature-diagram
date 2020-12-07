/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import static de.monticore.io.paths.ModelCoordinates.createQualifiedCoordinate;
import static de.monticore.io.paths.ModelCoordinates.getReader;

public class FeatureDiagramGlobalScope extends FeatureDiagramGlobalScopeTOP {

  public FeatureDiagramGlobalScope(ModelPath modelPath,
      String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  public FeatureDiagramGlobalScope() {
  }

  @Override public FeatureDiagramGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName, String symbolName) {
    String symbolFileExtension = getFileExt() + "sym";
    ModelCoordinate modelCoordinate = createQualifiedCoordinate(modelName, symbolFileExtension);
    String filePath = modelCoordinate.getQualifiedPath().toString();
    if(!isFileLoaded(filePath)) {
      //Load symbol table into enclosing global scope if a file has been found
      getModelPath().resolveModel(modelCoordinate);
      if (modelCoordinate.hasLocation()) {
        URL url = modelCoordinate.getLocation();
        this.addSubScope(symbols2Json.load(url));
      }
      else{
        modelCoordinate = createQualifiedCoordinate(modelName, getFileExt());
        filePath = modelCoordinate.getQualifiedPath().toString();
        getModelPath().resolveModel(modelCoordinate);
        if (modelCoordinate.hasLocation()) {
          Reader reader = getReader(modelCoordinate);
          ASTFDCompilationUnit ast = parse(reader);
          FeatureDiagramMill.scopesGenitorDelegator().createFromAST(ast);
        }
      }
      addLoadedFile(filePath);
    } else {
      Log.debug("Already tried to load model for '" + symbolName + "'. If model exists, continue with cached version.",
          "FeatureDiagramGlobalScope");
    }
  }

  protected ASTFDCompilationUnit parse(Reader reader) {
    FeatureDiagramParser parser = new FeatureDiagramParser();
    try {
      return parser.parse(reader).orElse(null);
    }
    catch (IOException e) {
      Log.error("0xFD324 Cannot parse the feature model for the feature configuration.");
      return null;
    }
  }
}
