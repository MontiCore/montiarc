/* (c) https://github.com/MontiCore/monticore */
package behavior.noRedundantInitialOutput;

// Valid, the two initial output declarations refer to two different states which is allowed by the coco.
component TwoUnambiguousOutputDeclarations {
  port in int number;

  automaton {
    initial { number = 0; } state Alpha;
    initial { number = 1; } state Beta;

    Alpha -> Beta;
  }
}