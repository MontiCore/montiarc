/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

// Should compile (by assigning correct default values to the ports)
component DefaultSyncOutputs {
  port sync out byte outByte,
       sync out short outShort,
       sync out int outInt,
       sync out long outLong,
       sync out float outFloat,
       sync out double outDouble,
       sync out char outChar,
       sync out boolean outBoolean;

  automaton {
    initial state S;
    S -> S;
  }
}
