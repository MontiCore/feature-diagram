<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Beta-version: This is intended to become a MontiCore stable explanation. -->

<!-- Relevant Publications -->
[KMR24]: https://www.se-rwth.de/publications/Towards-Reference-Models-with-Conformance-Relations-for-Structure.pdf
[KRS+24]: https://www.se-rwth.de/publications/Towards-a-Semantically-Useful-Definition-of-Conformance-with-a-Reference-Model.pdf

# Checking Conformance to Reference Feature Diagrams

#### Foreword:
The aim of `fd-conformance` is to provide tooling for automatically checking 
conformance with a reference Feature Diagram. 
We base our notion of conformance on semantic model refinement: 
a concrete Feature Diagram conforms to a reference model 
if each valid feature configuration of the concrete model corresponds or 
*incarnates* a valid feature configuration of the reference model.
To designate concrete features as incarnations of reference features, we employ
a custom mapping language.
The tool then employs a translation of the models and incarnation mapping to 
SMT to automatically check for conformance.
If the model does not conform, a counter-example or *diff-witness* is provided 
to demonstrate non-conformance.


In the following, we will describe the concept and implementation of this 
tooling in greater detail.


[[_TOC_]]

## Feature Diagrams
Feature Diagrams are used to model product-lines and configuration options.
A Feature Diagram is a directed tree with nodes consisting of features and 
edges defining dependency between them.
A child feature always depends on is parent feature.
Edges can be *mandatory* or *optional*, and they can be bundled into *inclusive* 
or *exclusive* selections.
Furthermore, features can *require* or *exclude* other features.
The semantics of a Feature Diagram is the set of valid feature configurations.
For more information on Feature Diagrams used in this subproject refer to the 
[REAMDE](../README.md) of the _feature-diagram_ project.


## Reference Models and Conformance
The concept of a reference model is contingent on its relation to other more 
concrete models.
By itself, a reference model is a model within a given modeling language that 
is used to describe domain concepts and domain-specific relations in an 
exemplary manner.
Whether it was originally used as a concrete model or created as a pattern,
a reference model is defined by its contextual purpose. [[KMR24]]

A concrete model which conforms to a reference model is also referred to as a 
concretization of the latter.
Its elements are said to incarnate corresponding elements of the reference model.
A mapping of incarnations to their corresponding reference elements is referred
to as an incarnation mapping.

A notion of conformance to a reference model must be semantically sound, i.e., 
the essence or meaning of the reference model mus be preserved in its 
concretizations.
Formally, we require that a concretization semantically refines its reference 
model in the context of incarnation.
More specifically, after translating the incarnations to their corresponding 
references, the semantics of the concrete model must be a subset of the 
reference model's semantics. [[KRS+24]]

In the case of Feature Diagrams, we require that every valid feature configuration of the 
concretization corresponds to a valid feature configuaration of the reference models 
with respect to the incarnations of reference features.


## Incarnation Mapping

Incarnation Mappings are used to relate elements of a concrete model to 
corresponding elements of a reference model. 
This need not be a one-to-one mapping, as an incarnation of a reference element
may consist of multiple, composed concrete elements, and vice versa.
Moreover, an element within the reference model may have multiple incarnations 
in the concrete model.
Finally, not all elements in the concrete model need to incarnate elements of 
the reference model, leading to only a partial mapping.

We use the language [FDMapping](src/main/grammars/de/monticore/fd/conformance) 
to define such incarnation mappings for Feature Diagrams.
If for a specific feature in the reference model no incarnation is defined in 
the mapping, but a feature of equal name exists in the concrete model, 
it is mapped to this feature by default.


#### Example:

We can use a Feature Diagram to model a car product-line:
A car of this product-line needs a gasoline engine and can optionally have an 
infotainment system built-in.
The corresponding Feature Diagram might look like this:

```
featurediagram CarProductLine1 {
  Car -> Engine;
  Car -> InfotainmentSystem?;
  Engine -> Gasoline;
}
```

