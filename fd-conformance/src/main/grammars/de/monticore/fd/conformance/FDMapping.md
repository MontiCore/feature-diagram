<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

<!-- List with all references used within this markdown file: -->
[README]: ../../../../../../../README.md
[Grammar]: ./FDMapping.mc4

> NOTE: <br>
This document is intended for  **language engineers** who extend, adapt or embed the FD language.
**modelers** please look **[here][README]**.

# MontiCore Feature Diagram Incarnation Mapping Language (FDMapping)

This language is designed to allow for defining incarnation mappings between concrete Feature 
Diagrams and corresponding reference Feature Diagrams.
The incarnation mappings are used as part of the input for a corresponding SMT-based 
conformance checker. 
For more information on reference models and incarnation mappings refer to the [README].

## Syntax

An incarnation mapping maps elements of a concrete model to elements of a reference model.
In the case of Feature Diagrams, we map concerete features to reference features by name.
This need not be a one-to-one mapping, as concrete features may incarnate multiple
reference features and reference features may be incarnated by a composition of concrete features.
To accommodate this fact, an `FDMapping` consists of multiple `FDMappingRules`, 
each having a `FDElementComposition` as their `leftSide` and a `FDMapElement` as their `rightSide`.
The `FDElementComposition` contains at least one `FDElement` itself.
An `FDElement` references exactly one (1) feature by name.
As such, each `FDMappingRule` maps a composition of concrete features to a reference feature.
The mapped features of each rule need not be disjoint, thus allowing for multiple incarnations.


### Example

Consider the example from the [README]:

```
mapping CarMapping2 {
  NavigationSystem ++ Radio ==> InfotainmentSystem;
  Gasoline ==> Gasoline;
  Electric ==> Gasoline;
  Hybrid ==> Gasoline;
}
```

`CarMapping2` defines the composition of concrete features `NavigationSystem` and `Radio` 
as an incarnation of the reference feature `InfotainmentSystem`.
It also defines `Gasoline`, `Electric` and `Hybrid` as incarnations of the engine type
`Gasoline` of the reference model.


