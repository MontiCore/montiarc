/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.Optional;

/**
 * Variability aware extension of {@link VarDeclarationNameAlreadyDefinedInScope}:
 * a variable declaration conflicts with a variable of the same name only in
 * the configurations in which both are present.
 */
public class VarDeclarationNameAlreadyDefinedInScope4Family extends VarDeclarationNameAlreadyDefinedInScope {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public VarDeclarationNameAlreadyDefinedInScope4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                                        @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTVariableDeclarator node) {
    Preconditions.checkNotNull(node);
    Optional<ComponentTypeSymbol> component = this.withinComponentResolver.enclosingComponentOf(node.getEnclosingScope());
    if (!node.getDeclarator().isPresentSymbol() || component.isEmpty()) {
      super.check(node);
      return;
    }

    VariableSymbol declared = node.getDeclarator().getSymbol();
    List<VariableSymbol> others = this.withinComponentResolver.getAll(node.getEnclosingScope(), VariableSymbol.class).stream()
      .filter(variable -> variable != declared && variable.getName().equals(declared.getName()))
      .filter(variable -> this.withinComponentResolver.superPathTo(component.get(), variable).map(List::isEmpty).orElse(false))
      .filter(variable -> this.precedes(variable, declared))
      .toList();
    if (others.isEmpty()) return;

    FormulaSolver solver = this.presenceResolver.solverFor(component.get());
    BoolExpr conflict = solver.and(this.presenceResolver.presenceOf(component.get(), declared),
      solver.or(others.stream().map(other -> this.presenceResolver.presenceOf(component.get(), other)).toList()));
    String message = ERROR_CODE + " " + String.format(ERROR_MSG_FORMAT, declared.getName());
    switch (solver.check(conflict)) {
      case SATISFIABLE -> Log.error(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }

  /**
   * @return whether the variable is declared in an enclosing scope of the
   * declared one or before it in the same scope, such that, as by
   * {@link VarDeclarationNameAlreadyDefinedInScope}, only the later of two
   * declarations in the same scope is reported
   */
  protected boolean precedes(@NotNull VariableSymbol variable, @NotNull VariableSymbol declared) {
    return variable.getEnclosingScope() != declared.getEnclosingScope()
      || !variable.isPresentAstNode() || !declared.isPresentAstNode()
      || variable.getAstNode().get_SourcePositionStart().compareTo(declared.getAstNode().get_SourcePositionStart()) < 0;
  }
}
