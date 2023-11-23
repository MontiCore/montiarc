/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

// Should compile (by assigning correct default values to the ports)
component DefaultEventOutputs {
  port out byte outByte,
       out short outShort,
       out int outInt,
       out long outLong,
       out float outFloat,
       out double outDouble,
       out char outChar,
       out boolean outBoolean;

  <<timed>> automaton {
    initial state S;
    S -> S / { };
  }
}
