/* (c) https://github.com/MontiCore/monticore */
package pack2;

import pack.ReferencedComponent;

/**
 * Consumes a component that is on another model path. We use the "consumed" component as a library model for which no
 * pretty printing / symbol table serialization should happen. On the other hand, _this_ component
 * (ReferencingComponent) should get pretty printed / symbol table deserialized.
 */
component ReferencingComponent {
  port in int rotorSpeed,
        out int desiredAngle;

  ReferencedComponent sub;

  rotorSpeed -> sub.rotorSpeed;
  sub.desiredAngle -> desiredAngle;
}
