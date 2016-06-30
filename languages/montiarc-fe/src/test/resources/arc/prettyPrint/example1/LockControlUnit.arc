package example1;
import example1.datatypes.Types;
import ma.sim.FixDelay;

component LockControlUnit {

    port in OpenCmd,
         in CloseCmd,
         out StatusMsg;

    component LockControler;
    component LimousineActuator;
    component FixDelay<LockStatus>(1);
    
    
    connect OpenCmd -> LockControler.OpenCmd;
    connect CloseCmd -> LockControler.CloseCmd;
    connect LockControler.StatusMsg -> StatusMsg;
    connect LockControler.ChangeCmd -> LimousineActuator.ChangeCmd;
    connect LimousineActuator.LockStatus -> FixDelay.portIn;
    connect FixDelay.portOut -> LockControler.LockStatus;


}

