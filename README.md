> NOTE: <br>
This documentation is intended for  **modelers** who use the feature diagram languages.
The documentation for **language engineers** using or extending the feature diagram language is 
located **[here](fd-lang/src/main/grammars/FeatureDiagram.md)** and the documentation for using 
or extending the feature configuration language is located 
**[here](fd-lang/src/main/grammars/FeatureConfiguration.md)**.

# Feature Diagram Languages in MontiCore

The models of the feature diagram language are called *feature models*. 
A feature model describes a software or system family in terms of 
(user-experienceable) features. Feature models are used as variability models in
the context of software product lines. 
This documentation does not provide a general holistic introduction to feature models
and their applications, as this is provided by several books (e.g., 
[[CE00]](https://dl.acm.org/doi/book/10.5555/345203), 
[[CN02]](https://dl.acm.org/doi/book/10.5555/501065)) and 
research papers (e.g., 
[[BSL+13]](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.650.9121&rep=rep1&type=pdf), 
[[KCH+90]](https://apps.dtic.mil/dtic/tr/fulltext/u2/a235785.pdf)).

Instead, the purpose of this documentation is to 
introduce the [textual syntax](#textual-syntax), 
describe the [feature analyses](#feature-analyses) that our feature model tool provides, and 
support setting up the tool in form of a short [tutorial](#tool-usage-documentation).

## Textual Syntax
Fig. 1 presents the textual and graphical syntax of an example feature model 
describing a product line of phones.
As in Java, each feature model can be located in a package to enable a hierarchical
name space of feature diagrams. Omitting the package statement 
defaults to an empty package.

<div align="center">
<img width="800" src="doc/CarSyntaxExample.png">
<br><b>Figure 1:</b> 
Example for the textual syntax of a feature model (left) and its visual 
representation (right). The bottom demonstrates the syntax of a feature
configuration by example.
</div><br>


The content of the feature model begins with the keyword `featurediagram` followed 
by the feature model's name (l. 3) and the feature model body, enclosed by curly 
braces. 
The body of a feature model must contain a statement introducing the root feature (l. 4).
Further, it can introduce subfeatures through feature groups (ll. 5-9) and 
cross-tree constraints (ll.11-12).

Feature configurations start with the keyword `featureconfig` followed by an optional
name of the feature configuration. Afterward, the feature configuration has to 
indicate, which feature model it belongs to. This begins with the keyword `for`, followed
by the (qualified) name of the feature model.
The body of the feature configuration, enclosed in curly braces, contains a comma-separated
list of selected feature names. 
Please note that this syntax for feature configurations does not distinguish between 
features that are not selected (yet) and features, which are "unselected". To this end, 
a different feature configuration language has to be employed, e.g., to model step-wise configuration
of feature models.

## Feature Analyses

## Tool Usage Documentation

### Setting up the Tool

### Using the Tool

