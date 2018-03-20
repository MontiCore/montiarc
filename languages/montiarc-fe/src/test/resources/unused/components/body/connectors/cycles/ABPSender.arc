package components.body.connectors.cycles;

import ma.sim.FixDelay;

/**
 * Used by ABP
 */
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