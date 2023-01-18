/* (c) https://github.com/MontiCore/monticore */
package usualCoCosAlsoWorkInModes;

/**
 * Invalid model, because there are two connectors ending in port "neutral" in mode "Inversion"
 */
component ModeAddsExtraSource {

  port in double positive;
  port in double negative;
  port out double neutral;

  component Rotator core{
    port in double direction;
    port out double position;
  }

  positive -> core.direction;
  core.position -> neutral;

  mode Forward {}

  mode Inversion {
    negative -> neutral;
  }

  mode automaton {
    initial Forward;

    Forward -> Inversion;
  }
}
