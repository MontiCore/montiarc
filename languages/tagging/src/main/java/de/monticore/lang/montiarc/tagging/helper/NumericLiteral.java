package de.monticore.lang.montiarc.tagging.helper;

import de.monticore.literals.literals._ast.ASTDoubleLiteral;
import de.monticore.literals.literals._ast.ASTFloatLiteral;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLongLiteral;
import de.monticore.literals.literals._ast.ASTNumericLiteral;
import de.monticore.literals.literals._ast.ASTSignedDoubleLiteral;
import de.monticore.literals.literals._ast.ASTSignedFloatLiteral;
import de.monticore.literals.literals._ast.ASTSignedIntLiteral;
import de.monticore.literals.literals._ast.ASTSignedLongLiteral;

/**
 * Created by Michael von Wenckstern on 17.06.2016.
 */
public class NumericLiteral {
  // TODO ASTNumericLiteral should have a getValue() method
  public static Number getValue(ASTNumericLiteral numericLiteral) {
    if (numericLiteral instanceof ASTDoubleLiteral) {
      return ((ASTDoubleLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedDoubleLiteral) {
      return ((ASTSignedDoubleLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTIntLiteral) {
      return ((ASTIntLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedIntLiteral) {
      return ((ASTSignedIntLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTFloatLiteral) {
      return ((ASTFloatLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedFloatLiteral) {
      return ((ASTSignedFloatLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTLongLiteral) {
      return ((ASTLongLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedLongLiteral) {
      return ((ASTSignedLongLiteral) numericLiteral).getValue();
    }
    else {
      throw new Error("unexpected ASTNumericLiteral: " + numericLiteral.getClass());
    }
  }
}
