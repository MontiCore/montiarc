/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.states;

/**
 * Invalid model.
 *
 * @implements [Wor16] MR2: No initial values for variables of pure generic types.
 */
component GenericInitAssignment<T> {

  T t;

  automaton {
    state Idle;
    initial Idle / {t = 10};
  }
}
