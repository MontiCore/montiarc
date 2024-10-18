/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.codegen.visitors.SetExpressionsPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.expressions.AssignmentExpression;
import variablearc.evaluation.expressions.Expression;
import variablearc.evaluation.expressions.NegatedExpression;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class MA2JSimJavaPrinter extends MontiArcFullPrettyPrinter {

  public MA2JSimJavaPrinter() {
    this(new IndentPrinter());
  }

  public MA2JSimJavaPrinter(@Nullable ComponentTypeSymbol currentVariant) {
    this(new MontiArcTypeCalculator(), new IndentPrinter(), true, currentVariant);
  }

  public MA2JSimJavaPrinter(@NotNull IndentPrinter printer) {
    this(new MontiArcTypeCalculator(), Preconditions.checkNotNull(printer), true, null);
  }

  public MA2JSimJavaPrinter(@NotNull IArcTypeCalculator tc, @NotNull IndentPrinter printer, boolean printComments, @Nullable ComponentTypeSymbol currentVariant) {
    super(Preconditions.checkNotNull(printer), printComments);

    CommonExpressionsJavaPrinter commonExpressionsJavaPrinter = new CommonExpressionsJavaPrinter(tc, printer, printComments);
    this.traverser.setCommonExpressionsHandler(commonExpressionsJavaPrinter);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(commonExpressionsJavaPrinter);

    ExpressionsBasisJavaPrinter expressionsBasisJavaPrinter = new ExpressionsBasisJavaPrinter(printer, printComments);
    this.traverser.setExpressionsBasisHandler(expressionsBasisJavaPrinter);
    this.traverser.getExpressionsBasisVisitorList().clear();
    this.traverser.add4ExpressionsBasis(expressionsBasisJavaPrinter);

    MCBasicTypesJavaPrinter mcBasicTypesJavaPrinter = new MCBasicTypesJavaPrinter(printer, printComments);
    this.traverser.setMCBasicTypesHandler(mcBasicTypesJavaPrinter);
    this.traverser.getMCBasicTypesVisitorList().clear();
    this.traverser.add4MCBasicTypes(mcBasicTypesJavaPrinter);

    MCSimpleGenericTypesJavaPrinter mcSimpleGenericTypesJavaPrinter = new MCSimpleGenericTypesJavaPrinter(printer, printComments);
    this.traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesJavaPrinter);
    this.traverser.getMCSimpleGenericTypesVisitorList().clear();
    this.traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesJavaPrinter);

    MCCollectionTypesJavaPrinter mcCollectionTypesJavaPrinter = new MCCollectionTypesJavaPrinter(printer, printComments);
    this.traverser.setMCCollectionTypesHandler(mcCollectionTypesJavaPrinter);
    this.traverser.getMCCollectionTypesVisitorList().clear();
    this.traverser.add4MCCollectionTypes(mcCollectionTypesJavaPrinter);

    AssignmentExpressionsMA2JSimPrinter assignmentExpressionsPrinter =
      new AssignmentExpressionsMA2JSimPrinter(printer, printComments, currentVariant);
    this.traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrinter);
    this.traverser.getAssignmentExpressionsVisitorList().clear();
    this.traverser.add4AssignmentExpressions(assignmentExpressionsPrinter);

    SetExpressionsPrinter setExpressionsPrinter = new SetExpressionsPrinter(printer, new VariableNaming(), new MontiArcTypeCalculator(), new MontiArcTypeCalculator());
    this.traverser.setSetExpressionsHandler(setExpressionsPrinter);
    this.traverser.getSetExpressionsVisitorList().clear();
    this.traverser.add4SetExpressions(setExpressionsPrinter);

    MCCommonStatementsJavaPrinter mcCommonStatementsPrinter = new MCCommonStatementsJavaPrinter(printer, printComments);
    this.traverser.setMCCommonStatementsHandler(mcCommonStatementsPrinter);
    this.traverser.getMCCommonStatementsVisitorList().clear();
    this.traverser.add4MCCommonStatements(mcCommonStatementsPrinter);
  }

  public String prettyprint(List<Expression> expressions) {
    StringBuilder prettyprinted = new StringBuilder();
    Iterator<Expression> iterator = expressions.iterator();
    while (iterator.hasNext()) {
      Expression expression = iterator.next();
      prettyprinted.append("(").append(prettyprint(expression)).append(")");
      if (iterator.hasNext()) prettyprinted.append("&&");
    }
    return prettyprinted.toString();
  }

  public String prettyprintCondition(VariableArcVariantComponentTypeSymbol variant) {
    ExpressionSet conditions = variant.getLocalConditions();
    StringBuilder prettyprinted = new StringBuilder();
    if (!conditions.getExpressions().isEmpty()) {
      prettyprinted.append(prettyprint(conditions.getExpressions()));
    }

    Iterator<String> iterator = conditions.getNegatedConjunctions().stream().map(this::prettyprint).iterator();
    if (!conditions.getExpressions().isEmpty() && iterator.hasNext()) {
      prettyprinted.append(" && ");
    }
    if (iterator.hasNext()) {
      prettyprinted.append("!(");
      while (iterator.hasNext()) {
        String expression = iterator.next();
        prettyprinted.append("(").append(expression).append(")");
        if (iterator.hasNext()) prettyprinted.append("||");
      }
      prettyprinted.append(")");
    }

    return prettyprinted.toString().isEmpty() ? "true" : prettyprinted.toString();
  }

  public String prettyprint(Expression expression) {
    if (expression instanceof AssignmentExpression) {
      return ((AssignmentExpression) expression).getVariable().getName() + " = " + this.prettyprint(expression.getAstExpression());
    } else if (expression instanceof NegatedExpression) {
      return "!(" + this.prettyprint(expression.getAstExpression()) + ")";
    }

    return this.prettyprint(expression.getAstExpression());
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
      StringBuilder r = new StringBuilder(expression.asGenericType().getTypeConstructorFullName()).append('<');
      for (int i = 0; i < expression.asGenericType().getArgumentList().size(); i++) {
        r.append(prettyprint(expression.asGenericType().getArgument(i), boxPrimitives));
        if (i < expression.asGenericType().getArgumentList().size() - 1) {
          r.append(',');
        }
      }
      return r.append('>').toString();
    }

    return expression.printFullName();
  }
}
