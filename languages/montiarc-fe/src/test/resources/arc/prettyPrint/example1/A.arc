package example1;

import ma.sim.FixDelay;

component A {

  component B<T> {
    timing instant;
    autoconnect type;
    
    port in T someport;
  }
  
  component C(Integer x) {
    component FixDelay<String>("some String") d;
  }
  
  component D<X>(int a, String c) {
  	autoconnect off;
  }
  
  component FixDelay(1) fd;


  ocl inv test1:
    a == b;
  
  ocl inv test3: 
    b == c;
    
  java inv test2: {
    a == b;
  };    

}