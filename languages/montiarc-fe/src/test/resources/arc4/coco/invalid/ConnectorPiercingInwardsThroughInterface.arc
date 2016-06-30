package invalid;

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