package fddiff;

import lombok.Data;

import java.util.Set;

@Data
public class FDSemDiffWitness {

  private final Set<Feature> witness;
}
