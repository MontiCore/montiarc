/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: Assignment expressions (or in general expressions with side
 * effects) are not allowed in varif conditions.
 */
component SideEffectInVarifCondition1(boolean p) {
  varif (p = true) { }
}
