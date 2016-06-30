package example1;
import example1.datatypes.Types;

component LimousineActuator {

    port in  ChangeCmd, 
         out LockStatus;

    component Door lf, rf, lr, rr;
    component Trunk;
    component StatusControl;
    
           
    connect ChangeCmd -> lf.ChangeCmd,
                    rf.ChangeCmd,
                    lr.ChangeCmd,
                    rr.ChangeCmd,
                    Trunk.ChangeCmd;
                    
    connect lf.ok -> StatusControl.lfOk;
    connect rf.ok -> StatusControl.rfOk;
    connect lr.ok -> StatusControl.lrOk;
    connect rr.ok -> StatusControl.rrOk;
    connect Trunk.ok -> StatusControl.trunkOk;
    
    connect StatusControl.LockStatus -> LockStatus;

}