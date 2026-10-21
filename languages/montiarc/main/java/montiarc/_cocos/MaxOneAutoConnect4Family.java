/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import comfortablearc._ast.ASTArcAutoConnect;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ComfortableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.Optional;

/**
 * Checks that no two autoconnect declarations of a component type are present
 * together in any configuration.
 */
public class MaxOneAutoConnect4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final PresenceResolver presenceResolver;

  public MaxOneAutoConnect4Family(@NotNull PresenceResolver presenceResolver) {
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    List<ASTArcAutoConnect> autoConnects = VariableArcElements.of(node, ASTArcAutoConnect.class);
    if (autoConnects.size() < 2) return;

    ComponentTypeSymbol component = node.getSymbol();
    String message = ComfortableArcError.MULTIPLE_AUTOCONNECTS.format(autoConnects.size());
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    Optional<ASTArcAutoConnect> possiblyMultiple = Optional.empty();
    for (int later = 1; later < autoConnects.size(); later++) {
      ASTArcAutoConnect autoConnect = autoConnects.get(later);
      BoolExpr earlier = solver.or(autoConnects.subList(0, later).stream()
        .map(other -> this.presenceResolver.presenceOf(component, other))
        .toList());
      switch (solver.check(solver.and(this.presenceResolver.presenceOf(component, autoConnect), earlier))) {
        case SATISFIABLE -> {
          Log.error(message, autoConnect.get_SourcePositionStart(), autoConnect.get_SourcePositionEnd());
          return;
        }
        case UNKNOWN -> possiblyMultiple = possiblyMultiple.or(() -> Optional.of(autoConnect));
        case UNSATISFIABLE -> { }
      }
    }
    possiblyMultiple.ifPresent(autoConnect ->
      Log.warn(message, autoConnect.get_SourcePositionStart(), autoConnect.get_SourcePositionEnd()));
  }
}
