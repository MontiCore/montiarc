/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasExitAction {
  automaton {
    initial state Foo {
      exit / { }
    };
  }
}