/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteOut {
  port sync out byte p;

  automaton {
    initial state S;
    S -> S / { int foo = 1; }
  }
}
