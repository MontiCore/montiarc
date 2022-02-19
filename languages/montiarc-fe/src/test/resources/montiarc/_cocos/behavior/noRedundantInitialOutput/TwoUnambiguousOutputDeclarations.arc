/* (c) https://github.com/MontiCore/monticore */
package behavior.noRedundantInitialOutput;

// Valid, the two initial output declarations refer to two different states which is allowed by the coco.
component TwoUnambiguousOutputDeclarations {
  port in int number;

  automaton {
    state Alpha;
    state Beta;
    initial Alpha / { number = 0; };
    initial Beta / { number = 1; };

    Alpha -> Beta;
  }
}