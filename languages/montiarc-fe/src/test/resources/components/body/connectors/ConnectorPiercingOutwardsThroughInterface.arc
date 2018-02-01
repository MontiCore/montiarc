package components.body.connectors;

/**
 * Invalid model. Connector pierces through A.
 */
component ConnectorPiercingOutwardsThroughInterface {
  component A {
    component Inner {
      port out String innerOut;
    }
  }
  
  component B {
    port in String bIn;
  }
  
  connect a.inner.innerOut -> b.bIn; // Error
}