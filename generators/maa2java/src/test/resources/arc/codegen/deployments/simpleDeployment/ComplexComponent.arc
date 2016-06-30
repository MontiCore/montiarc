package simpleDeployment;


component ComplexComponent {

  ports
    in Integer CCIn,
    out String CCStringOut,
    out Integer CCIntOut; 
  
  component BWithSubcomponentA b;
  component C c;
  
  connect CCIn -> b.bIn;
  connect CCIn -> c.cIn;
  connect b.bOut -> CCStringOut;
  connect c.cOut -> CCIntOut; 
  
  

}