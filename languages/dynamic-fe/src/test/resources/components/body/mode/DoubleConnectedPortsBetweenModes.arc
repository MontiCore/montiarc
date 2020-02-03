/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Valid model.
 */
component DoubleConnectedPortsBetweenModes {

  component Inner {
    port in String in1;
    port in String in2;
    port out String out1;
    port out String out2;
  }

  component Inner inner1;
  component Inner inner2;

  port in String pin1;
  port in String pin2;
  port in String pin3;

  port out String po1;
  port out String po2;
  port out String po3;

  connect pin1 -> inner1.in2;
  connect inner2.out1 -> po2;

  modeautomaton {
    mode M1 {
      use inner1;
      connect pin2 -> inner1.in1;
      connect inner1.out1 -> po1;
      connect inner1.out2 -> po3;
    }

    mode M2 {
      use inner2;
      connect pin2 -> inner2.in1;
      connect pin3 -> inner2.in2;
      connect inner2.out2 -> po1;
    }

    initial M1;

    M1 -> M2;
  }
}