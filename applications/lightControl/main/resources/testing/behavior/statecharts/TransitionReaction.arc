/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.statecharts;

/**
 * the most simple form of a statechart, with only one state
 */
component TransitionReaction {

  port in int inputValue;
  port out int outputValue;

  automaton {
    initial state SingleState;

    SingleState -> SingleState / {outputValue = inputValue * 2;};
  }
}