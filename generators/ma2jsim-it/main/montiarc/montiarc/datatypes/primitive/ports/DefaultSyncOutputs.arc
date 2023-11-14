/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

component DefaultSyncOutputs {
  port out byte outByte,
       out short outShort,
       out int outInt,
       out long outLong,
       out float outFloat,
       out double outDouble,
       out char outChar,
       out boolean outBoolean;

  <<sync>> automaton {
    initial state S;
    S -> S;
  }
}
