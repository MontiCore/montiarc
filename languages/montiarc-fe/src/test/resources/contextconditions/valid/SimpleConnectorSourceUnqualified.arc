package valid;

component SimpleConnectorSourceUnqualified {
  component A {
    port out String outPort;
  }
  
  connect a.outPort -> b.inPort;
  
  component A a1
    [outPort -> b.inPort2];
  
  component B {
    port in String inPort,
         in String inPort2;
  }
}