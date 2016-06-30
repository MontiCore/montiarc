package industry3;

component MainController {
  ports in Integer filteredSpeed,
        in Integer filteredOmega,
        out Integer PitchBrake,
        out Integer ParkingBrake,
        out Integer TurbineState;
}