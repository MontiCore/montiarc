package setup;

component InteriorLightArbiter {
  port 
    in String switchStatus,
    in String alarmStatus,
    in String doorStatus,
    out String cmd;

}