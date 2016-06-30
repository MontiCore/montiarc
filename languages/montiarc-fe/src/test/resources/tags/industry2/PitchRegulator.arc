package industry2;

component PitchRegulator {
  ports in Integer estimatedAngle,
        in Integer desiredAngle,
        out Integer regulatedPitch;
}