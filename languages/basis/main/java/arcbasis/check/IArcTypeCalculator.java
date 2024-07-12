/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.codehaus.commons.nullanalysis.NotNull;

public interface IArcTypeCalculator extends IDerive, ISynthesize {

  SymTypeExpression typeOf(@NotNull ASTExpression node);

  SymTypeExpression typeOf(@NotNull ASTExpression node, @NotNull SymTypeExpression targetType);

  SymTypeExpression typeOf(@NotNull ASTMCType node);

  SymTypeExpression typeOf(@NotNull ASTLiteral node);

  SymTypeExpression typeOf(@NotNull ASTMCReturnType node);

  SymTypeExpression typeOf(@NotNull ASTMCPrimitiveType node);

  SymTypeExpression typeOf(@NotNull ASTMCQualifiedName node);

}
