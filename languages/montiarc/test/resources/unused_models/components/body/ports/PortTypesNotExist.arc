/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/*
 * Invalid model.
 */
component PortTypesNotExist {

  port
    in T somePort, //Component GenericPortsWithoutTypeParams has no generic type parameter T
    out V anotherPort; //Component GenericPortsWithoutTypeParams has no generic type parameter V

}
