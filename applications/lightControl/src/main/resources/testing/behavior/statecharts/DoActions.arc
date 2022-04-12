/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.statecharts;

/**
 * builds up on "TwoStates"
 * adds a actions to add alternatives
 */
component DoActions {

  port in int inputValue;
  port out int outputValue;

  automaton {
    initial state Duplicating {
      do / {
        outputValue = 2 * inputValue;
      }
      exit / {
        outputValue = -1;
      }
    };
    state Quadrupling {
      do / {
        outputValue = 4 * inputValue;
      }
    };

    Duplicating -> Quadrupling [inputValue == -1];
  }
}