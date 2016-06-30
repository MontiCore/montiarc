package basic;

component DelayedSender {
  port
    in String msgIn,
    out String msgOut;

   component Delay;
   component Sender;
      
   connect msgIn -> delay.inp;
   connect delay.outp -> sender.messageIn;
   connect sender.messageOut -> msgOut;
}
