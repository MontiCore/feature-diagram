/* (c) https://github.com/MontiCore/monticore */

package fddiff;

import java.util.Set;

/**
 * Represents the semantic difference witness between two feature diagrams.
 * In particular, a diff witness holds a feature configuration (a set of selected features).
 */
public class FDSemDiffWitness {

  private final Set<String> witness;

  public FDSemDiffWitness(Set<String> witness) {
    this.witness = witness;
  }

  public Set<String> getWitness() {
    return this.witness;
  }
}
