/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanIn {
  port sync in boolean p;

  automaton {
    initial state S;
    S -> S / { boolean foo = p; }
  }
}
