/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstantiation;
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
 * Checks that an autoconnect declaration is not present in any configuration
 * in which its component type has no subcomponent instantiations.
 */
public class AtomicNoAutoConnect4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final PresenceResolver presenceResolver;

  public AtomicNoAutoConnect4Family(@NotNull PresenceResolver presenceResolver) {
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    List<ASTArcAutoConnect> autoConnects = VariableArcElements.of(node, ASTArcAutoConnect.class);
    if (autoConnects.isEmpty()) return;

    ComponentTypeSymbol component = node.getSymbol();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr atomic = solver.not(solver.or(VariableArcElements.of(node, ASTComponentInstantiation.class).stream()
      .map(instantiation -> this.presenceResolver.presenceOf(component, instantiation))
      .toList()));
    Optional<ASTArcAutoConnect> possiblyAtomic = Optional.empty();
    for (ASTArcAutoConnect autoConnect : autoConnects) {
      switch (solver.check(solver.and(this.presenceResolver.presenceOf(component, autoConnect), atomic))) {
        case SATISFIABLE -> {
          Log.error(ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT.format(),
            autoConnect.get_SourcePositionStart(), autoConnect.get_SourcePositionEnd());
          return;
        }
        case UNKNOWN -> possiblyAtomic = possiblyAtomic.or(() -> Optional.of(autoConnect));
        case UNSATISFIABLE -> { }
      }
    }
    possiblyAtomic.ifPresent(autoConnect -> Log.warn(ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT.format(),
      autoConnect.get_SourcePositionStart(), autoConnect.get_SourcePositionEnd()));
  }
}
