/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.ports;

component DefaultEventOutputsForBoxedPorts {
  port out Byte outByte,
       out Short outShort,
       out Integer outInt,
       out Long outLong,
       out Float outFloat,
       out Double outDouble,
       out Character outChar,
       out Boolean outBoolean;

  <<timed>> automaton {
    initial state S;
    S -> S;
  }
}
