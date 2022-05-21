/* (c) https://github.com/MontiCore/monticore */
package lighting;

component RandomBlinker {
  timing sync;

  port out boolean output;

  int counter = 0;

  automaton {
    state On;
    initial state Off;
    state Chance;
    state Blinking;

    On -> Off [Math.random() < 0.1] / {output = false;};
    On -> Blinking [Math.random() < 0.1] / {output = false;};
    Off -> On [Math.random() < 0.1] / {output = false;};
    Off -> Chance [Math.random() < 0.1] / {output = false;};
    Chance -> On [Math.random() < 0.1] / {output = true;};
    Chance -> Blinking [Math.random() < 0.1] / {output = true;};
    Blinking -> Off [Math.random() < 0.1] / {output = true;};
    Blinking -> Chance [Math.random() < 0.1] / output = true;;

    On -> On / {output = true;};
    Off -> Off / {output = false;};
    Chance -> Chance / {output = Math.random() < 0.5;};
    Blinking -> Blinking / {output = counter++ %2 == 0;};
  }
}
