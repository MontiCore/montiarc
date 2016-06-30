package industry3;

component TurbineController {
  ports in Integer omega,
        in Integer windSpeed,
        out Integer controlSignals;

  component Filtering filtering;
  component PitchEstimator pitchEstimator;
  component PIController piController;
  component MainController mainController;
  component PitchRegulator pitchRegulator;
  component BrakeController brakeController;
  component ParkController parkController;

  connect omega -> filtering.omega;
  connect windSpeed -> filtering.windSpeed;

  connect filtering.filteredOmega -> mainController.filteredSpeed,
                                     pitchEstimator.rotorSpeed,
                                     piController.rotorSpeed;
  connect filtering.filteredWindSpeed -> mainController.filteredOmega,
                                         pitchEstimator.windSpeed;
  connect pitchEstimator.estimatedAngele -> pitchRegulator.estimatedAngele;
  connect piController.desiredAngle -> pitchRegulator.desiredAngle;

  connect mainController.pitchBrake -> brakeController.pitchBrake;
  connect mainController.parkingBrake -> parkController.parkingBrake;
  connect mainController.turbineState -> parkController.turbineState,
                                         brakeController.turbineState;

  connect brakeController.brakeSignal -> parkController.brakeControl;

  connect parkController.out1 -> controlSignals;
}