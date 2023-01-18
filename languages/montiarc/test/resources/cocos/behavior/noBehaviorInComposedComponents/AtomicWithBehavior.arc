/* (c) https://github.com/MontiCore/monticore */
package behavior.noBehaviorInComposedComponents;

// Valid. Atomic components are allowed to have behaviors
component AtomicWithBehavior {
  port in int number;

  automaton {
    initial state Alpha;
    state Beta;

    Alpha -> Beta;
  }
}
