/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks that the identifiers of the elements declared by a component type
 * (inner component types, subcomponents, ports, fields, parameters, type
 * parameters, and features) are unique in every configuration, i.e., that no
 * two elements of the same name are present together.
 */
public class UniqueIdentifier4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public UniqueIdentifier4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                 @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    Map<String, List<ISymbol>> byName = new LinkedHashMap<>();
    for (ISymbol element : this.getElements(component)) {
      byName.computeIfAbsent(element.getName(), n -> new ArrayList<>()).add(element);
    }

    FormulaSolver solver = this.presenceResolver.solverFor(component);
    for (Map.Entry<String, List<ISymbol>> entry : byName.entrySet()) {
      if (entry.getValue().size() < 2) continue;
      String message = ArcError.UNIQUE_IDENTIFIER_NAMES.format(entry.getKey());
      switch (solver.check(this.conflict(solver, component, entry.getValue()))) {
        case SATISFIABLE -> Log.error(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        case UNKNOWN -> Log.warn(message, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        case UNSATISFIABLE -> { }
      }
    }
  }

  /**
   * @return the condition under which at least two of the elements are present
   */
  protected BoolExpr conflict(@NotNull FormulaSolver solver,
                              @NotNull ComponentTypeSymbol component,
                              @NotNull List<ISymbol> elements) {
    List<BoolExpr> presences = elements.stream().map(element -> this.presenceOf(solver, component, element)).toList();
    List<BoolExpr> pairs = new ArrayList<>();
    for (int i = 0; i < presences.size(); i++) {
      for (int j = i + 1; j < presences.size(); j++) {
        pairs.add(solver.and(presences.get(i), presences.get(j)));
      }
    }
    return solver.or(pairs);
  }

  protected BoolExpr presenceOf(@NotNull FormulaSolver solver,
                                @NotNull ComponentTypeSymbol component,
                                @NotNull ISymbol element) {
    return element.isPresentAstNode() ? this.presenceResolver.presenceOf(component, element.getAstNode())
      : this.presenceResolver.presenceOf(component);
  }

  protected List<ISymbol> getElements(@NotNull ComponentTypeSymbol component) {
    List<ISymbol> elements = new ArrayList<>();
    elements.addAll(component.getSpannedScope().getLocalComponentTypeSymbols());
    elements.addAll(component.getSubcomponents());
    elements.addAll(component.getPorts());
    elements.addAll(component.getFields());
    elements.addAll(component.getParameterList());
    elements.addAll(component.getTypeParameters());
    if (component.getSpannedScope() instanceof IVariableArcScope scope) {
      elements.addAll(scope.getLocalArcFeatureSymbols());
    }
    return elements;
  }
}
