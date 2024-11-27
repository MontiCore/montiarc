/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisMill;
import de.monticore.literals.MCLiteralsDecoder;

public class ASTStereoValueExpr extends ASTStereoValueExprTOP {

  @Override
  public String getValue() {
    // Backwards compatible version for getValue()
    if (this.content == null) {
      if (ArcBasisMill.typeDispatcher().isExpressionsBasisASTLiteralExpression(this.expression)
        && ArcBasisMill.typeDispatcher()
        .isMCCommonLiteralsASTStringLiteral(ArcBasisMill.typeDispatcher()
          .asExpressionsBasisASTLiteralExpression(this.expression).getLiteral())) {
        this.content = MCLiteralsDecoder.decodeString(ArcBasisMill.typeDispatcher()
          .asMCCommonLiteralsASTStringLiteral(ArcBasisMill.typeDispatcher()
            .asExpressionsBasisASTLiteralExpression(this.expression)
            .getLiteral())
          .getValue());
      } else {
        this.content = "";
      }
    }

    return this.content;
  }
}
