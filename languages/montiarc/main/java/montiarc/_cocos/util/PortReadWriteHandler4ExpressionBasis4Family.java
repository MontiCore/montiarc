/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

public class PortReadWriteHandler4ExpressionBasis4Family implements ExpressionsBasisHandler {

  protected ExpressionsBasisTraverser traverser;

  public class PortWithState {
    private PortSymbol portSymbol;
    private ContextState contextState;

    public PortWithState(PortSymbol portSymbol, ContextState contextState){
      this.portSymbol = portSymbol;
      this.contextState = contextState;
    }

    public PortSymbol getPortSymbol() {
      return portSymbol;
    }

    public ContextState getContextState() {
      return contextState;
    }
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull ExpressionsBasisTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  protected final ContextState context;

  public PortReadWriteHandler4ExpressionBasis4Family(@NotNull ContextState context) {
    this.context = Preconditions.checkNotNull(context);
  }

  public PortReadWriteHandler4ExpressionBasis4Family() {
    this(new ContextState());
  }

  private List<PortWithState> portContext = new ArrayList<>();

  protected void clearPortContext() {
    portContext.clear();
  }

  protected List<PortWithState> getPortWithContext() { return this.portContext; }

  @Override
  public void handle(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(expr.getEnclosingScope());
    Preconditions.checkArgument(expr.getEnclosingScope() instanceof IArcBasisScope);

    IArcBasisScope scope = (IArcBasisScope) expr.getEnclosingScope();

    List<VariableSymbol> ports = scope.resolveVariableMany(expr.getName(), getVariablePredicate());

    if (ports.size() == 1 && ports.get(0) instanceof Port2VariableAdapter) {
      PortSymbol port = ((Port2VariableAdapter) ports.get(0)).getAdaptee();
      var newContext = new ContextState();
      newContext.setType(context.getType());
      portContext.add(new PortWithState(port, newContext));
    }
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    return v -> true;
  }

}
