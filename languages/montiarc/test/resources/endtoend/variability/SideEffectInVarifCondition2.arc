/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: Prefix and suffix increment and decrement expressions (or in
 * general expressions with side effects) are not allowed in varif conditions.
 */
component SideEffectInVarifCondition2(int p) {
  varif (++p > 1) { }
  varif (--p > 1) { }
  varif (p++ > 1) { }
  varif (p-- > 1) { }
}
