package ajava;

component CompWithVariableAndPortInit {

  port
    in Integer i,
    out Integer o;
    
  var Integer counter;
    
  init {
    o = new Integer(-1);
    counter = 0;
  }
  
  compute calculateSomething {
    counter++;
    o = i + counter;    
  }


}