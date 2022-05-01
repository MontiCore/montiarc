/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

/**
 * Valid model. 
 */
component ExtendsSuperComponent extends SuperComponent {
  port 
    in Integer inputInteger;

  port out String outString;
  port out Integer outInteger;

  component components.body.subcomponents._subcomponents.StringAndIntegerInputAndOutput a;

  connect inputString -> a.stringIn;
  connect inputInteger -> a.intIn;
  connect a.stringOut -> outString;
  connect a.intOut -> outInteger;
}
