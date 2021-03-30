/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import de.monticore.types.check.*;

public class ArcTypeCheck extends TypeCheck {


  public ArcTypeCheck(ISynthesize synthesizeSymType, IDerive iTypesCalculator) {
    super(synthesizeSymType, iTypesCalculator);
  }

  //TODO: Remove once fixed in MontiCore
  public static boolean compatible(SymTypeExpression left, SymTypeExpression right) {
    if (left.isTypeConstant() && right.isTypeConstant()) {
      SymTypeConstant leftType = (SymTypeConstant)left;
      SymTypeConstant rightType = (SymTypeConstant)right;
      if (isBoolean(leftType) && isBoolean(rightType)) {
        return true;
      } else if (isDouble(leftType) && rightType.isNumericType()) {
        return true;
      } else if (isFloat(leftType) && (rightType.isIntegralType() || isFloat(right))) {
        return true;
      } else if (isLong(leftType) && rightType.isIntegralType()) {
        return true;
      } else if (isInt(leftType) && rightType.isIntegralType() && !isLong(right)) {
        return true;
      } else if (isChar(leftType) && isChar(right)) {
        return true;
      } else if (isShort(leftType) && isShort(right)) {
        return true;
      } else {
        return isByte(leftType) && isByte(right);
      }
    } else if (right.getTypeInfo().getName().equals(left.getTypeInfo().getName())) {
      return true;
    } else if (isSubtypeOf(right, left)) {
      return true;
    } else if (right.print().equals(left.print())) {
      return true;
    } else {
      return left.deepEquals(right) || right.deepEquals(left);
    }
  }
}