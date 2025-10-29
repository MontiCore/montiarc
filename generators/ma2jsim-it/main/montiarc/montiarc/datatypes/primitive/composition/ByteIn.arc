/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteIn {
  port sync in byte p;

  automaton {
    initial state S;
    S -> S / { byte foo = p; }
  }
}
