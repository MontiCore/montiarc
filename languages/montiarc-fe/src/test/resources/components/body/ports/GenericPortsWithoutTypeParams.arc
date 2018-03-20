package components.body.ports;

/*
 * Invalid model. Has generic ports but no generic type parameters.
 *
 * Formerly named "CG3false" in MontiArc3.
 *
 * @implements TODO literature for import of port types
 *
 * TODO Enable Test; Problem: Currently no errors logged
 */
component GenericPortsWithoutTypeParams {
  
  port 
    in Tiger somePort, //Component GenericPortsWithoutTypeParams has no generic type parameter T
    out V anotherPort; //Component GenericPortsWithoutTypeParams has no generic type parameter V
}