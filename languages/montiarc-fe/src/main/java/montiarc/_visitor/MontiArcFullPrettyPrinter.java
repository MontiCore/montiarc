/* (c) https://github.com/MontiCore/monticore */
package montiarc._visitor;

import arcautomaton._visitor.ArcAutomatonPrettyPrinter;
import arcbasis._visitor.ArcBasisPrettyPrinter;
import comfortablearc._visitor.ComfortableArcPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.SCActionsPrettyPrinter;
import de.monticore.prettyprint.SCBasisPrettyPrinter;
import de.monticore.prettyprint.SCTransitions4CodePrettyPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import genericarc._visitor.GenericArcPrettyPrinter;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMontiArcNode;
import variablearc._visitor.VariableArcPrettyPrinter;

public class MontiArcFullPrettyPrinter {

  protected MontiArcTraverser traverser;

  protected IndentPrinter printer;

  public MontiArcFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public MontiArcFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    traverser = MontiArcMill.traverser();
  
    CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new CommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
    AssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter = new AssignmentExpressionsPrettyPrinter(printer);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);
    traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
    MCSimpleGenericTypesPrettyPrinter mcSimpleGenericTypesPrettyPrinter = new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesPrettyPrinter);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesPrettyPrinter);
    MCCommonStatementsPrettyPrinter mcCommonStatementsPrettyPrinter = new MCCommonStatementsPrettyPrinter(printer);
    traverser.setMCCommonStatementsHandler(mcCommonStatementsPrettyPrinter);
    traverser.add4MCCommonStatements(mcCommonStatementsPrettyPrinter);
    MCVarDeclarationStatementsPrettyPrinter mcVarDeclarationStatementsPrettyPrinter = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatementsPrettyPrinter);
    traverser.add4MCVarDeclarationStatements(mcVarDeclarationStatementsPrettyPrinter);

    SCTransitions4CodePrettyPrinter scTransitions4CodePrettyPrinter = new SCTransitions4CodePrettyPrinter(printer);
    traverser.setSCTransitions4CodeHandler(scTransitions4CodePrettyPrinter);
    ArcAutomatonPrettyPrinter arcAutomatonPrettyPrinter = new ArcAutomatonPrettyPrinter(printer);
    traverser.setArcAutomatonHandler(arcAutomatonPrettyPrinter);
    traverser.add4ArcAutomaton(arcAutomatonPrettyPrinter);
    SCBasisPrettyPrinter scBasisPrettyPrinter = new SCBasisPrettyPrinter(printer);
    traverser.setSCBasisHandler(scBasisPrettyPrinter);
    SCActionsPrettyPrinter scActionsPrettyPrinter = new SCActionsPrettyPrinter(printer);
    traverser.setSCActionsHandler(scActionsPrettyPrinter);

    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
    MCCollectionTypesPrettyPrinter mcCollectionTypesPrettyPrinter = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(mcCollectionTypesPrettyPrinter);
    traverser.add4MCCollectionTypes(mcCollectionTypesPrettyPrinter);
    MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
    ArcBasisPrettyPrinter arcBasisPrettyPrinter = new ArcBasisPrettyPrinter(printer);
    traverser.setArcBasisHandler(arcBasisPrettyPrinter);
    ComfortableArcPrettyPrinter comfortableArcPrettyPrinter = new ComfortableArcPrettyPrinter(printer);
    traverser.setComfortableArcHandler(comfortableArcPrettyPrinter);
    GenericArcPrettyPrinter genericArcPrettyPrinter = new GenericArcPrettyPrinter(printer);
    traverser.setGenericArcHandler(genericArcPrettyPrinter);

    MCCommonLiteralsPrettyPrinter commonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(commonLiteralsPrettyPrinter);
    traverser.add4MCCommonLiterals(commonLiteralsPrettyPrinter);

    MontiArcPrettyPrinter montiArcPrettyPrinter = new MontiArcPrettyPrinter(printer);
    traverser.setMontiArcHandler(montiArcPrettyPrinter);

    VariableArcPrettyPrinter variableArcPrettyPrinter = new VariableArcPrettyPrinter(printer);
    traverser.setVariableArcHandler(variableArcPrettyPrinter);
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTMontiArcNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
  
  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBlockStatement node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public MontiArcTraverser getTraverser() {
    return traverser;
  }
}
