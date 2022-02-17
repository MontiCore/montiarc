/* (c) https://github.com/MontiCore/monticore */


/**
 * Invalid model. The automaton contains an initial state declaration that uses a production rule from SCBasis. But we
 * introduce our own initial state declaration in ArcAutomaton that must be used.
 */
component HasIllegalInitialState {
  automaton {
    initial state Foo;
  }
}