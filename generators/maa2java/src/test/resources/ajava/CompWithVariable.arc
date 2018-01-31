package ajava;

component CompWithVariable {

  port
    in Boolean b,
    out String s;

  Integer counter;
  
  compute Count {
    counter+=1;
  }  
  

}