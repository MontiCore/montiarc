package industry;

component MainController {
  ports in Integer filteredSpeed,
        in Integer filteredOmega,
        out Integer pitchBrake,
        out Integer parkingBrake,
        out Integer turbineState;
}