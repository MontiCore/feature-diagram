package fddiff;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Builder(builderClassName = "Builder")
public class FeatureDiagram {

  private final Feature root;

  private final Set<Feature> features;

  private final Map<Feature, Set<Feature>> mandatory;

  private final Map<Feature, Set<Feature>> or;

  private final Map<Feature, Set<Feature>> xor;

  private final Map<Feature, Set<Feature>> implies;

  private final Map<Feature, Set<Feature>> excludes;
}
