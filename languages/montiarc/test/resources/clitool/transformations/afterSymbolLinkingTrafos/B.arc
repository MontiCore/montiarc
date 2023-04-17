/* (c) https://github.com/MontiCore/monticore */
package transformations.afterSymbolLinkingTrafos;

/**
 * Valid model.
 */
component B {
  autoconnect type;  // should connect inPortB -> a.inPortA;
  port in int inPortB;
  A a;
}
