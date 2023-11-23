/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component ByteOut {
  port out byte p;

  <<sync>> automaton {
    initial state S;
    S -> S / { int foo = 1; };
  }
}
