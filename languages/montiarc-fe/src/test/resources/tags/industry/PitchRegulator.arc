package industry;

component PitchRegulator {
  ports in Integer estimatedAngle,
        in Integer desiredAngle,
        out Integer regulatedPitch;
}