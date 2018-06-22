package components.body.connectors;

import types.CType;

/**
 * Valid model.
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