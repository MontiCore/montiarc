/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.statecharts;

//import java.lang.String;

/**
 * builds up on "TransitionReaction"
 * adds a second state to add alternatives
 */
component TwoStates {

  port in int inputValue;
  port out int outputValue;

  automaton {
    initial state Duplicating;
    state Quadrupling;

    Duplicating -> Quadrupling / {outputValue = 2 * inputValue;};
    Quadrupling -> Quadrupling / {outputValue = 4 * inputValue;};
  }
}