Then, for the next product-line of cars, the previous Feature Diagram is used 
as a reference.
However, now the car can either have an electric or hybrid engine as an 
alternative to the gasoline engine. 
Furthermore, it must contain a radio and may optionally have a navigation 
system and air conditioning.
The corresponding Feature Diagram might look like this:

```
featurediagram CarProductLine2 {
  Car -> Engine;
  Car -> ComfortFunctions;
  Engine -> Gasoline ^ Electric ^ Hybrid ;
  ComfortFunctions -> NavigationSystem?;
  ComfortFunctions -> Radio;
  ComfortFunctions -> AirConditioning?;
}
```

The infotainment system of the previous product-line is incarnated by the 
combination of the radio and navigation system. 
The remaining features either have the same name or are new.
As such, one would assume, that we only need to specify the incarnation of 
`InfotainmentSystem` in the incarnation mapping as follows:

```
mapping CarMapping {
  NavigationSystem ++ Radio ==> InfotainmentSystem;
}
```

However, this is a mistake, as the features `Electric` and `Hybrid` now exist 
as alternative engine types to `Gasoline`.
As such we have to map them accordingly:

```
mapping CarMapping2 {
  NavigationSystem ++ Radio ==> InfotainmentSystem;
  Gasoline ==> Gasoline;
  Electric ==> Gasoline;
  Hybrid ==> Gasoline;
}
```



## Conformance Checker

The conformance checker determines based on the aforementioned requirements 
whether a concrete
Feature Diagram conforms to a reference Feature Diagram concerning a given 
incarnation mapping.
As such, it takes two (2) Feature Diagrams and one (1) mapping as input and 
produces a corresponding _diff-witness_ in the case of non-conformance.
This witness is a feature configuration of the concrete model that does not 
correspond to any instance of the reference model, thus demonstrating that 
the concrete model does not refine the reference model, i.e., 
the meaning/essence of the reference model was not preserved.
Otherwise, the tool simply confirms the concrete model's conformance to the 
reference model.

The conformance checker operates by reducing the conformance problem to an 
equivalent instance of a satisfiability problem.
The Feature Diagrams and mappings are translated to propositional formulas: 
Each feature corresponding to a Boolean variable.
A child feature always implies its parent feature.
A parent feature also implies mandatory child features.
Inclusive and exclusive selections of child features are translated 
using corresponding disjunctions.
Similarly, a _requires_-relations is translated into an implication, 
and an _excludes_-relations implies the negation of the targeted feature.
Each mapping rule is translated to an implication, as well.
Finally, the formula representing the reference model is negated and conjoined 
with the others.
If the resulting formula is satisfiable, the concrete model is non-conforming, 
as any valid assignment of the Boolean variables represents a diff-witness.
If it unsatisfiable, then each feature configuration of the concrete model must
have a corresponding instance of the reference model, and conformance holds.

The tool can be used by executing the `MCFDC.jar ` with corresponding input parameters:
```
java -jar fd-conformance/target/libs/MCFDC.jar --reference "Reference.fd" --concrete "Concrete.fd" --map "Mapping.map"
```

### Example:

If we consider the previous example with the incomplete `CarMapping` and execute the `MCFDC.jar ` 
with

```
java -jar fd-conformance/target/libs/MCFDC.jar --reference "CarProductLine1.fd" --concrete "CarProductLine2.fd" --map "CarMapping.map"
```

the tool outputs the following:

```
===== Check if CarProductLine2 conforms to CarProductLine1 with respect to CarMapping =====
===== NOT CONFORM =====
Concrete Configuration: [ComfortFunctions, Hybrid, Car, Radio, Engine] is valid.
Reference Configuration: [Car, Engine] is NOT allowed!
```

If instead we use the complete `CarMapping2`, we simply get:
```
===== Check if CarProductLine2 conforms to CarProductLine1 with respect to CarMapping2 =====
===== CONFORM =====
```



 
