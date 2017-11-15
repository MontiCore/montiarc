package contextconditions.valid;


component AJavaOutputInExpression {
  
  port
    in String sIn,
    out String sOut;
  
  compute ReadSout {
    sOut  = "test";
    String x = sOut;
  }
}
