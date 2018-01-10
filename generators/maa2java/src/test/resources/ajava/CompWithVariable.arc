package ajava;

component CompWithVariable {

  port
    in Boolean b,
    out String s;

  var Integer counter;
  
  compute Count {
    counter+=1;
  }  
  

}