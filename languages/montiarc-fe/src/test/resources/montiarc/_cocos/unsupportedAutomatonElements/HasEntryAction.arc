/* (c) https://github.com/MontiCore/monticore */
package unsupportedAutomatonElements;

/**
 * Invalid model because of unsupported modeling elements.
 */
component HasEntryAction {
  automaton {
    initial state Foo {
      entry / { }
    };
  }
}