/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.Instruction;
import montiarc.example.computers.instruction.z1.Types.Opcode;

/**
 * Reads the program from the punched tape, one instruction per time slice.
 * The Z1 has no jumps, the tape is processed strictly in order.
 */
component TapeReader {

  port sync in Instruction tape,
       sync out Opcode op,
       sync out int address;

  compute {
    op = tape.op;
    address = tape.address;
  }
}
