/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * The constraint is only satisfied for some results of a method call that is
 * not known at compile time.
 */
component FunctionCallInConstraint {
  constraint (OOTypeWithStaticBooleanFunction.m());
}
