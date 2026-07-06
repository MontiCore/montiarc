/* (c) https://github.com/MontiCore/monticore */
package consumer;

import wrapper.FmuWrapper;

component FmuConsumer {
  port sync in  boolean boolIn;
    port sync in  double  continuousIn;
    port sync in  double  discreteIn;
    port sync in  double  tunableParam;
    port sync in  int     int32In;
    port sync in  int     enumIn;
    port sync in  String  stringIn;

    port sync out boolean boolOut;
    port sync out double  continuousOut;
    port sync out double  discreteOut;
    port sync out int     int32Out;
    port sync out String  stringOut;
    port sync out int     enumOut;

  FmuWrapper wrapper;

  boolIn        -> wrapper.boolIn;
  continuousIn  -> wrapper.continuousIn;
  discreteIn    -> wrapper.discreteIn;
  tunableParam  -> wrapper.tunableParam;
  int32In       -> wrapper.int32In;
  enumIn        -> wrapper.enumIn;
  stringIn      -> wrapper.stringIn;

  wrapper.boolOut       -> boolOut;
  wrapper.continuousOut -> continuousOut;
  wrapper.discreteOut   -> discreteOut;
  wrapper.int32Out      -> int32Out;
  wrapper.stringOut     -> stringOut;
  wrapper.enumOut       -> enumOut;
}
