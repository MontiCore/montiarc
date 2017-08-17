package contextconditions.invalid;

component UsedPortsNotExist {
  port
    in double distance,
    out String hulu;

  compute IncreaseHulu {    
    counter++;
    hulu = counter;
  }
}