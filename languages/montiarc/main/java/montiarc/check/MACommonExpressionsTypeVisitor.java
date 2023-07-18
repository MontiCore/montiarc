/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfIntersection;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class MACommonExpressionsTypeVisitor extends CommonExpressionsTypeVisitor {

  @Override
  public void endVisit(ASTCallExpression expr) {
    // most of the time the expression within the call expression
    // will be a (qualified) name of a function.
    // here, we rely on the non-separation between functions and variables
    // (in Java, we would need `::` instead of `.` to select a method)
    // but as we support function types, the difference is nigh existent
    SymTypeExpression type;
    List<SymTypeExpression> args = new ArrayList<>();
    for (int i = 0; i < expr.getArguments().sizeExpressions(); i++) {
      args.add(getType4Ast().getPartialTypeOfExpr(expr.getArguments().getExpression(i)));
    }
    Set<SymTypeExpression> inner;
    if (getType4Ast().getPartialTypeOfExpr(expr.getExpression())
      .isIntersectionType()) {
      inner = new HashSet<>(
        ((SymTypeOfIntersection) getType4Ast()
          .getPartialTypeOfExpr(expr.getExpression())
        ).getIntersectedTypeSet()
      );
    }
    else {
      inner = new HashSet<>();
      inner.add(getType4Ast().getPartialTypeOfExpr(expr.getExpression()));
    }

    // error already logged if Obscure
    if (inner.stream().allMatch(SymTypeExpression::isObscureType)) {
      type = SymTypeExpressionFactory.createObscureType();
    }
    else if (args.stream().anyMatch(SymTypeExpression::isObscureType)) {
      type = SymTypeExpressionFactory.createObscureType();
    }
    // as we call, we require a function type
    else if (inner.stream().noneMatch(SymTypeExpression::isFunctionType)) {
      Log.error("0xCDABC expression does not seem to be a function, "
          + "instead the (potential) type(s) are: "
          + inner.stream()
          .map(SymTypeExpression::printFullName)
          .collect(Collectors.joining(", ")),
        expr.get_SourcePositionStart(),
        expr.get_SourcePositionEnd()
      );
      type = SymTypeExpressionFactory.createObscureType();
    }
    else {
      Set<SymTypeOfFunction> funcs = inner.stream()
        .filter(SymTypeExpression::isFunctionType)
        .map(t -> (SymTypeOfFunction) t)
        .collect(Collectors.toSet());
      // filter out all function that do not fit the arguments
      Set<SymTypeOfFunction> callableFuncs = funcs.stream()
        .filter(f -> getTypeRel().canBeCalledWith(f, args))
        .collect(Collectors.toSet());
      if (callableFuncs.isEmpty()) {
        Log.error("0xCDABE with " + args.size() + " argument ("
            + args.stream()
            .map(SymTypeExpression::printFullName)
            .collect(Collectors.joining(", "))
            + "), no potential function can be invoked:"
            + System.lineSeparator()
            + funcs.stream()
            .map(SymTypeExpression::printFullName)
            .collect(Collectors.joining(System.lineSeparator())),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
        );
        type = SymTypeExpressionFactory.createObscureType();
      }
      else {
        // fix arity according to the arguments
        callableFuncs = callableFuncs.stream()
          .map(f -> f.getWithFixedArity(args.size()))
          .collect(Collectors.toSet());
        Optional<SymTypeOfFunction> mostSpecificFunction =
          getTypeRel().getMostSpecificFunction(callableFuncs);
        if (mostSpecificFunction.isPresent()) {
          this.getType4Ast().setTypeOfExpression(expr.getExpression(), mostSpecificFunction.get());
          type = mostSpecificFunction.get().getType().deepClone();
        }
        else {
          type = createObscureType();
        }
      }
    }

    getType4Ast().setTypeOfExpression(expr, type);
  }
}
