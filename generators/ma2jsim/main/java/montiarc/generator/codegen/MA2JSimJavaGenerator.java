/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.codegen.CodeGenSymTypeExpressionConverter;
import de.monticore.codegen.TraverserBasedCodeGenerator;
import de.monticore.codegen.javagen.JavaGenSymTypeExpressionConverter;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.JavaOperationPrinter;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis.codegen.javagen.ExpressionsBasisJavaGenVisitor;
import de.monticore.expressions.tupleexpressions.codegen.javagen.TupleExpressionsJavaGenVisitor;
import de.monticore.literals.mccommonliterals.codegen.javagen.MCCommonLiteralsJavaGenVisitor;
import de.monticore.ocl.setexpressions.codegen.javagen.SetExpressionsJavaGenVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes.codegen.javagen.MCBasicTypesJavaGenVisitor;
import de.monticore.types3.TypeCheck3;
import de.monticore.visitor.ITraverser;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import montiarc.generator.codegen.javagen.BitExpressionsJavaPrinter;
import montiarc.generator.codegen.javagen.MAAssignmentExpressionsJavaGenVisitor;
import montiarc.generator.codegen.javagen.MACommonExpressionsJavaGenVisitor;
import montiarc.generator.codegen.javagen.MACommonStatementsJavaGenVisitor;
import montiarc.generator.codegen.javagen.MAVarDeclarationStatementsJavaGenVisitor;
import montiarc.generator.codegen.javagen.StreamExpressionsJavaPrinter;


@SuppressWarnings("unused")
public class MA2JSimJavaGenerator implements TraverserBasedCodeGenerator {

  protected MontiArcTraverser traverser;

  protected JavaGenVisitorState state;

  public MA2JSimJavaGenerator() {
    init();
  }

  public void init() {
    JavaGenSymTypeExpressionConverter.init();
    JavaOperationPrinter.init();
    SymTypeExpression2JavaConverter.init();

    this.state = new JavaGenVisitorState(new IndentPrinter());
    this.traverser = MontiArcMill.inheritanceTraverser();
    boolean printComments = true;

    // Types

    MCBasicTypesJavaGenVisitor visMCBasicTypes = new MCBasicTypesJavaGenVisitor(state);
    traverser.setMCBasicTypesHandler(visMCBasicTypes);
    traverser.add4MCBasicTypes(visMCBasicTypes);

    // Literals

    MCCommonLiteralsJavaGenVisitor visMCCommonLiterals =
      new MCCommonLiteralsJavaGenVisitor(state);
    traverser.setMCCommonLiteralsHandler(visMCCommonLiterals);

    // Expressions

    MAAssignmentExpressionsJavaGenVisitor visAssignmentExpressions =
      new MAAssignmentExpressionsJavaGenVisitor(state);
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

    TupleExpressionsJavaGenVisitor visTupleExpressions =
      new TupleExpressionsJavaGenVisitor(state);
    traverser.setTupleExpressionsHandler(visTupleExpressions);

    StreamExpressionsJavaPrinter streamExpressionsJavaPrinter = new StreamExpressionsJavaPrinter(getPrinter(), printComments);
    this.traverser.setStreamExpressionsHandler(streamExpressionsJavaPrinter);
    this.traverser.getStreamExpressionsVisitorList().clear();
    this.traverser.add4StreamExpressions(streamExpressionsJavaPrinter);

    // Statements

    SetExpressionsJavaGenVisitor setExpressionsPrinter = new SetExpressionsJavaGenVisitor(state);
    this.traverser.setSetExpressionsHandler(setExpressionsPrinter);

    MACommonStatementsJavaGenVisitor mcCommonStatementsPrinter = new MACommonStatementsJavaGenVisitor(state);
    this.traverser.setMCCommonStatementsHandler(mcCommonStatementsPrinter);

    MAVarDeclarationStatementsJavaGenVisitor mcVarDeclarationStatementsJavaPrinter = new MAVarDeclarationStatementsJavaGenVisitor(state);
    this.traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatementsJavaPrinter);

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

  public String generateCode(ASTExpression expression, SymTypeExpression targetType) {
    String printedExpression = this.generateCode(expression);
    SymTypeExpression expressionType = TypeCheck3.typeOf(expression, targetType);

    getPrinter().clearBuffer();
    CodeGenSymTypeExpressionConverter.printConverted(
      getPrinter(),
      targetType,
      expressionType,
      printer -> printer.print(printedExpression)
    );
    return getPrinter().getContent();
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
