package components.body.ports;

component PortTypesNotExist {

  port 
    in T somePort, //Component GenericPortsWithoutTypeParams has no generic type parameter T
    out V anotherPort; //Component GenericPortsWithoutTypeParams has no generic type parameter V
  
}