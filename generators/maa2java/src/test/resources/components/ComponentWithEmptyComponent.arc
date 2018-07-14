package components;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

component ComponentWithEmptyComponent(String stringParam){
  port in String inString;
  port out String outString;

  component EmptyComponent(5);

  component HasStringInputAndOutput a;
  connect a.pOut -> outString;
  connect inString -> a.pIn;
}