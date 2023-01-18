/* (c) https://github.com/MontiCore/monticore */
package noSubComponentReferenceCycles;

/**
 * Valid model.
 */
component DummyWithoutCycle {
  component Inner {}
  Inner i1, i2;
}
