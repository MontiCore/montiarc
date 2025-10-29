/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanWrapperOut {
  port sync out Boolean p;

  automaton {
    initial state S;
    S -> S / { p = true; }
  }
}
