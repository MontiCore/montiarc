/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteWrapperOut {
  port sync out Byte p;

  automaton {
    initial state S;
    S -> S / { int foo = 1; }
  }
}
