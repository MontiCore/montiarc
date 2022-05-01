/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos;

component TransStateTransition {

  automaton {
    initial state Ready;
    state Locked;
    state Turning {
      initial state Accelerating;
      state Radiating;

      Accelerating -> Locked;
    };

    Ready -> Accelerating;
    Locked -> Turning;
  }

  // valid model
}