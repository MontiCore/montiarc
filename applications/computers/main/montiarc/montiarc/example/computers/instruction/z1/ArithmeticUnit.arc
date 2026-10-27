/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.AluOp;

/**
 * The floating-point unit of the Z1. As in the original machine, exponents
 * and mantissas are processed by separate units. Without an operation, the
 * operand a is passed through.
 */
component ArithmeticUnit {

  port sync in AluOp op,
       sync in int a,
       sync in int b,
       sync out int result;

  Unpack unpackA, unpackB;
  ExponentUnit exponentUnit;
  MantissaUnit mantissaUnit;
  Normalizer normalizer;

  a -> unpackA.word;
  b -> unpackB.word;

  op -> exponentUnit.op;
  unpackA.exponent -> exponentUnit.ea;
  unpackB.exponent -> exponentUnit.eb;

  op -> mantissaUnit.op;
  unpackA.sign -> mantissaUnit.sa;
  unpackA.mantissa -> mantissaUnit.ma;
  unpackB.sign -> mantissaUnit.sb;
  unpackB.mantissa -> mantissaUnit.mb;
  exponentUnit.shift -> mantissaUnit.shift;
  exponentUnit.swap -> mantissaUnit.swap;

  exponentUnit.exponent -> normalizer.exponent;
  mantissaUnit.sign -> normalizer.sign;
  mantissaUnit.mantissa -> normalizer.mantissa;
  mantissaUnit.overflow -> normalizer.overflow;
  normalizer.word -> result;
}
