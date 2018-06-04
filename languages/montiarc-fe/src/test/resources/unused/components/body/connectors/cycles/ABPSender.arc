package components.body.connectors.cycles;

import types.CType;

/**
 * Valid model.
 * TODO Add test after fixing autoconnect
 */
component ABPSender {
	autoconnect port;
	
	port 
		in String message,
		in Boolean ack,
		out CType abpMessage;

  component ABPInnerSender sender {
    port
      in String message,
      in Boolean ack,
      out CType abpMessage;
  }

  /* Expected connectors from autoconnect:
  connect message -> sender.message;
  connect ack -> sender.ack;
  connect sender.abpMessage -> abpMessage;
  */
}