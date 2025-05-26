/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcFieldDeclaration;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;

import java.lang.reflect.Method;
import java.util.*;

public class CheckNoFieldDependencyCycles implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(ASTArcComponentType comp) {
    Map<String, Set<String>> deps = new LinkedHashMap<>();

    comp.getBody().getArcElementList().stream()
      .filter(ASTArcFieldDeclaration.class::isInstance)
      .map(ASTArcFieldDeclaration.class::cast)
      .forEach(fd -> {
        fd.getArcFieldList().forEach(field -> {
          ASTExpression init = field.getInitial();
          if (init != null) {
            deps.put(field.getName(), extractNames(init));
          }
        });
      });

    boolean cycle = deps.keySet().stream()
      .anyMatch(start -> hasCycle(start, deps, new HashSet<>()));

    if (cycle) {
      Log.error(ArcError.CIRCULAR_FIELDS_DEPENDENCY.format(),
        comp.get_SourcePositionStart());
    }
  }

  private Set<String> extractNames(ASTExpression expr) {
    Set<String> names = new HashSet<>();
    collectNames(expr, names);
    return names;
  }

  private void collectNames(ASTExpression expr, Set<String> names) {
    if (expr instanceof ASTNameExpression) {
      names.add(((ASTNameExpression) expr).getName());
    }

    for (Method m : expr.getClass().getMethods()) {
      if (m.getParameterCount() != 0 || !m.getName().startsWith("get")) {
        continue;
      }

      String attr = m.getName().substring(3);
      try {
        Method isPresent = expr.getClass().getMethod("isPresent" + attr);
        Boolean present = (Boolean) isPresent.invoke(expr);
        if (!present) {
          continue;
        }
      } catch (NoSuchMethodException ignored) {
      } catch (Exception e) {
        continue;
      }

      Object child;
      try {
        child = m.invoke(expr);
      } catch (Exception e) {
        continue;
      }

      if (child instanceof Optional<?>) {
        Optional<?> opt = (Optional<?>) child;
        if (opt.isEmpty()) {
          continue;
        }
        Object val = opt.get();
        if (val instanceof ASTExpression) {
          collectNames((ASTExpression) val, names);
          continue;
        }
        if (val instanceof Collection<?>) {
          for (Object o : (Collection<?>) val) {
            if (o instanceof ASTExpression) {
              collectNames((ASTExpression) o, names);
            }
          }
          continue;
        }
      }

      if (child instanceof ASTExpression) {
        collectNames((ASTExpression) child, names);
      } else if (child instanceof Collection<?>) {
        for (Object o : (Collection<?>) child) {
          if (o instanceof ASTExpression) {
            collectNames((ASTExpression) o, names);
          }
        }
      }
    }
  }

  private boolean hasCycle(String current,
                           Map<String, Set<String>> deps,
                           Set<String> visited) {
    Set<String> next = deps.get(current);
    if (next == null) {
      return false;
    }
    for (String nxt : next) {
      if (visited.contains(nxt)) {
        return true;
      }
      Set<String> nextVisited = new HashSet<>(visited);
      nextVisited.add(current);
      if (hasCycle(nxt, deps, nextVisited)) {
        return true;
      }
    }
    return false;
  }
}
