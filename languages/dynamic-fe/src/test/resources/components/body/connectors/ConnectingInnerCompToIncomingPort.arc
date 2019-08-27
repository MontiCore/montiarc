/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

/*
 * Invalid model.
 * Connects a subcomponent to an incoming port and the other way round.
 */
component ConnectingInnerCompToIncomingPort{

  component Inner inner {
    port in String inString;
    port out Integer outInteger;
  }

  port in String inString;
  port out Integer outInteger;

  connect inner.inString -> inString;
    // Error: Can not connect to an incoming port
    // Error: Can not connect from an incoming port of a subcomponent
  connect outInteger -> inner.outInteger;
    // Error: Can not connect to an outgoing port of a subcomponent
    // Error: Can not connect from an outgoing port
}