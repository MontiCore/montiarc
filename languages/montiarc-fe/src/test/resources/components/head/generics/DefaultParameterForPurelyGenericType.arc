package components.head.generics;

/*
 * Invalid model.
 *
 * @implements [Wor16] MR3:  No default values for configuration
 * parameters of purely generic types. (p. 59, Lst. 4.13)
 */
component DefaultParameterForPurelyGenericType<T>(T max = 10){
  // Empty body
}