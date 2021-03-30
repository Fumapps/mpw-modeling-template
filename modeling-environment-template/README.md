# $MPW_NAME_FIRST_UPPER$ - MPW Simulator

This is the modeled $MPW_NAME$ MPW simulator example based on the MPW framework (https://github.com/Fumapps/mpw-modeling-framework).

![modeling approach](documentation/graphics/mdsd-approach-concrete-simulator.svg)

It defines the modeling of the $MPW_NAME$ simulator under `/bundles/de.unistuttgart.iste.sqa.mpw.modeling.$MPW_NAME$simulator`.
After code-generation with Maven `package`, in `/simulators` the both simulators for Java and C++ can be found.

## Java Simulator

The Java simulator uses Maven, Java 15 and JavaFX.

##  C++ Simulator

The C++ simulator is based on CMake and SDL2.
