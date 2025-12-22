/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;

@SuppressWarnings("unused")
public class TypeHelper {

  public String getNullLikeValue(SymTypeExpression type) {
    if (type.isPrimitive()) {
      if (BasicSymbolsMill.BOOLEAN.equals(type.asPrimitive().getPrimitiveName()))
        return "false";
      if (BasicSymbolsMill.CHAR.equals(type.asPrimitive().getPrimitiveName())) {
        return "(char) 0";
      } else return "0";
    } else return "null";
  }

  /**
   * Same as {@link this#getNullLikeValue(SymTypeExpression)}, but also explicitly casts 0's to bytes and shorts
   * (i.e.: narrowing operations).
   */
  public String getNarrowedNullLikeValue(SymTypeExpression type) {
    if (type.isPrimitive()) {
      if (BasicSymbolsMill.BYTE.equals(type.asPrimitive().getPrimitiveName())) {
        return "(byte) 0";
      } else if (BasicSymbolsMill.SHORT.equals(type.asPrimitive().getPrimitiveName())) {
        return "(short) 0";
      } else if (BasicSymbolsMill.CHAR.equals(type.asPrimitive().getPrimitiveName())) {
        return "(char) 0";
      } else if (BasicSymbolsMill.BOOLEAN.equals(type.asPrimitive().getPrimitiveName())) {
        return "false";
      } else if (BasicSymbolsMill.LONG.equals(type.asPrimitive().getPrimitiveName())) {
        return "0L";
      } else if (BasicSymbolsMill.FLOAT.equals(type.asPrimitive().getPrimitiveName())) {
        return "0.0f";
      } else if (BasicSymbolsMill.DOUBLE.equals(type.asPrimitive().getPrimitiveName())) {
        return "0.0";
      } else return "0";
    } else return "null";
  }

  public boolean isUnboxedBoolean(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.BOOLEAN.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedChar(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.CHAR.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedByte(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.BYTE.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedShort(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.SHORT.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedInt(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.INT.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedLong(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.LONG.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedFloat(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.FLOAT.equals(type.asPrimitive().getPrimitiveName());
  }

  public boolean isUnboxedDouble(SymTypeExpression type) {
    return type.isPrimitive() && BasicSymbolsMill.DOUBLE.equals(type.asPrimitive().getPrimitiveName());
  }
}
