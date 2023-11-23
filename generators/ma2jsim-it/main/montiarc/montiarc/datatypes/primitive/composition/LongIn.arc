/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongIn {
  port in long p;

  <<sync>> automaton {
    initial state S;
    S -> S / { long foo = p; };
  }
}
