/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Names;

/**
 * This context can be used to store and share a state across visitors during generation.
 */
public class SymTypeExpressionJavaPrinter {

  public String prettyprint(SymTypeExpression expression) {
    return this.prettyprint(expression, false);
  }

  public String prettyprint(SymTypeExpression expression, boolean boxPrimitives) {
    if (expression.isPrimitive()) {
      if (boxPrimitives) {
        return expression.asPrimitive().getBoxedPrimitiveName();
      } else {
        return expression.asPrimitive().getPrimitiveName();
      }
    }
    if (expression.isTypeVariable()) {
      return expression.print();
    }
    if (expression.isGenericType()) {
      String name = expression.asGenericType().getTypeConstructorFullName();
      switch (name) {
        case "EventStream.EventStream":
        case "Stream.Stream":
        case "SyncStream.SyncStream":
        case "ToptStream.ToptStream":
        case "UntimedStream.UntimedStream":
          name = "de.monticore.rte.streams." + Names.getSimpleName(name);
          break;
      }
      StringBuilder r = new StringBuilder(name).append('<');
      for (int i = 0; i < expression.asGenericType().getArgumentList().size(); i++) {
        r.append(prettyprint(expression.asGenericType().getArgument(i), true));
        if (i < expression.asGenericType().getArgumentList().size() - 1) {
          r.append(',');
        }
      }
      return r.append('>').toString();
    }

    return expression.printFullName();
  }
}
