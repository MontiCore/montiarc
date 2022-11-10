/* (c) https://github.com/MontiCore/monticore */
package elevator;

component FloorControl {

  port <<sync>> in boolean btn,
       <<sync>> out boolean light;
  port <<sync>> in boolean clear,
       <<sync>> out boolean req;

  automaton {
    initial state LightOff;

    LightOff -> LightOff [!btn || clear] / {
      light = false;
      req = false;
    };
    LightOff -> LightOn [btn && !clear] / {
      light = true;
      req = true;
    };

    state LightOn;

    LightOn -> LightOn [!clear] / {
      light = true;
      req = true;
    };
    LightOn -> LightOff [clear] / {
      light = false;
      req = false;
    };
  }

}
