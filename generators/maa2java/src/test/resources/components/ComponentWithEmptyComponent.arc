package components;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

<<deploy>> component ComponentWithEmptyComponent{
  port in String inString;
  port out String outString;

  component EmptyComponent(5);

  component HasStringInputAndOutput a;
  connect a.pOut -> outString;
  connect inString -> a.pIn;
}