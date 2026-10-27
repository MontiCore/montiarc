/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.AluOp;
import montiarc.example.computers.instruction.z1.Types.Opcode;
import montiarc.example.computers.instruction.z1.Types.Register;
import montiarc.example.computers.instruction.z1.Types.Source;

/**
 * Decodes instructions into control signals for memory, registers,
 * arithmetic unit, and display.
 *
 * The first load after a store or display goes to R1, any further load
 * goes to R2. Arithmetic instructions compute R1 := R1 op R2.
 */
component ControlUnit {

  port sync in Opcode op,
       sync in int address,
       sync out int memAddress,
       sync out boolean memWrite,
       sync out Source source,
       sync out Register target,
       sync out AluOp aluOp,
       sync out boolean show;

  boolean r1Loaded = false;

  compute {
    memAddress = address;
    memWrite = false;
    source = Source.NONE;
    target = Register.R1;
    aluOp = AluOp.NONE;
    show = false;

    if (op == Opcode.PR || op == Opcode.LU) {
      if (op == Opcode.PR) {
        source = Source.MEMORY;
      } else {
        source = Source.KEYBOARD;
      }
      if (r1Loaded) {
        target = Register.R2;
      }
      r1Loaded = true;
    } else if (op == Opcode.PS) {
      memWrite = true;
      r1Loaded = false;
    } else if (op == Opcode.LD) {
      show = true;
      r1Loaded = false;
    } else {
      if (op == Opcode.LS1) {
        aluOp = AluOp.ADD;
      } else if (op == Opcode.LS2) {
        aluOp = AluOp.SUB;
      } else if (op == Opcode.LM) {
        aluOp = AluOp.MUL;
      } else {
        aluOp = AluOp.DIV;
      }
      source = Source.ALU;
    }
  }
}
