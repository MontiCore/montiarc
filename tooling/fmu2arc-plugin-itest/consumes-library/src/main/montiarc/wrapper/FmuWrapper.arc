/* (c) https://github.com/MontiCore/monticore */
package wrapper;

import fmu.Feedthrough;

component FmuWrapper {
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

  Feedthrough feedthrough(0.0);

  boolIn        -> feedthrough.Boolean_input;
  continuousIn  -> feedthrough.Float64_continuous_input;
  discreteIn    -> feedthrough.Float64_discrete_input;
  tunableParam  -> feedthrough.Float64_tunable_parameter;
  int32In       -> feedthrough.Int32_input;
  enumIn        -> feedthrough.Enumeration_input;
  stringIn      -> feedthrough.String_input;

  feedthrough.Boolean_output            -> boolOut;
  feedthrough.Float64_continuous_output -> continuousOut;
  feedthrough.Float64_discrete_output   -> discreteOut;
  feedthrough.Int32_output              -> int32Out;
  feedthrough.String_output             -> stringOut;
  feedthrough.Enumeration_output        -> enumOut;
}
