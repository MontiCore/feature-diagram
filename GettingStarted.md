<!-- (c) https://github.com/MontiCore/monticore -->

# Getting Started
This explains how to set up the command line interface tools for the FD languages
and provides some copy and paste templates for executing the resulting tools against 
some test models that are contained in the language project. This is a good basis 
for getting familiar with the tools while experimenting with changes to the models. 
The tools are explained here and the languages are documented there.

## Set Up
Each tool is contained in a separate jar file, which is produced as result
of building the project with gradle. The following explains this.

##### Prerequisites
To build the project, it is required to install a Java 8 JDK and git. 

##### Step 1: Clone Project with git

    git clone https://git.rwth-aachen.de/monticore/languages/feature-diagram.git
    cd feature-diagram

##### Step 2: Build Project with gradle

    gradlew build --refresh-dependencies

## Examples

### Print help of the tools

    java -jar fd-lang/target/libs/FeatureDiagramTool.jar -h