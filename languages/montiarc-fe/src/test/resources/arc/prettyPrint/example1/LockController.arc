package example1;
import example1.datatypes.Types;

component LockController {
  port  in OpenCmd,
        in CloseCmd,
        in Boolean locked,
        out StatusMsg,
        out ChangeCmd;

}