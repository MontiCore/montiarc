/* (c) https://github.com/MontiCore/monticore */
package noSubComponentReferenceCycles;

/**
 * Invalid model. Is used in other components provoking subcomponent reference cycles.
 */
component DummyWithCycle {
  WithSubCompRefCycle sub;
}