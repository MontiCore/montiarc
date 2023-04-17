/* (c) https://github.com/MontiCore/monticore */
package symboltable.pass3;

/**
 * Valid model.
 */
component B {
  port in int inPortB;

  A a;

  inPortB -> a.inPortA;
}
