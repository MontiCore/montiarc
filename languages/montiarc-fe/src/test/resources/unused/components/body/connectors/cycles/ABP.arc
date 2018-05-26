package components.body.connectors.cycles;

/*
 * TODO Determine correctness
 */
component ABP {
	
	port 
		in  String msg,
		out String transmittedMsg;

	component ABPSender
		sender [abpMessage -> receiver.abpMessage];
	
	component ABPReceiver
	   receiver [  ack -> sender.ack; 
	               message -> transmittedMsg];
	
	
	connect msg -> sender.message;

}