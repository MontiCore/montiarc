package example1;

component ConfigurationComponent {

    autoconnect port;
    timing instant;
  
    component InnerA {
        timing untimed;
        autoconnect type;
    }
    
    component InnerB {
        autoconnect off;
        timing sync;
    }    
    
    component InnerC {
        timing delayed;
    }    
}