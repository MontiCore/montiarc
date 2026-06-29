/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.codegen.TraverserBasedCodeGenerator;
import de.monticore.codegen.javagen.JavaGenSymTypeExpressionConverter;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.JavaOperationPrinter;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.expressions.expressionsbasis.codegen.javagen.ExpressionsBasisJavaGenVisitor;
import de.monticore.literals.mccommonliterals.codegen.javagen.MCCommonLiteralsJavaGenVisitor;
import de.monticore.ocl.setexpressions.codegen.javagen.SetExpressionsJavaGenVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.visitor.ITraverser;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import montiarc.generator.codegen.javagen.BitExpressionsJavaPrinter;
import montiarc.generator.codegen.javagen.MAAssignmentExpressionsJavaGenVisitor;
import montiarc.generator.codegen.javagen.MACommonExpressionsJavaGenVisitor;
import montiarc.generator.codegen.javagen.MCBasicTypesJavaPrinter;
import montiarc.generator.codegen.javagen.MCCollectionTypesJavaPrinter;
import montiarc.generator.codegen.javagen.MCCommonStatementsJavaPrinter;
import montiarc.generator.codegen.javagen.MCSimpleGenericTypesJavaPrinter;
import montiarc.generator.codegen.javagen.MCVarDeclarationStatementsJavaPrinter;
import montiarc.generator.codegen.javagen.StreamExpressionsJavaPrinter;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.expressions.AssignmentExpression;
import variablearc.evaluation.expressions.Expression;
import variablearc.evaluation.expressions.NegatedExpression;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class MA2JSimJavaGenerator implements TraverserBasedCodeGenerator {

  protected MontiArcTraverser traverser;

  protected JavaGenVisitorState state;

  public MA2JSimJavaGenerator() {
    init(null);
  }

  public MA2JSimJavaGenerator(@Nullable ComponentTypeSymbol currentVariant) {
    init(currentVariant);
  }

  public void init(@Nullable ComponentTypeSymbol currentVariant) {
    JavaGenSymTypeExpressionConverter.init();
    JavaOperationPrinter.init();
    SymTypeExpression2JavaConverter.init();

    this.state = new JavaGenVisitorState(new IndentPrinter());
    this.traverser = MontiArcMill.inheritanceTraverser();
    boolean printComments = true;

    // Types

    MCBasicTypesJavaPrinter mcBasicTypesJavaPrinter = new MCBasicTypesJavaPrinter(getPrinter(), printComments);
    this.traverser.setMCBasicTypesHandler(mcBasicTypesJavaPrinter);
    this.traverser.getMCBasicTypesVisitorList().clear();
    this.traverser.add4MCBasicTypes(mcBasicTypesJavaPrinter);

    MCSimpleGenericTypesJavaPrinter mcSimpleGenericTypesJavaPrinter = new MCSimpleGenericTypesJavaPrinter(getPrinter(), printComments);
    this.traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesJavaPrinter);
    this.traverser.getMCSimpleGenericTypesVisitorList().clear();
    this.traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesJavaPrinter);

    MCCollectionTypesJavaPrinter mcCollectionTypesJavaPrinter = new MCCollectionTypesJavaPrinter(getPrinter(), printComments);
    this.traverser.setMCCollectionTypesHandler(mcCollectionTypesJavaPrinter);
    this.traverser.getMCCollectionTypesVisitorList().clear();
    this.traverser.add4MCCollectionTypes(mcCollectionTypesJavaPrinter);

    // Literals

    MCCommonLiteralsJavaGenVisitor visMCCommonLiterals =
      new MCCommonLiteralsJavaGenVisitor(state);
    traverser.setMCCommonLiteralsHandler(visMCCommonLiterals);

    // Expressions

    MAAssignmentExpressionsJavaGenVisitor visAssignmentExpressions =
      new MAAssignmentExpressionsJavaGenVisitor(state, currentVariant);
    traverser.setAssignmentExpressionsHandler(visAssignmentExpressions);

    BitExpressionsJavaPrinter bitExpressions = new BitExpressionsJavaPrinter(getPrinter());
    this.traverser.setBitExpressionsHandler(bitExpressions);
    this.traverser.add4BitExpressions(bitExpressions);

    MACommonExpressionsJavaGenVisitor visCommonExpressions =
      new MACommonExpressionsJavaGenVisitor(state);
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    ExpressionsBasisJavaGenVisitor visExpressionBasis =
      new ExpressionsBasisJavaGenVisitor(state);
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    // Statements

    SetExpressionsJavaGenVisitor setExpressionsPrinter = new SetExpressionsJavaGenVisitor(state);
    this.traverser.setSetExpressionsHandler(setExpressionsPrinter);

    MCCommonStatementsJavaPrinter mcCommonStatementsPrinter = new MCCommonStatementsJavaPrinter(getPrinter(), printComments);
    this.traverser.setMCCommonStatementsHandler(mcCommonStatementsPrinter);
    this.traverser.getMCCommonStatementsVisitorList().clear();
    this.traverser.add4MCCommonStatements(mcCommonStatementsPrinter);

    MCVarDeclarationStatementsJavaPrinter mcVarDeclarationStatementsJavaPrinter = new MCVarDeclarationStatementsJavaPrinter(getPrinter(), printComments);
    this.traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatementsJavaPrinter);
    this.traverser.getMCVarDeclarationStatementsVisitorList().clear();
    this.traverser.add4MCVarDeclarationStatements(mcVarDeclarationStatementsJavaPrinter);

    StreamExpressionsJavaPrinter streamExpressionsJavaPrinter = new StreamExpressionsJavaPrinter(getPrinter(), printComments);
    this.traverser.setStreamExpressionsHandler(streamExpressionsJavaPrinter);
    this.traverser.getStreamExpressionsVisitorList().clear();
    this.traverser.add4StreamExpressions(streamExpressionsJavaPrinter);

    // Fallback pretty printer

    de.monticore.scdoactions._prettyprint.SCDoActionsPrettyPrinter sCDoActions = new de.monticore.scdoactions._prettyprint.SCDoActionsPrettyPrinter(getPrinter(), printComments);
    this.traverser.setSCDoActionsHandler(sCDoActions);
    this.traverser.add4SCDoActions(sCDoActions);
    de.monticore.literals.mcliteralsbasis._prettyprint.MCLiteralsBasisPrettyPrinter mCLiteralsBasis = new de.monticore.literals.mcliteralsbasis._prettyprint.MCLiteralsBasisPrettyPrinter(getPrinter(), printComments);
    this.traverser.setMCLiteralsBasisHandler(mCLiteralsBasis);
    this.traverser.add4MCLiteralsBasis(mCLiteralsBasis);
    de.monticore.statements.mcstatementsbasis._prettyprint.MCStatementsBasisPrettyPrinter mCStatementsBasis = new de.monticore.statements.mcstatementsbasis._prettyprint.MCStatementsBasisPrettyPrinter(getPrinter(), printComments);
    this.traverser.setMCStatementsBasisHandler(mCStatementsBasis);
    this.traverser.add4MCStatementsBasis(mCStatementsBasis);
    de.monticore.symbols.oosymbols._prettyprint.OOSymbolsPrettyPrinter oOSymbols = new de.monticore.symbols.oosymbols._prettyprint.OOSymbolsPrettyPrinter(getPrinter(), printComments);
    this.traverser.setOOSymbolsHandler(oOSymbols);
    this.traverser.add4OOSymbols(oOSymbols);
    de.monticore.symbols.basicsymbols._prettyprint.BasicSymbolsPrettyPrinter basicSymbols = new de.monticore.symbols.basicsymbols._prettyprint.BasicSymbolsPrettyPrinter(getPrinter(), printComments);
    this.traverser.setBasicSymbolsHandler(basicSymbols);
    this.traverser.add4BasicSymbols(basicSymbols);
  }

  public String generateCode(List<Expression> expressions) {
    StringBuilder prettyprinted = new StringBuilder();
    Iterator<Expression> iterator = expressions.iterator();
    while (iterator.hasNext()) {
      Expression expression = iterator.next();
      prettyprinted.append("(").append(generateCode(expression)).append(")");
      if (iterator.hasNext()) prettyprinted.append("&&");
    }
    return prettyprinted.toString();
  }

  public String generateCodeCondition(VariableArcVariantComponentTypeSymbol variant) {
    ExpressionSet conditions = variant.getLocalConditions();
    StringBuilder prettyprinted = new StringBuilder();
    if (!conditions.getExpressions().isEmpty()) {
      prettyprinted.append(generateCode(conditions.getExpressions()));
    }

    Iterator<String> iterator = conditions.getNegatedConjunctions().stream().map(this::generateCode).iterator();
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

  public String generateCode(Expression expression) {
    if (expression instanceof AssignmentExpression) {
      return ((AssignmentExpression) expression).getVariable().getName() + " = " + this.generateCode(expression.getAstExpression());
    } else if (expression instanceof NegatedExpression) {
      return "!(" + this.generateCode(expression.getAstExpression()) + ")";
    }

    return this.generateCode(expression.getAstExpression());
  }

  public String generateCode(SymTypeExpression expression, boolean boxPrimitives) {
    if (boxPrimitives) {
      return SymTypeExpression2JavaConverter.getBoxedJavaTypePrint(expression);
    }
    return SymTypeExpression2JavaConverter.getJavaTypePrint(expression);
  }

  @Override
  public IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public ITraverser getTraverser() {
    return traverser;
  }

  public JavaGenVisitorState getSharedState() {
    return state;
  }
}
