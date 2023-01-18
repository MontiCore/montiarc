/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

/*
 * Valid model.
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
