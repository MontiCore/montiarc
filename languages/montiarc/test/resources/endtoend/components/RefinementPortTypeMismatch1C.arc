/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model: The port inherited from the super component has type int,
 * which is not a supertype of the type long of the port in the abstraction.
 */
component RefinementPortTypeMismatch1C extends RefinementPortTypeMismatch1A<int> refines RefinementPortTypeMismatch1B {

}
