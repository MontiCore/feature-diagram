/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package featurediagram._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class FeatureDiagramLanguage extends FeatureDiagramLanguageTOP {
  
  public FeatureDiagramLanguage() {
    super("Feature Diagram", "fd");
  }
  
  /**
   * @see de.monticore.CommonModelingLanguage#provideModelLoader()
   */
  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new FeatureDiagramModelLoader(this);
  }
}
