# How to use the DSE Engine

## Model specification
The MontiArc models must be saved in the folder: `main/montiarc/automata` Java code is generated for all models located
in this folder or subfolders. The java code can be found in `build/classes/java/main/automata`.


MontiArc models may have the following features:
- Behavior description by automata
- Composition of models 
- Non-deterministic transitions selection
- Cyclic connector loops 
- Definition of model parameters
- Ports and internal variables can be of primitive types, strings and enum

## Controller
Controllers that should be used by the DSE Engine must be defined in the folder: `java/controller`. `AbstracController`
provides a controller template, which extends all necessary Interfaces to use a controller with the DSE Engine.
It also implements some of the necessary functions. In order to use a controller for the Evaluation the interface
`EvaluationControllerI` must be implemented.

## Run the DSE Engine
For each MontiArc model a class called `DSEMainMODELNAME` is generated. This class is the starting point of the engine.
The engine can either be started by calling the `runController` function or the specific run function of a certain controller.
If the function `runController` is called, the controller name, as well as the inputLength and further required parameters 
of the model must be passed as parameters. 
If the specific run function of a controller is called, e.g. `runPathCoverageController`, then the inputLength, as well as
further required parameters of the model must be passed as parameters.


```*.java
// example for model smallModel
DSEMainSmallModel smallModel = new DSEMainSmallModel();

// run controller through generel run function
smallModel.runController(
        new String[]{"", "PathCoverageController", "1", "400000"});
        )
        
// run controller through specific run function (e.g. PathCoverageController)
smallModel.runPathCoverageController(
        inputLength,
        new String[]{"", "", "", "400000"}
        );
```


## Evaluating a DSE run
The file Evaluation, in `test/java/evalutation` presents a possibility on how to evaluate a DSE run. All results will be
saved in an ExcelFile.

Each model needs an own evaluation function, as model specific information, such as states and transitions, are needed.
The input for the evaluation is defined through the MehthodScource functions. This function defines which controller on 
which model should be used, e.g. specification through the function `runCONTROLLER`. The input length, as well as whether the
calculation of the number of non-deterministic paths is desired is also defined there.
The calculation of the number or existence of non-deterministic paths is very computationally intensive, which is why it
is commented out by default. If it is desired, the parameter `calculateNumberOfNonDetPaths` must be set to true. If only
the existence of non-deterministic paths should be checked, the parameter `checkForNonDetPaths` must be set to true.

The Evaluation of `SmallModel` consists of
- runtime of the DSE engine 
- existence of duplicates of interesting inputs 
- completeness in terms of transitions, states considering and not considering internal variables
- existence of redundant paths
- existence of non-deterministic paths / number of non-deterministic paths

For the Excel file, the line in which the results of the controller are to be saved in the table must also be specified.
All results are also printed in the command line.

An example definition of for the information needed through specification function. This function must return a Stream
of Arguments. The first argument can be seen below. It defines the PathCoverageController through the function 
``runPathCoverageController``, which has to be cast to a Callable in order to use it in the evaluation. The result of 
such a function is always defined as a pair of a list of inputs and parameters and the output of the model.
Input length and parameters are defined through the input of the run function, parameter definition starts with
the fourth element and has to be a String. 

```*.java
(Callable<ResultI<Pair<List<ListerInSmallModel>, ListerParameterSmallModel>, List<ListerOutSmallModel>>>) () -> {
      DSEMainSmallModel smallModel = new DSEMainSmallModel();

      try {
        return smallModel.runPathCoverageController(
                inputLength,
                new String[]{"", "", "", "400000"});
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
}
```

Other parameters needed for the evaluation are the input length, a boolean specifying whether the existence of
non-deterministic paths should be checked. And a boolean if the number of non-deterministic paths should be calculated.
The name of the controller must also be specified as a string, as well as the row for the Excel sheet.


## Calculation of Semantic Differences
The controller to calculate the semantic difference between `SmallModel` and `SemDiffSmallModel`can be found in
`java/semDiff`. An example how to use this controller can be found in `SemDiffTest` (`test/java/evaluation`). This test
is model specific, and it needs helper-methods, found in `helper/SemDiffHelperSmallModel`. These helper methods are needed
to filter the input of the second component from the result of the DSE of the first component, and to convert the outputs of
the output of each component to a string. These conversions are needed to be able to compare the results of the two components.
In addition, the method for starting a dse run of the first component must also be defined for the controller. 
The SemDiffController can be started by the method `startSemDiff`, using the parameters `inputLength` and `parameters` 
defined at the beginning of the test.
The result of the controller is printed to the output. For the `smallModel` an inputLength of 3 is required to detect any 
semDiff witnesses.

```*.java
// Setup of the semDiffController with three helper functions and definition of the two models to be compared
semDiffController.setUp(converter, getEntriesResult1, getEntriesResult2, diffSmallModel, runOnce);

// start the controller
semDiffController.startSemDiff(inputLength, parameters);
```