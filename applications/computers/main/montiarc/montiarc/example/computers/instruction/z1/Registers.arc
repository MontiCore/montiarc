/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.Register;
import montiarc.example.computers.instruction.z1.Types.Source;

/**
 * The registers R1 and R2 of the arithmetic unit. The register contents are
 * emitted in the following time slice, which breaks the feedback loops via
 * memory and arithmetic unit.
 */
component Registers {

  port sync in Source source,
       sync in Register target,
       sync in int fromMemory,
       sync in int fromKeyboard,
       sync in int fromAlu,
       sync out int r1,
       sync out int r2;

  int reg1 = 1048576;
  int reg2 = 1048576;

  init {
    r1 = reg1;
    r2 = reg2;
  }

  <<delayed>> compute {
    if (source != Source.NONE) {
      int value = fromAlu;
      if (source == Source.MEMORY) {
        value = fromMemory;
      } else if (source == Source.KEYBOARD) {
        value = fromKeyboard;
      }
      if (target == Register.R1) {
        reg1 = value;
      } else {
        reg2 = value;
      }
    }
    r1 = reg1;
    r2 = reg2;
  }
}
