/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
  public List<CompKindExpression> getSuperComponents() {
    return this.getTypeInfo().getSuperComponentsList();
  }

  @Override
  public Optional<SymTypeExpression> getTypeOfPort(@NotNull String portName) {
    Preconditions.checkNotNull(portName);
    return this.getTypeInfo()
      .getArcPort(portName, true).map(ArcPortSymbol::getType);
  }

  @Override
  public Optional<SymTypeExpression> getTypeOfParameter(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getTypeInfo().getParameter(name).map(VariableSymbol::getType);
  }

  @Override
  public List<SymTypeExpression> getParameterTypes() {
    return this.getTypeInfo().getParameters().stream().map(VariableSymbol::getType).collect(Collectors.toList());
  }

  @Override
  public List<SymTypeExpression> getTypeBindingsAsList() {
    return Collections.emptyList();
  }

  @Override
  public CompTypeExpression deepClone(@NotNull ComponentTypeSymbol compTypeSymbol) {
    return new TypeExprOfComponent(compTypeSymbol);
  }

  @Override
  public boolean deepEquals(@NotNull CompTypeExpression compSymType) {
    Preconditions.checkNotNull(compSymType);
    return this.getTypeInfo().equals(compSymType.getTypeInfo());
  }
}
