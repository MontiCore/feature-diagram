/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.utils.Names;

import java.util.Collections;
import java.util.Set;

/**
 * Language for Feature Diagrams
 */
public class FeatureDiagramLanguage extends FeatureDiagramLanguageTOP {

  public FeatureDiagramLanguage() {
    super("Feature Diagram", "fd");
  }

  @Override
  protected FeatureDiagramModelLoader provideModelLoader() {
    return new FeatureDiagramModelLoader(this);
  }

  @Override
  protected Set<String> calculateModelNamesForFeature(String name) {
    // e.g., if p.FeatureDiagram.Feature, return p.FeatureDiagram
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }

    return Collections.emptySet();
  }
}
