package components;


component B {


  ports
    out Integer bOut,
    out Integer bOut2;
    
  
  component D;
  
  connect d.dOut -> bOut;
  connect d.dOut2 -> bOut2;
  

}