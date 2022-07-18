/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/**
 * Invalid model: port sOut must not be used on rightHandSide.
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
