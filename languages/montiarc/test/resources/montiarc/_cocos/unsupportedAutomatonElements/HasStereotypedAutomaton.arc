/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasStereotypedAutomaton {
  <<stereo_sound>> automaton {
    initial state Foo;
  }
}