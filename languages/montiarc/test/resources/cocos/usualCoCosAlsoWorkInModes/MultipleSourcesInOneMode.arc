/* (c) https://github.com/MontiCore/monticore */
package usualCoCosAlsoWorkInModes;

/**
 * Invalid model, because mode "Normal" adds two connectors ending in port "neutral"
 */
component MultipleSourcesInOneMode {

  port in double positive;
  port in double negative;
  port out double neutral;

  component Rotator core{
    port in double direction;
    port out double position;
  }

  mode Normal {
    positive -> neutral;
  }

  mode Rotating, Normal {
    positive -> core.direction;
    core.position -> neutral;
  }

  mode automaton {
    initial Normal;

    Normal -> Rotating;
  }
}
