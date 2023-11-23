/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component CharIn {
  port in char p;

  <<sync>> automaton {
    initial state S;
    S -> S / { char foo = p; };
  }
}
