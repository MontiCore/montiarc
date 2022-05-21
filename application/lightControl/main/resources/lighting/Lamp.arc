/* (c) https://github.com/MontiCore/monticore */
package lighting;

component Lamp(String name) {
  timing sync;

  port in boolean on;
  port out String light;

  automaton {
    initial {
      light = name + " idle";
    } state Off;
    state On;

    On -> Off [!on] / {light = name + " turned off";};
    Off -> On [on] / {light = (name + " turned on");};
    Off -> Off / {light = name + " stays off";};
    On -> On / {light = name + " stays on";};
  }
}
