/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/*
 * Invalid model.
 * The generic type parameters K and T are ambiguous.
 * Produces two errors.
 *
 * @implements [Hab16] B1: All names of model elements within a component namespace have to be unique. (p. 59, Listing 3.31)
 */
component TypeParametersNotUnique<K, K, V, T, T> {
  // Empty body
}
