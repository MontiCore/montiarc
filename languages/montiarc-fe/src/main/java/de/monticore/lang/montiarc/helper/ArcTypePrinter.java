/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.helper;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;

/**
 * TODO: Implement
 *
 * @author Robert Heim
 */
public class ArcTypePrinter {

  /**
   * Converts an ASTType to a String
   *
   * @param type ASTType to be converted
   * @return String representation of "type"
   */
  public static String printType(ASTType type) {
    // TODO ArcTypes?!
    return TypesPrinter.printType(type);
  }

  /**
   * Converts an ASTReferenceType to a String
   *
   * @param type ASTReferenceType to be converted
   * @return String representation of "type"
   */
  public static String printReferenceType(ASTReferenceType astReferenceType) {
    // TODO ArcTypes?!
    return TypesPrinter.printReferenceType(astReferenceType);
  }

  /**
   * Converts an ASTType to a String, but omits type arguments
   *
   * @param type ASTType to be converted
   * @return String representation of "type" without type arguments
   */
  public static String printTypeWithoutTypeArgumentsAndDimension(ASTType typeBound) {
    // TODO ArcTypes?!
    return TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound);
  }
}
