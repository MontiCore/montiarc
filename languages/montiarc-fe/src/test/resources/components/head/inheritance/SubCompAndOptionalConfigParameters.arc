/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;
import components.body.subcomponents._subcomponents.SingleStringParameter;

/*
 * Valid model.
 */
component SubCompAndOptionalConfigParameters(Integer param1Int,
    String param2String = "Test")
{
  port in String inString;
  port out String outString;

  component HasStringInputAndOutput subComp;
  component SingleStringParameter(param2String) subCompWithParam;

  connect inString -> subComp.pIn;
  connect subComp.pOut -> outString;
}
