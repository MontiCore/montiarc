/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/**
 * Invalid model.
 *
 * @implements [Wor16] MR2: No initial values for variables of pure generic types.
 */

component GenericInitValue<T> {
  T genericVariable;

  init {
    genericVariable = 10;
  }

   compute {
    }

}
