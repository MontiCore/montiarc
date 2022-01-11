/* (c) https://github.com/MontiCore/monticore */
package usualCoCosAlsoWorkInModes;

/**
 * Invalid model, because in mode 'RemoteActive' of component 'Slider' of mode 'Nesting' there are two connectors ending in port 'force'
 */
component ModeAddsExtraSourceInNestedComponent {

  port in double positive;
  port in double negative;
  port out double neutral;

  component Rotator core{
    port in double direction;
    port out double position;
  }

  mode Rotating {
    negative -> core.direction;
    core.position -> neutral;
  }

  mode Nesting {

    component Slider subSlider{
      port in double strength;
      port out double force;

      component RemoteAntenna antenna{
        port out double received;
      }

      strength -> force;

      mode NoRemote {}

      mode RemoteActive {
        antenna.received -> force;
      }

      mode automaton {
        initial NoRemote;
        NoRemote -> RemoteActive;
      }
    }

    positive -> subSlider.strength;
    subSlider.force -> neutral;
  }

  mode automaton {
    initial Rotating;

    Rotating -> Nesting;
  }
}