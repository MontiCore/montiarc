/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.atLeastOneInitialState;

/**
 * Valid model.
 */
component TwoInitialStates {

  automaton {
    initial state A;
    initial state B;
    initial { System.out.println("foo"); } state C;
  }
}