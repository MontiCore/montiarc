package abp;

component LossyChannel {
  port 
    in ABPMessage<T> portIn,
    out T portOut;
}