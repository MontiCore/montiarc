package example1;
import example1.datatypes.Types;

component StatusControl {

    port in  DoorStat lfStat,
         in  DoorStat rfStat,
         in  DoorStat rlStat,
         in  DoorStat rrStat,
         in  DoorStat trunkStat,
         out LockStatus;
       
    ocl inv StatusCtrlAllLocked:
      let x = lfStat.size;
      in 
        lfStat.get(x).status == DoorStat.CLOSED &&
        rfStat.get(x).status == DoorStat.CLOSED &&
        lrStat.get(x).status == DoorStat.CLOSED &&
        rrStat.get(x).status == DoorStat.CLOSED &&
        trunkStat.get(x).status == DoorStat.CLOSED implies
        LockStatus.get(x+1).lockStatus == LockStatus.CLOSED;
    
    inv StatusCtrlAllOpened: 
      lfStat[x].status == DoorStat.OPENED && 
      rfStat[x].status == DoorStat.OPENED && 
      lrStat[x].status == DoorStat.OPENED && 
      rrStat[x].status == DoorStat.OPENED && 
      trunkStat[x].status == DoorStat.OPENED implies 
      LockStatus[x+1].lockStatus == LockStatus.OPENED;
      
    //inv StatusCtrlPartial:
      
      
    //inv StatusCtrlError:
      //lfStat.status == DoorStat.BLOCKED ||
      //rfStat.status == DoorStat.BLOCKED ||
      //lrStat.status == DoorStat.BLOCKED ||
      //rrStat.status == DoorStat.BLOCKED ||  
      //trunkStat.status == DoorStat.BLOCKED implies
      //locked@next.lockStatus = LockStatus.ERROR;
      
     
}