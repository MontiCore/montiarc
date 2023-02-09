/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Represents component types that are solely defined by their type symbol. I.e. they, for example, are not generic and
 * do not feature type parameters.
 */
public class TypeExprOfComponent extends CompTypeExpression {

  public TypeExprOfComponent(@NotNull ComponentTypeSymbol compTypeSymbol) {
    super(compTypeSymbol);
  }

  @Override
  public String printName() {
    return this.getTypeInfo().getName();
  }

  @Override
  public String printFullName() {
    return this.getTypeInfo().getFullName();
  }

  @Override
  public Optional<CompTypeExpression> getParentTypeExpr() {
    if (this.getTypeInfo().isPresentParent()) {
      return Optional.of(this.getTypeInfo().getParent());
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<SymTypeExpression> getTypeExprOfPort(@NotNull String portName) {
    Preconditions.checkNotNull(portName);
    return this.getTypeInfo()
      .getPort(portName, true).map(PortSymbol::getType);
  }

  @Override
  public Optional<SymTypeExpression> getTypeExprOfParameter(@NotNull String parameterName) {
    Preconditions.checkNotNull(parameterName);
    return this.getTypeInfo()
      .getParameter(parameterName).map(VariableSymbolTOP::getType);
  }

  @Override
  public CompTypeExpression deepClone() {
    return new TypeExprOfComponent(this.getTypeInfo());
  }

  @Override
  public boolean deepEquals(@NotNull CompTypeExpression compSymType) {
    Preconditions.checkNotNull(compSymType);
    return this.getTypeInfo().equals(compSymType.getTypeInfo());
  }
}
