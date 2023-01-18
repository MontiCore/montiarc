/* (c) https://github.com/MontiCore/monticore */
package behavior.noBehaviorInComposedComponents;

// Valid, composed components must not have behavior
component ComposedComponentWithoutBehavior {
  port in int number;

  component InnerComponent inner {
    port in int number;
  }

  number -> inner.number;

}
