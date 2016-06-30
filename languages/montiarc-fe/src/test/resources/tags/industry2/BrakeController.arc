package industry2;

component BrakeController {
  ports in Integer pitchBrake,
        in Integer regulatedPitch,
        in Integer turbineState,
        out Integer brakeSignal;
}