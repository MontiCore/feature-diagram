<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

<!-- Relevant Publications -->
[RRS23]: https://www.se-rwth.de/publications/On-Implementing-Open-World-Semantic-Differencing-for-Class-Diagrams.pdf
[KMR24]: https://www.se-rwth.de/publications/Towards-Reference-Models-with-Conformance-Relations-for-Structure.pdf
[KRS+24]: https://www.se-rwth.de/publications/Towards-a-Semantically-Useful-Definition-of-Conformance-with-a-Reference-Model.pdf

<!-- Other Links -->
[cd4analysis]: https://github.com/MontiCore/cd4analysis
[fd-conformance]: fd-conformance

# Checking Conformance to 150% Reference Models

The aim of `cdxfd-conformance` is to provide tooling for automatically checking
conformance with a 150% reference model consisting of a reference Class Diagram 
and a Feature Diagram that defines configurations of elements to be incarnated.

The tooling developed in this project is based on the concepts developed in 
[[KMR24]] and [[KRS+24]], and constitutes as an extension of the conformance 
checker for Class Diagrams developed in **[cd4analysis]**.

## Approach and Implementation
A concretization of a 150% reference model is a Class Diagram containing 
incarnations of elements from the reference Class Diagram that form a
valid configuration of the Feature Model.
Thus, in order to check for conformance, we derive a feature configuration
from the incarnations present in the concrete model, then check if it 
constitutes a valid instance of the Feature Model (employing our translation 
to SMT from **[fd-conformance]**).
If the configuration is valid, we add the stereotype `<<optional>>` to all 
elements in the reference Class Diagram that are referenced in the Feature 
Model but not part of the feature configuration, and check for conformance
using the implementation from **[cd4analysis]**.





