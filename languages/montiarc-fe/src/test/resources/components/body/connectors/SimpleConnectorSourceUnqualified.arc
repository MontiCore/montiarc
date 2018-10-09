package components.body.connectors;

/**
 * Valid model.
 */
component SimpleConnectorSourceUnqualified {
  component A {
    port out String outPort;
  }

  connect a.outPort -> b.inPort;
  
  component A a
    [outPort -> b.inPort2];
  
  component B {
    port in String inPort,
         in String inPort2;
  }
}