/* (c) https://github.com/MontiCore/monticore */
package montiarc._visitor;

import arcautomaton._visitor.ArcAutomatonPrettyPrinter;
import arcbasis._visitor.ArcBasisPrettyPrinter;
import arcbasis._visitor.IFullPrettyPrinter;
import com.google.common.base.Preconditions;
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
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariableArcPrettyPrinter;

public class MontiArcFullPrettyPrinter implements IFullPrettyPrinter {

  protected MontiArcTraverser traverser;

  protected IndentPrinter printer;

  public MontiArcFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public MontiArcFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    traverser = MontiArcMill.traverser();

    // MontiArc languages
    initMontiArcPrettyPrinter(printer);
    initArcBasisPrettyPrinter(printer);
    initComfortableArcPrettyPrinter(printer);
    initGenericArcPrettyPrinter(printer);
    initArcAutomatonPrettyPrinter(printer);
    initVariableArcPrettyPrinter(printer);

    // Type languages
    initMCBasicTypesPrettyPrinter(printer);
    initMCCollectionTypesPrettyPrinter(printer);
    initMCSimpleGenericTypesPrettyPrinter(printer);

    // Expression & Literal languages
    initExpressionsBasisPrettyPrinter(printer);
    initCommonExpressionPrettyPrinter(printer);
    initAssignmentExpressionsPrettyPrinter(printer);
    initMCCommonLiteralsPrettyPrinter(printer);

    // Statement languages
    initMCCommonStatementsPrettyPrinter(printer);
    initMCVarDeclarationStatementsPrettyPrinter(printer);

    // Statechart languages
    initSCBasisPrettyPrinter(printer);
    initSCTransitions4CodePrettyPrinter(printer);
    initSCActionsPrettyPrinter(printer);
  }

  protected void initMontiArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    MontiArcPrettyPrinter montiArcPrettyPrinter = new MontiArcPrettyPrinter(printer);
    traverser.setMontiArcHandler(montiArcPrettyPrinter);
  }

  protected void initArcBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    ArcBasisPrettyPrinter arcBasisPrettyPrinter = new ArcBasisPrettyPrinter(printer);
    traverser.setArcBasisHandler(arcBasisPrettyPrinter);
  }

  protected void initComfortableArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    ComfortableArcPrettyPrinter comfortableArcPrettyPrinter = new ComfortableArcPrettyPrinter(printer);
    traverser.setComfortableArcHandler(comfortableArcPrettyPrinter);
  }

  protected void initGenericArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    GenericArcPrettyPrinter genericArcPrettyPrinter = new GenericArcPrettyPrinter(printer);
    traverser.setGenericArcHandler(genericArcPrettyPrinter);
  }

  protected void initArcAutomatonPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    ArcAutomatonPrettyPrinter arcAutomatonPrettyPrinter = new ArcAutomatonPrettyPrinter(printer);
    traverser.setArcAutomatonHandler(arcAutomatonPrettyPrinter);
    traverser.add4ArcAutomaton(arcAutomatonPrettyPrinter);
  }

  protected void initVariableArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    VariableArcPrettyPrinter variableArcPrettyPrinter = new VariableArcPrettyPrinter(printer);
    traverser.setVariableArcHandler(variableArcPrettyPrinter);
  }

  protected void initMCBasicTypesPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
  }

  protected void initMCCollectionTypesPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    MCCollectionTypesPrettyPrinter mcCollectionTypesPrettyPrinter = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(mcCollectionTypesPrettyPrinter);
    traverser.add4MCCollectionTypes(mcCollectionTypesPrettyPrinter);
  }

  protected void initMCSimpleGenericTypesPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    MCSimpleGenericTypesPrettyPrinter mcSimpleGenericTypesPrettyPrinter =
      new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesPrettyPrinter);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesPrettyPrinter);
  }

  protected void initExpressionsBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
  }

  protected void initCommonExpressionPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new CustomCommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
  }

  protected void initAssignmentExpressionsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    AssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter =
      new AssignmentExpressionsPrettyPrinter(printer);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);
    traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
  }

  protected void initMCCommonLiteralsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    MCCommonLiteralsPrettyPrinter commonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(commonLiteralsPrettyPrinter);
    traverser.add4MCCommonLiterals(commonLiteralsPrettyPrinter);
  }

  protected void initMCCommonStatementsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    MCCommonStatementsPrettyPrinter mcCommonStatementsPrettyPrinter = new MCCommonStatementsPrettyPrinter(printer);
    traverser.setMCCommonStatementsHandler(mcCommonStatementsPrettyPrinter);
    traverser.add4MCCommonStatements(mcCommonStatementsPrettyPrinter);
  }

  protected void initMCVarDeclarationStatementsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    MCVarDeclarationStatementsPrettyPrinter mcVarDeclarationStatementsPrettyPrinter =
      new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatementsPrettyPrinter);
    traverser.add4MCVarDeclarationStatements(mcVarDeclarationStatementsPrettyPrinter);
  }

  protected void initSCBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    SCBasisPrettyPrinter scBasisPrettyPrinter = new SCBasisPrettyPrinter(printer);
    traverser.setSCBasisHandler(scBasisPrettyPrinter);
  }

  protected void initSCTransitions4CodePrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    SCTransitions4CodePrettyPrinter scTransitions4CodePrettyPrinter = new SCTransitions4CodePrettyPrinter(printer);
    traverser.setSCTransitions4CodeHandler(scTransitions4CodePrettyPrinter);
  }

  protected void initSCActionsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    SCActionsPrettyPrinter scActionsPrettyPrinter = new SCActionsPrettyPrinter(printer);
    traverser.setSCActionsHandler(scActionsPrettyPrinter);
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

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public MontiArcTraverser getTraverser() {
    return traverser;
  }
}
