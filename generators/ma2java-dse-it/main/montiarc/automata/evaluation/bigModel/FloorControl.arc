/* (c) https://github.com/MontiCore/monticore */
package automata.evaluation.bigModel;

component FloorControl {

  port in Boolean btn,
       out Boolean light;
  port in Boolean clear,
       out Boolean req;

  <<sync>> automaton {
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
    LightOn -> LightOff [clear == true] / {
      light = false;
      req = false;
    };
  }

}
