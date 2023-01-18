/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/*
 * Invalid model. Has generic ports but no generic type parameters.
 *
 * Formerly named "CG3false" in MontiArc3.
 *
 */
component GenericPortsWithoutTypeParams {

  port
    in T somePort, //Component GenericPortsWithoutTypeParams has no generic type parameter T
    out V anotherPort; //Component GenericPortsWithoutTypeParams has no generic type parameter V
}
