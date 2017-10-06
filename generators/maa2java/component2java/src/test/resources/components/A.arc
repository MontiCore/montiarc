package components;


<<deploy>> component A {

  component B;
  component C;
  
  connect b.bOut -> c.cIn;
  connect b.bOut2 -> c.cIn2;
  
  

}