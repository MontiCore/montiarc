/* (c) https://github.com/MontiCore/monticore */
package lighting;

component LightAI {
  timing sync;

  // whether the door is open or not
  port in boolean doorStatus;
  // whether the door-light should be on
  port out boolean door;
  // whether the mirror-light should be on
  port out boolean mirror;

  automaton {
    initial state Predicting;

    Predicting -> Predicting / {door = false; mirror = true;};
  }
}