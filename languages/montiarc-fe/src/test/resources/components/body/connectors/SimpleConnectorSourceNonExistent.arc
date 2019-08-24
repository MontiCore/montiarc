/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

/**
 * Invalid model. Port aOut does not exist.
 *
 * @implements [Hab16] R7: The source port of a simple connector must exist
 *  in the subcomponents type. (p. 65 Lst 3.42)
 */
component SimpleConnectorSourceNonExistent {
  component A {
  }
  
  component B {
    port in String bIn;
  }
  
  component A myA [aOut -> b.bIn];
}
