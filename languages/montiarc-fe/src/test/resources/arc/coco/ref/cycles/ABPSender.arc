package cycles;

import cycles.ABPMessage;
import ma.sim.FixDelay;


component ABPSender {
	autoconnect port;
	
	port 
		in String message,
		in Boolean ack,
		out ABPMessage abpMessage;
	
    
    component ABPInnerSender sender {

        port 
            in String message,
            in Boolean ack,
            out ABPMessage abpMessage;
        
    }
}