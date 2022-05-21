/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.statecharts;

/**
 * builds up on "TransitionReaction"
 * adds a guard to add alternatives
 */
component TransitionGuard {

  port in int inputValue;
  port out int outputValue;

  automaton {
    initial state SingleState;

    SingleState -> SingleState [inputValue > 0] / {outputValue = inputValue * 2;};
    SingleState -> SingleState / {outputValue = inputValue * -2;};
  }
}