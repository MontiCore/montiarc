/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/**
 * Invalid model.
 * In Mode M1 the port inS of component "inner1" is not connected.
 * In Mode M2 the port outS of component "inner2" is not connected.
 */
component NotAllIncomingPortsConnectedInEachMode {

  port in String inS;
  port out String outS;
  port in Integer inInt;
  port out Integer outInt;

  component Inner {
    port in String inS;
    port in Integer inInt;
    port out Integer outInt;
    port out String outS;
  }

  component Inner inner1;
  component Inner inner2;

  modeautomaton {
    mode M1 {
      use inner1;
      connect inInt -> inner1.inInt;
      connect inner1.outInt -> outInt;
      connect inner1.outS -> outS;
    }
    mode M2 {
      use inner2;
      connect inner2.outInt -> outInt;
      connect inS -> inner2.inS;
      connect inInt -> inner2.inInt;
    }

    initial M1;

    M1 -> M2;
  }
}