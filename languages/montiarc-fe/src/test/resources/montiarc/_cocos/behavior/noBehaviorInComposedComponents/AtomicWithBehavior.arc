/* (c) https://github.com/MontiCore/monticore */
package behavior.noBehaviorInComposedComponents;

// Valid. Atomic components are allowed to have behaviors
component AtomicWithBehavior {
  port in int number;

  automaton {
    state Alpha;
    state Beta;
    initial Alpha / {};

    Alpha -> Beta;
  }
}