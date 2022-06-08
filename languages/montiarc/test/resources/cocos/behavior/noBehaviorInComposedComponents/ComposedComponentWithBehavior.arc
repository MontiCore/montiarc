/* (c) https://github.com/MontiCore/monticore */
package behavior.noBehaviorInComposedComponents;

// Invalid, composed components should not have behavior
component ComposedComponentWithBehavior {
  port in int number;

  component InnerComponent inner {
    port in int number;
  }

  number -> inner.number;

  automaton {
    initial state Invalid;
  }
}