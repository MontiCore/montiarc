/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteWrapperIn {
  port sync in Byte p;

  automaton {
    initial state S;
    S -> S / { byte foo = p; };
  }
}
