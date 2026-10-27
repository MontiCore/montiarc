/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.AluOp;

/**
 * Computes the preliminary exponent of the result. For additions and
 * subtractions, it also determines by how many places the mantissa of the
 * smaller operand has to be shifted (swap: operand a is the smaller one).
 */
component ExponentUnit {

  port sync in AluOp op,
       sync in int ea,
       sync in int eb,
       sync out int exponent,
       sync out int shift,
       sync out boolean swap;

  compute {
    exponent = ea;
    shift = 0;
    swap = false;
    if (op == AluOp.ADD || op == AluOp.SUB) {
      if (eb > ea) {
        exponent = eb;
        shift = eb - ea;
        swap = true;
      } else {
        shift = ea - eb;
      }
    } else if (op == AluOp.MUL) {
      exponent = ea + eb;
    } else if (op == AluOp.DIV) {
      exponent = ea - eb;
    }
  }
}
