package components.body.connectors;

/**
 * Valid model.
 */
component SimpleConnectorSourceFullyQualified {
  component A {
    port out String aOut;
  }
  
  component B {
    port in String bIn;
  }
  
  connect a.aOut -> b.bIn;
  
  component A a1 [a1.aOut -> b.bIn];
}