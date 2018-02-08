package ajava;

component CompWithVariableAndPortInit {

  port
    in Integer i,
    out Integer o;
    
  Integer counter;
    
  init {
    o = new Integer(-1);
    counter = 0;
  }
  
  compute CalculateSomething {
    counter++;
    o = i + counter;    
  }


}