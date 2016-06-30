package industry2;

component MainController {
  ports in Integer filteredSpeed,
        in Integer filteredOmega,
        out Integer PitchBrake,
        out Integer ParkingBrake,
        out Integer TurbineState;
}