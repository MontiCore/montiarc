/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.HasGenericInputAndOutputPort;

/*
 * Valid model
 */
component GenericArchitectureComponent<T> {
  
  port 
    in T myIncomingPort,
    out T myOutgoingPort;
    
  component HasGenericInputAndOutputPort<T> g;
  
  connect myIncomingPort -> g.tIn;
  connect g.tOut -> myOutgoingPort;

}
