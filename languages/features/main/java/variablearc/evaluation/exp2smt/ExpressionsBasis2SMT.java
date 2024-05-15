/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;

import java.util.Optional;


public class ExpressionsBasis2SMT implements ExpressionsBasisHandler {

  protected final IDeriveSMTExpr deriveSMTExpr;
  protected final IArcTypeCalculator tc;
  protected ExpressionsBasisTraverser traverser;

  public ExpressionsBasis2SMT(@NotNull IDeriveSMTExpr deriveSMTExpr, @NotNull IArcTypeCalculator tc) {
    Preconditions.checkNotNull(deriveSMTExpr);
    Preconditions.checkNotNull(tc);
    this.deriveSMTExpr = deriveSMTExpr;
    this.tc = tc;
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

  protected String getPrefix() {
    return this.deriveSMTExpr.getPrefix();
  }

  @Override
  public void handle(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    Optional<Sort> sort = this.getExpr2Sort().toSort(this.getContext(), node);

    this.getResult().clear();

    if (sort.isPresent()) {
      SymTypeExpression type = this.tc.typeOf(node);
      // Handle enum constants
      if (type.isObjectType() && type.asObjectType().hasTypeInfo() && VariableArcMill.typeDispatcher().isOOSymbolsOOType(
        type.asObjectType().getTypeInfo()) && VariableArcMill.typeDispatcher().asOOSymbolsOOType(type.asObjectType().getTypeInfo()).isIsEnum()) {
        IOOSymbolsScope ooScope = VariableArcMill.typeDispatcher().asOOSymbolsOOType(type.asObjectType().getTypeInfo()).getSpannedScope();
        Optional<FieldSymbol> field = ooScope.resolveFieldMany(node.getName()).stream().findFirst();
        if (field.isPresent() && field.get().isIsPublic() && field.get().isIsStatic() && field.get().isIsFinal() && field.get().isIsReadOnly()) {
          this.getResult().setValue(this.getContext().mkInt(ooScope.getLocalFieldSymbols().indexOf(field.get())));
        }
      }
      if (this.getResult().getValue().isEmpty()) {
        // Handle variables
        this.getResult().setValue(this.getContext().mkConst((getPrefix().isEmpty() ? "" : getPrefix() + ".") + node.getName(), sort.get()));
      }
    }
  }
}
