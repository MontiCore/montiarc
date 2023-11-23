/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

// Should compile (by assigning correct default values to the ports)
component DefaultSyncOutputsForBoxedPorts {
  port out Byte outByte,
       out Short outShort,
       out Integer outInt,
       out Long outLong,
       out Float outFloat,
       out Double outDouble,
       out Character outChar,
       out Boolean outBoolean;

  <<sync>> automaton {
    initial state S;
    S -> S;
  }
}
