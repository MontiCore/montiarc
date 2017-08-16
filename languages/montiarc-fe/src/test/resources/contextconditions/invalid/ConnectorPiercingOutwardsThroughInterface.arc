package invalid;

component ConnectorPiercingOutwardsThroughInterface {
  component A {
    component Inner {
      port out String innerOut;
    }
  }
  
  component B {
    port in String bIn;
  }
  
  connect a.inner.innerOut -> b.bIn; 
}