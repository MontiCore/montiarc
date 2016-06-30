package e;

import ma.sim.FixDelay;

component ComponentFromJar {
    
    autoconnect type;
    
    port 
        in String sIn,
        out String sOut;
        
    component FixDelay<String>(1);
    
            

}