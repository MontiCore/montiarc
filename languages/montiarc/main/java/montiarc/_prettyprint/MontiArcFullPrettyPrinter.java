/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import arcautomaton._prettyprint.ArcAutomatonPrettyPrinter;
import arcbasis._prettyprint.ArcBasisPrettyPrinter;
import arcbasis._visitor.IFullPrettyPrinter;
import com.google.common.base.Preconditions;
import comfortablearc._prettyprint.ComfortableArcPrettyPrinter;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.*;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import genericarc._prettyprint.GenericArcPrettyPrinter;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._prettyprint.VariableArcPrettyPrinter;

public class MontiArcFullPrettyPrinter implements IFullPrettyPrinter {

  protected MontiArcTraverser traverser;

  protected IndentPrinter printer;

  public MontiArcFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public MontiArcFullPrettyPrinter(@NotNull IndentPrinter printer) {
    this(Preconditions.checkNotNull(printer), true);
  }

  public MontiArcFullPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);
    this.printer = printer;
    this.traverser = MontiArcMill.traverser();

    // MontiArc languages
    initMontiArcPrettyPrinter(printer, printComments);
    initArcBasisPrettyPrinter(printer, printComments);
    initComfortableArcPrettyPrinter(printer, printComments);
    initGenericArcPrettyPrinter(printer, printComments);
    initArcAutomatonPrettyPrinter(printer, printComments);
    initVariableArcPrettyPrinter(printer, printComments);

    // Type languages
    initMCBasicTypesPrettyPrinter(printer, printComments);
    initMCCollectionTypesPrettyPrinter(printer, printComments);
    initMCSimpleGenericTypesPrettyPrinter(printer, printComments);

    // Expression & Literal languages
    initExpressionsBasisPrettyPrinter(printer, printComments);
    initCommonExpressionPrettyPrinter(printer, printComments);
    initBitExpressionsPrettyPrinter(printer, printComments);
    initAssignmentExpressionsPrettyPrinter(printer, printComments);
    initMCCommonLiteralsPrettyPrinter(printer, printComments);

    // Statement languages
    initMCCommonStatementsPrettyPrinter(printer, printComments);
    initMCVarDeclarationStatementsPrettyPrinter(printer, printComments);

    // Statechart languages
    initSCBasisPrettyPrinter(printer, printComments);
    initSCTransitions4CodePrettyPrinter(printer, printComments);
    initSCActionsPrettyPrinter(printer, printComments);
    initSCStateHierarchyPrettyPrinter(printer, printComments);
  }

  protected void initMontiArcPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    MontiArcPrettyPrinter montiArcPrettyPrinter = new MontiArcPrettyPrinter(printer, printComments);
    traverser.setMontiArcHandler(montiArcPrettyPrinter);
  }

  protected void initArcBasisPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    ArcBasisPrettyPrinter arcBasisPrettyPrinter = new ArcBasisPrettyPrinter(printer, printComments);
    traverser.setArcBasisHandler(arcBasisPrettyPrinter);
  }

  protected void initComfortableArcPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    ComfortableArcPrettyPrinter comfortableArcPrettyPrinter = new ComfortableArcPrettyPrinter(printer, printComments);
    traverser.setComfortableArcHandler(comfortableArcPrettyPrinter);
  }

  protected void initGenericArcPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    GenericArcPrettyPrinter genericArcPrettyPrinter = new GenericArcPrettyPrinter(printer, printComments);
    traverser.setGenericArcHandler(genericArcPrettyPrinter);
  }

  protected void initArcAutomatonPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    ArcAutomatonPrettyPrinter arcAutomatonPrettyPrinter = new ArcAutomatonPrettyPrinter(printer, printComments);
    traverser.setArcAutomatonHandler(arcAutomatonPrettyPrinter);
    traverser.add4ArcAutomaton(arcAutomatonPrettyPrinter);
  }

  protected void initVariableArcPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    VariableArcPrettyPrinter variableArcPrettyPrinter = new VariableArcPrettyPrinter(printer, printComments);
    traverser.setVariableArcHandler(variableArcPrettyPrinter);
  }

  protected void initMCBasicTypesPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
  }

  protected void initMCCollectionTypesPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    MCCollectionTypesPrettyPrinter mcCollectionTypesPrettyPrinter = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(mcCollectionTypesPrettyPrinter);
    traverser.add4MCCollectionTypes(mcCollectionTypesPrettyPrinter);
  }

  protected void initMCSimpleGenericTypesPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    MCSimpleGenericTypesPrettyPrinter mcSimpleGenericTypesPrettyPrinter =
      new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesPrettyPrinter);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesPrettyPrinter);
  }

  protected void initExpressionsBasisPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
  }

  protected void initCommonExpressionPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new CommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
  }

  protected void initBitExpressionsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    BitExpressionsPrettyPrinter bitExpressionsPrettyPrinter = new BitExpressionsPrettyPrinter(printer);
    traverser.setBitExpressionsHandler(bitExpressionsPrettyPrinter);
    traverser.add4BitExpressions(bitExpressionsPrettyPrinter);
  }

  protected void initAssignmentExpressionsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    AssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter =
      new AssignmentExpressionsPrettyPrinter(printer);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);
    traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
  }

  protected void initMCCommonLiteralsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    MCCommonLiteralsPrettyPrinter commonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(commonLiteralsPrettyPrinter);
    traverser.add4MCCommonLiterals(commonLiteralsPrettyPrinter);
  }

  protected void initMCCommonStatementsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    MCCommonStatementsPrettyPrinter mcCommonStatementsPrettyPrinter = new MCCommonStatementsPrettyPrinter(printer);
    traverser.setMCCommonStatementsHandler(mcCommonStatementsPrettyPrinter);
    traverser.add4MCCommonStatements(mcCommonStatementsPrettyPrinter);
  }

  protected void initMCVarDeclarationStatementsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    MCVarDeclarationStatementsPrettyPrinter mcVarDeclarationStatementsPrettyPrinter =
      new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatementsPrettyPrinter);
    traverser.add4MCVarDeclarationStatements(mcVarDeclarationStatementsPrettyPrinter);
  }

  protected void initSCBasisPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    SCBasisPrettyPrinter scBasisPrettyPrinter = new SCBasisPrettyPrinter(printer);
    traverser.setSCBasisHandler(scBasisPrettyPrinter);
  }

  protected void initSCTransitions4CodePrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    SCTransitions4CodePrettyPrinter scTransitions4CodePrettyPrinter = new SCTransitions4CodePrettyPrinter(printer);
    traverser.setSCTransitions4CodeHandler(scTransitions4CodePrettyPrinter);
  }

  protected void initSCStateHierarchyPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    SCStateHierarchyPrettyPrinter scStateHierarchyPrettyPrinter = new SCStateHierarchyPrettyPrinter(printer);
    traverser.setSCStateHierarchyHandler(scStateHierarchyPrettyPrinter);
  }

  protected void initSCActionsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    SCActionsPrettyPrinter scActionsPrettyPrinter = new SCActionsPrettyPrinter(printer);
    traverser.setSCActionsHandler(scActionsPrettyPrinter);
  }

  @Override
  public String prettyprint(ASTExpressionsBasisNode a) {
    return prettyprint((ASTNode) a);
  }

  @Override
  public String prettyprint(ASTMCBasicTypesNode type) {
    return prettyprint((ASTNode) type);
  }

  public String prettyprint(ASTNode node) {
    Preconditions.checkNotNull(node);
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(MontiArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    this.printer = printer;
  }
}
