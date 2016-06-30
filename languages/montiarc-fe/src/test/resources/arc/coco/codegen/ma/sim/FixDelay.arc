package ma.sim;

<<author="Arne Haber">> 
component FixDelay<T>(int delay) {
  
  timing delayed;
  
  port in T portIn;
  port out T portOut;
   
}