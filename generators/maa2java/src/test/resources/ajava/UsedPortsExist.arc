package ajava;

component UsedPortsExist {
  port
    in Double distance,
    out Double hulu;

  compute IncreaseHulu {    
    hulu = distance+2;
  }
}