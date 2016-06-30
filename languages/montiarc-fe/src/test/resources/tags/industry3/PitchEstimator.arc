package industry3;

component PitchEstimator {
  ports in Integer windSpeed,
        in Integer rotorSpeed,
        out Integer estimatedAngle;
}