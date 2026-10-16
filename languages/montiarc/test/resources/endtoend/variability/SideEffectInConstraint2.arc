/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: Prefix and suffix increment and decrement expressions (or in
 * general expressions with side effects) are not allowed in constraints.
 */
component SideEffectInConstraint2(int p) {
  constraint (++p > 1);
  constraint (--p > 1);
  constraint (p++ > 1);
  constraint (p-- > 1);
}
