${tc.signature("helper", "_package", "name", "deployName")}

package ${_package};

public class ${deployName} {  

  final static int CYCLE_TIME = 50; // in ms
  
  public static void main(String[] args) {
    final ${name} cmp = new ${name}();
    
    cmp.setUp();
    cmp.init();
           
    long time;
     
    while (!Thread.interrupted()) {
      time = System.currentTimeMillis();
      cmp.compute();
      cmp.update();
      while((System.currentTimeMillis()-time) < CYCLE_TIME){
        Thread.yield();
      }
    }
  }
}
