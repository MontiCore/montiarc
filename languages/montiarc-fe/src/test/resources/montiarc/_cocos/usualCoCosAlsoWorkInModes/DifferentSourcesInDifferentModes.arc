/* (c) https://github.com/MontiCore/monticore */
package usualCoCosAlsoWorkInModes;

/**
 * Valid model, because there are never multiple connectors ending in port 'neutral'
 */
component DifferentSourcesInDifferentModes {

  port in double positive;
  port in double negative;
  port out double neutral;

  component Rotator core{
    port in double direction;
    port out double position;
  }

  positive -> core.direction;

  mode Plus {
    positive -> neutral;
  }

  mode Minus {
    negative -> neutral;
  }

  mode Equal {
    core.positive -> neutral;
  }

  mode automaton {
    initial Plus;

    Plus -> Minus;
    Minus -> Equal;
  }
}