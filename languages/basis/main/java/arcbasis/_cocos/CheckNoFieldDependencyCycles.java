/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._visitor.NameCollectorVisitor;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import java.util.*;
import java.util.stream.Collectors;

public class CheckNoFieldDependencyCycles implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(ASTArcComponentType comp) {
    List<VariableSymbol> fields = comp.getSymbol().getFields();
    Map<VariableSymbol, Set<VariableSymbol>> deps = new LinkedHashMap<>();

    comp.getBody().getArcElementList().stream()
      .filter(ASTArcFieldDeclaration.class::isInstance)
      .map(ASTArcFieldDeclaration.class::cast)
      .forEach(fd -> {
        for (ASTArcField field : fd.getArcFieldList()) {
          ASTExpression init = field.getInitial();
          Set<String> usedNames = extractNames(init);
          Set<VariableSymbol> usedFields = fields.stream()
            .filter(sym -> usedNames.contains(sym.getName()))
            .collect(Collectors.toSet());
          deps.put(field.getSymbol(), usedFields);
        }
      });

    Map<VariableSymbol, Integer> inDegree = new LinkedHashMap<>();
    for (VariableSymbol f : fields) {
      inDegree.put(f, deps.getOrDefault(f, Collections.emptySet()).size());
    }
    Deque<VariableSymbol> queue = new ArrayDeque<>();
    inDegree.forEach((f, deg) -> { if (deg == 0) queue.add(f); });

    int removed = 0;
    while (!queue.isEmpty()) {
      VariableSymbol u = queue.remove();
      removed++;
      // for every field that depends on u, decrease its in-degree
      for (Map.Entry<VariableSymbol, Set<VariableSymbol>> e : deps.entrySet()) {
        if (e.getValue().contains(u)) {
          int d2 = inDegree.get(e.getKey()) - 1;
          inDegree.put(e.getKey(), d2);
          if (d2 == 0) {
            queue.add(e.getKey());
          }
        }
      }
    }

    if (removed < fields.size()) {
      String circularVars = inDegree.entrySet().stream()
        .filter(e -> e.getValue() > 0)
        .map(e -> e.getKey().getName())
        .collect(Collectors.joining(", "));

      Log.error(ArcError.CIRCULAR_FIELDS_DEPENDENCY.format(circularVars),
        comp.get_SourcePositionStart());
    }
  }

  /** Builds the set of simple names referenced anywhere inside a (possibly null) init-expr. */
  private Set<String> extractNames(ASTExpression expr) {
    if (expr == null) {
      return Collections.emptySet();
    }
    ExpressionsBasisTraverser traverser = ExpressionsBasisMill.traverser();
    NameCollectorVisitor visitor = new NameCollectorVisitor();
    traverser.add4ExpressionsBasis(visitor);
    expr.accept(traverser);
    return visitor.getNames();
  }
}
