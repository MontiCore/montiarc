package components;


component C {

  port
    in Integer cIn,
    in Integer cIn2;
    
  component E;
  
  connect cIn -> e.eIn;
  connect cIn2 -> e.eIn2;
  

}