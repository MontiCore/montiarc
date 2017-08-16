package valid;

component InPortUniqueSender {
  
  component A {
    port out String aOut;
  }
  
  component B {
    port in String bIn;
  }
  
  connect a.aOut -> b.bIn;
  
  component B myB;
  
  component A myA
    [aOut -> myB.bIn];
}