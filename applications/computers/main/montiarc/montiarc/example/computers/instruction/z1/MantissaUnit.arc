/* (c) https://github.com/MontiCore/monticore */
package montiarc.example.computers.instruction.z1;

import montiarc.example.computers.instruction.z1.Types.AluOp;

/**
 * Combines the mantissas of both operands. The resulting magnitude is not
 * yet normalized. A division by zero is signaled as overflow.
 */
component MantissaUnit {

  port sync in AluOp op,
       sync in boolean sa,
       sync in int ma,
       sync in boolean sb,
       sync in int mb,
       sync in int shift,
       sync in boolean swap,
       sync out boolean sign,
       sync out int mantissa,
       sync out boolean overflow;

  compute {
    sign = sa;
    mantissa = ma;
    overflow = false;
    if (op == AluOp.ADD || op == AluOp.SUB) {
      int va = ma;
      if (sa) {
        va = -va;
      }
      int vb = mb;
      if ((sb && op == AluOp.ADD) || (!sb && op == AluOp.SUB)) {
        vb = -vb;
      }
      // align the mantissa of the smaller operand
      int s = shift;
      while (s > 0 && va != 0 && vb != 0) {
        if (swap) {
          va = va / 2;
        } else {
          vb = vb / 2;
        }
        s = s - 1;
      }
      int sum = va + vb;
      sign = sum < 0;
      if (sum < 0) {
        mantissa = -sum;
      } else {
        mantissa = sum;
      }
    } else if (op == AluOp.MUL || op == AluOp.DIV) {
      sign = (sa && !sb) || (!sa && sb);
      if (op == AluOp.MUL) {
        mantissa = ma * mb / 16384;
      } else if (mb == 0) {
        mantissa = 0;
        overflow = true;
      } else {
        mantissa = ma * 16384 / mb;
      }
    }
  }
}
