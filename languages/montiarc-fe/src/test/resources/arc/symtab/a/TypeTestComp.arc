package a;

component TypeTestComp {


  port 
    in OpenCmd open,
    in CloseCmd close,
    in String,
    in Integer,
    in Number, 
    in Object,
    out ChangeCmd,
    out ButtonCmd,
    out StatusMsg,
    out LockStatus,
    out DoorStat status; 
}