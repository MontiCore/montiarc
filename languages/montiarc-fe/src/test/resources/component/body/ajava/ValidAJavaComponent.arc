package component.body.ajava;

/**
 * Valid model.
 */
component ValidAJavaComponent {

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