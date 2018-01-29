package architectures.abp

component ABP {
  port
    in String messageIn,
    out String messageOut;

   component Sender;
   component Receiver;
   component LossyChannel med1;
   component LossyChannel med2;
   component Delay;
      
   connect messageIn -> sender.messageIn;
   connect sender.messageOut -> med1.portIn;
   connect med1.portOut -> receiver.messageIn;
   connect receiver.messageOut -> messageOut;
   connect receiver.ack -> med2.portIn;
   connect med2.portOut -> delay.portIn;
   connect delay.portOut -> sender.ack;
}
