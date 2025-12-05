/* (c) https://github.com/MontiCore/monticore */
package composition;

/*
 * Invalid model: The incoming ports i1 and i2 of subcomponent sub, defined
 * by external component PortNotConnected6B, are not connected. The outgoing
 * ports o1 and o2 of the same component are also not connected, however, these
 * should only raise warnings.
 */
component PortNotConnected6A {

  PortNotConnected6B sub;

}
