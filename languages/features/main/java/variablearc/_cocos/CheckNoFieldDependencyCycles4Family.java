/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._visitor.NameCollectorVisitor;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that the initial values of the fields of a component type do not
 * depend on each other cyclically in any configuration, i.e., that no cycle of
 * fields whose initial values reference each other is present together.
 */
public class CheckNoFieldDependencyCycles4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final PresenceResolver presenceResolver;

  public CheckNoFieldDependencyCycles4Family(@NotNull PresenceResolver presenceResolver) {
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    List<ASTArcField> fields = VariableArcElements.of(node, ASTArcFieldDeclaration.class).stream()
      .flatMap(declaration -> declaration.getArcFieldList().stream())
      .filter(ASTArcField::isPresentSymbol)
      .toList();
    List<List<Integer>> dependencies = new ArrayList<>();
    for (ASTArcField field : fields) {
      Set<String> names = this.namesOf(field);
      List<Integer> used = new ArrayList<>();
      for (int i = 0; i < fields.size(); i++) {
        if (names.contains(fields.get(i).getName())) {
          used.add(i);
        }
      }
      dependencies.add(used);
    }

    List<List<Integer>> cycles = new ArrayList<>();
    for (int start = 0; start < fields.size(); start++) {
      this.collectCycles(dependencies, start, start, new ArrayList<>(List.of(start)), cycles);
    }
    if (cycles.isEmpty()) return;

    Optional<String> possibleCycle = Optional.empty();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    for (List<Integer> cycle : cycles) {
      BoolExpr present = solver.and(cycle.stream()
        .map(i -> this.presenceResolver.presenceOf(component, fields.get(i).getSymbol()))
        .toList());
      String message = ArcError.CIRCULAR_FIELDS_DEPENDENCY.format(cycle.stream()
        .sorted()
        .map(i -> fields.get(i).getName())
        .collect(Collectors.joining(", ")));
      switch (solver.check(present)) {
        case SATISFIABLE -> {
          Log.error(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
          return;
        }
        case UNKNOWN -> possibleCycle = possibleCycle.or(() -> Optional.of(message));
        case UNSATISFIABLE -> { }
      }
    }
    possibleCycle.ifPresent(message -> Log.warn(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd()));
  }

  /**
   * Collects the simple cycles through the start field whose other fields
   * come after it, such that every cycle is collected once.
   */
  protected void collectCycles(@NotNull List<List<Integer>> dependencies,
                               int start,
                               int current,
                               @NotNull List<Integer> path,
                               @NotNull List<List<Integer>> cycles) {
    for (int next : dependencies.get(current)) {
      if (next == start) {
        cycles.add(List.copyOf(path));
      } else if (next > start && !path.contains(next)) {
        path.add(next);
        this.collectCycles(dependencies, start, next, path, cycles);
        path.remove(path.size() - 1);
      }
    }
  }

  protected Set<String> namesOf(@NotNull ASTArcField field) {
    ExpressionsBasisTraverser traverser = ExpressionsBasisMill.traverser();
    NameCollectorVisitor visitor = new NameCollectorVisitor();
    traverser.add4ExpressionsBasis(visitor);
    field.getInitial().accept(traverser);
    return visitor.getNames();
  }
}
