package industry;

component PitchEstimator {
  ports in Integer windSpeed,
        in Integer rotorSpeed,
        out Integer estimatedAngle;
}