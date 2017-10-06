package ajava;

component UsedPortsExist {
  port
    in double distance,
    out double hulu;

  compute IncreaseHulu {    
    hulu = distance+2;
  }
}