/* (c) https://github.com/MontiCore/monticore */
package libpack;

/**
 * Provides a component that is used by another model laying in another file structure (meaning not on the same model
 * path)
 */
component ReferencedComponent {
  port in int rotorSpeed,
        out int desiredAngle;
}
