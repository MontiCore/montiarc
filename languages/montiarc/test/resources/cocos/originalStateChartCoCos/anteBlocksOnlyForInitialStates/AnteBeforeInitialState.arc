/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.anteBlocksOnlyForInitialStates;

// valid model
component AnteBeforeInitialState {
  automaton {
    state A;
    initial { System.out.println("Yuuuhuu"); } state B;

    A -> B;
    B -> B;
  }
}
