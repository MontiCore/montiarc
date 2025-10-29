/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component LongIn {
  port sync in long p;

  automaton {
    initial state S;
    S -> S / { long foo = p; }
  }
}
