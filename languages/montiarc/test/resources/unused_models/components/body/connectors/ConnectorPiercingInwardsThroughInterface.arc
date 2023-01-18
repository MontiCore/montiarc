/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

 /**
 * Invalid model: connector pierces through component As and component Inners interface.
 *
 * @implements [Hab16] CO1: Connectors may not pierce through component interfaces. (p. 60, Lst. 3.33)
 */
component ConnectorPiercingInwardsThroughInterface {
  component A {
    component Inner {
      port in String innerIn;
    }
  }

  component B {
    port out String bOut;
  }

  connect b.bout -> a.inner.innerIn;
}
