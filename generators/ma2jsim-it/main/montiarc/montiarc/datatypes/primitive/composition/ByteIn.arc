/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteIn {
  port in byte p;

  <<sync>> automaton {
    initial state S;
    S -> S / { byte foo = p; };
  }
}
