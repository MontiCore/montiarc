package components.body.ajava;

/**
 * Invalid model.
 */
component AssignExpressionToOutgoingPort {
  
  port
    in String sIn,
    out String sOut;
  
  compute ReadSout {
    sOut  = "test" + "123";
    String x = sOut;
  }
}
