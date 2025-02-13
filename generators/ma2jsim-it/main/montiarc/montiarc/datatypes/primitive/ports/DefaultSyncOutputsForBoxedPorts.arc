/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

// Should compile (by assigning correct default values to the ports)
component DefaultSyncOutputsForBoxedPorts {
  port sync out Byte outByte,
       sync out Short outShort,
       sync out Integer outInt,
       sync out Long outLong,
       sync out Float outFloat,
       sync out Double outDouble,
       sync out Character outChar,
       sync out Boolean outBoolean;

  automaton {
    initial state S;
    S -> S;
  }
}
