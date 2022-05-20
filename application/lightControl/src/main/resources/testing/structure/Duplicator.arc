/* (c) https://github.com/MontiCore/monticore */
package testing.structure;

/**
 * just doubles the int-input
 */
component Duplicator {

  port in int inputValue;
  port out int outputValue;

  automaton {
    initial state Duplicating {
      do / {
        outputValue = 2 * inputValue;
      }
    };
  }
}