/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasStereotypedAutomaton {
  <<stereo_sound>> automaton {
    state Foo;
    initial Foo / { /* empty initial output declaration */ };
  }
}