/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.anteBlocksOnlyForInitialStates;

// Invalid model
component AnteBeforeNormalStates {
  automaton {
    initial state A;
    { System.out.println("Illegal ante block"); } state B;
    { System.out.println("Illegal ante block"); } state C;

    A -> B;
    B -> C;
    C -> C;
  }
}
