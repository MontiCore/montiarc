package bumperbot;

//import models.TimerCmd;
//import models.TimerSignal;
import library.types.*;

component BumpControl {

  port
    in Double distance,
    //in TimerSignal signal,
    //out TimerCmd timer,
    out Integer right,
    out Integer left;
}