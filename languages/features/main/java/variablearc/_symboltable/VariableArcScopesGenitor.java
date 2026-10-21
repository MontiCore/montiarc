/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcParameter;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.presence.LocalPresenceCalculator;

import java.util.Optional;
import java.util.Stack;

public class VariableArcScopesGenitor extends VariableArcScopesGenitorTOP
  implements arcbasis._visitor.ArcBasisVisitor2 {

  protected Stack<IVariableArcComponentTypeSymbol> componentStack = new Stack<>();
  protected LocalPresenceCalculator localPresenceCalculator = new LocalPresenceCalculator();

  protected Stack<IVariableArcComponentTypeSymbol> getComponentStack() {
    return this.componentStack;
  }

  protected Optional<IVariableArcComponentTypeSymbol> getCurrentComponent() {
    if (!this.getComponentStack().isEmpty()) {
      return Optional.ofNullable(this.getComponentStack().peek());
    }
    return Optional.empty();
  }

  protected void removeCurrentComponent() {
    this.getComponentStack().pop();
  }

  protected void putOnStack(@Nullable IVariableArcComponentTypeSymbol symbol) {
    this.getComponentStack().push(symbol);
  }

  @Override
  public void visit(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    this.putOnStack((IVariableArcComponentTypeSymbol) node.getSymbol());
  }

  @Override
  public void endVisit(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    Preconditions.checkState(this.getCurrentComponent().get().getTypeInfo().isPresentAstNode());
    Preconditions.checkState(this.getCurrentComponent().get().getTypeInfo().getAstNode().equals(node));
    IVariableArcComponentTypeSymbol component = this.getCurrentComponent().get();
    node.getHead().streamArcParameters()
      .filter(ASTArcParameter::isPresentDefault)
      .forEach(parameter -> component.setParameterDefault(parameter.getName(), parameter.getDefault()));
    this.removeCurrentComponent();
    if (this.getComponentStack().isEmpty()) {
      this.localPresenceCalculator.calculate(node);
    }
  }
}
