/* (c) https://github.com/MontiCore/monticore */

package de.monticore.fd.conformance;

public enum ConfParams {
  MC_MAPPING("allow to map features with a mapping language"),
  NAME_MAPPING(
      "allow the implicitly map concrete features that was not mapped in the mapping language, to reference features that have the same name");

  ConfParams(String description) {}
}
