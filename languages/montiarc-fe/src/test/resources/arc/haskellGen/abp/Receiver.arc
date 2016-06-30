package abp;

component Receiver {
  port 
    in ABPMessage<T> messageIn,
    out T messageOut,
    out Boolean ack;
}