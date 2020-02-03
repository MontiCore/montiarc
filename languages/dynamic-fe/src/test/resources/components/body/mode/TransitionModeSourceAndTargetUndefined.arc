/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 */
component TransitionModeSourceAndTargetUndefined {

  component Adder add;

  modeautomaton {
    Mode1 -> Mode2;
    ExisitingMode -> Mode1;

    initial ExisitingMode;

    mode ExisitingMode {}
  }
}