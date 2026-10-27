/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.Instruction;

/**
 * Instruction-level model of Konrad Zuse's Z1 (1936-1938), following the
 * reconstruction by Raúl Rojas. "The Z1: Architecture and Algorithms of
 * Konrad Zuse's First Computer." arXiv:1406.1886 (2014).
 *
 * Each time slice executes one instruction of the program tape. Data words
 * are 22-bit floating-point numbers, encoded as an int:
 * 1 sign bit, 7 exponent bits (two's complement), 14 mantissa bits with a
 * hidden leading one. The exponent -64 encodes zero, 63 encodes infinity.
 *
 * Simplifications: the micro-steps of the control unit, the mechanical
 * clock phases, and the decimal digit entry are not modeled. The keyboard
 * and the display exchange decimal numbers as doubles; the conversion
 * between decimal and binary happens in the keyboard and display units.
 * The keyboard value is only read by the instruction Lu.
 */
component Z1 {

  port sync in Instruction tape,
       sync in double keyboard,
       sync out double display;

  TapeReader reader;
  ControlUnit control;
  Memory memory;
  Registers registers;
  ArithmeticUnit alu;
  Keyboard keyboardUnit;
  Display displayUnit;

  tape -> reader.tape;
  reader.op -> control.op;
  reader.address -> control.address;

  control.memAddress -> memory.address;
  control.memWrite -> memory.write;
  registers.r1 -> memory.data;

  control.source -> registers.source;
  control.target -> registers.target;
  memory.word -> registers.fromMemory;
  keyboardUnit.word -> registers.fromKeyboard;
  alu.result -> registers.fromAlu;

  keyboard -> keyboardUnit.value;

  control.aluOp -> alu.op;
  registers.r1 -> alu.a;
  registers.r2 -> alu.b;

  control.show -> displayUnit.show;
  registers.r1 -> displayUnit.word;
  displayUnit.value -> display;
}
