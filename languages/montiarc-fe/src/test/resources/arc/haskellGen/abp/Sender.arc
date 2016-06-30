package abp;

component Sender {
  port 
    in T messageIn,
    in Boolean ack,
    out ABPMessage<T> messageOut;

}