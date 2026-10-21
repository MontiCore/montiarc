/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.smt.VariableArcDeriveSMTSort;

import java.util.Optional;


public class ExpressionsBasis2SMT implements ExpressionsBasisHandler {

  protected final IDeriveSMTExpr deriveSMTExpr;
  protected ExpressionsBasisTraverser traverser;

  public ExpressionsBasis2SMT(@NotNull IDeriveSMTExpr deriveSMTExpr) {
    Preconditions.checkNotNull(deriveSMTExpr);
    this.deriveSMTExpr = deriveSMTExpr;
  }

  @Override
  public void setTraverser(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return this.traverser;
  }

  protected Expr2SMTResult getResult() {
    return this.deriveSMTExpr.getResult();
  }

  protected Context getContext() {
    return this.deriveSMTExpr.getContext();
  }

  protected IDeriveSMTSort getExpr2Sort() {
    return this.deriveSMTExpr.getSortDerive();
  }

  @Override
  public void handle(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    SymTypeExpression type = TypeCheck3.typeOf(node);
    Optional<Sort> sort = this.getExpr2Sort().toSort(node);

    this.getResult().clear();

    if (sort.isPresent()) {
      // Handle enum constants
      if (type.isObjectType() && type.asObjectType().hasTypeInfo()
        && type.asObjectType().getTypeInfo() instanceof OOTypeSymbol ooType && ooType.isIsEnum()) {
        IOOSymbolsScope ooScope = ooType.getSpannedScope();
        Optional<FieldSymbol> field = ooScope.resolveFieldMany(node.getName()).stream().findFirst();
        if (field.isPresent() && field.get().isIsPublic() && field.get().isIsStatic() && field.get().isIsFinal() && field.get().isIsReadOnly()
          && sort.get() instanceof EnumSort<?> enumSort && VariableArcDeriveSMTSort.indexOf(enumSort, node.getName()) >= 0) {
          this.getResult().setValue(enumSort.getConst(VariableArcDeriveSMTSort.indexOf(enumSort, node.getName())));
        }
      }
      if (this.getResult().getValue().isEmpty()) {
        // Handle variables
        this.getResult().setValue(this.deriveSMTExpr.variable(node.getName(), sort.get()));
      }
    }
  }
}
