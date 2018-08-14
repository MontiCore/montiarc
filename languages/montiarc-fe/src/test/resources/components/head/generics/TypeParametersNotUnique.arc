package components.head.generics;

/*
 * Invalid model.
 * The generic type parameters K and T are ambiguous.
 * Produces two errors.
 *
 * @implements No literature reference
 */
component TypeParametersNotUnique<K, K, V, T, T> {
  // Empty body
}