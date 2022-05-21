/* (c) https://github.com/MontiCore/monticore */
package lighting;

component LightCtrl {
  timing sync;

  port in boolean doorOpen;
  port in boolean changeMode;
  port out String door;
  port out String mirror;

  Lamp doorLight("door-light");
  Lamp mirrorLight("mirror-light");

  doorLight.light -> door;
  mirrorLight.light -> mirror;

  mode Default {
    doorOpen -> doorLight.on;
    doorOpen -> mirrorLight.on;
  }

  mode Comfort {
    LightAI ai;

    doorOpen -> ai.doorStatus;
    ai.door -> doorLight.on;
    ai.mirror -> mirrorLight.on;
  }

  mode Stroke {
    RandomBlinker flasher1;
    RandomBlinker flasher2;

    flasher1.output -> doorLight.on;
    flasher2.output -> mirrorLight.on;
  }

  mode automaton {
    initial Default;
    Default -> Comfort [ changeMode ];
    Comfort -> Stroke [ changeMode ];
    Stroke -> Default [ changeMode ];
  }
}
