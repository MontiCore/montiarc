/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

component BooleanWrapperIn {
  port sync in Boolean p;

  automaton {
    initial state S;
    S -> S / { boolean foo = p; };
  }
}
