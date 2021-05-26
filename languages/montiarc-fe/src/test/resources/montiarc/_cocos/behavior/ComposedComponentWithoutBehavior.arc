/* (c) https://github.com/MontiCore/monticore */
package behavior;

// valid, the inner component is atomic and hence may have behavior
component ComposedComponentWithoutBehavior {
  port in int number;

  component InnerComponent inner {
    port in int number;

    automaton {
      initial state Valid;
    }
  }

  number -> inner.number;

}