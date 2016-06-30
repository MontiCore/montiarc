package ma.sim;

<<author="Arne Haber">> 
component RandomDelay<T>(int minDelay, int maxDelay) {
  
  timing delayed;
  
  port in T portIn;
  port out T portOut;
  
  
}