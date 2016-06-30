package industry3;

component PitchRegulator {
  ports in Integer estimatedAngle,
        in Integer desiredAngle,
        out Integer regulatedPitch;
}