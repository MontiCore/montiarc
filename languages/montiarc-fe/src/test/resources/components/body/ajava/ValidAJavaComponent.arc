/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/**
 * Valid model.
 */
component ValidAJavaComponent(Integer x) {

  port
    in Integer i,
    out Integer o;
    
  Integer counter;
    
  init {
    o = new Integer(-1);
    counter = 0;
  }
  
  compute CalculateSomething {
    for(int j = 1; j<10; j++) {
      counter+=1;
      o = i + counter;
      o = x;
      o += j;
    }
    
    List<String> l = new ArrayList<String>();
    for(String s : l) {
      s.toString();
    }
    
  }


}
