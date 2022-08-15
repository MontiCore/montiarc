/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcautomaton._visitor.ArcAutomatonInheritanceHandler;
import arcbasis._visitor.ArcBasisInheritanceHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arccore._visitor.ArcCoreInheritanceHandler;
import basicmodeautomata._visitor.BasicModeAutomataInheritanceHandler;
import com.google.common.base.Preconditions;
import comfortablearc._visitor.ComfortableArcInheritanceHandler;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsInheritanceHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsInheritanceHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisInheritanceHandler;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsInheritanceHandler;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisInheritanceHandler;
import de.monticore.mcbasics._visitor.MCBasicsInheritanceHandler;
import de.monticore.scactions._visitor.SCActionsInheritanceHandler;
import de.monticore.scbasis._visitor.SCBasisInheritanceHandler;
import de.monticore.scdoactions._visitor.SCDoActionsInheritanceHandler;
import de.monticore.scstatehierarchy._visitor.SCStateHierarchyInheritanceHandler;
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeInheritanceHandler;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsInheritanceHandler;
import de.monticore.statements.mcstatementsbasis._visitor.MCStatementsBasisInheritanceHandler;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsInheritanceHandler;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsInheritanceHandler;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsInheritanceHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesInheritanceHandler;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesInheritanceHandler;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesInheritanceHandler;
import de.monticore.umlstereotype._visitor.UMLStereotypeInheritanceHandler;
import genericarc._visitor.GenericArcInheritanceHandler;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcInheritanceHandler;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariableArcInheritanceHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks the completeness of the symbol table by traversing ast nodes,
 * scopes, and symbols. Completion of the symbol table for any ast node can
 * be checked via the method {@link #checkComplete(ASTNode)}, which provides
 * a list of findings that is empty of the symbol table has the expected
 * scope structure.
 */
public class SymbolTableChecker {

  protected MontiArcTraverser traverser;

  protected List<String> findings;

  protected SymbolTableChecker() {
    this(new ArrayList<>());
  }

  protected SymbolTableChecker(@NotNull List<String> findings) {
    Preconditions.checkNotNull(findings);
    this.findings = findings;

    this.traverser = MontiArcMill.traverser();

    // MontiArc handler
    this.traverser.setArcBasisHandler(new ArcBasisInheritanceHandler());
    this.traverser.setArcAutomatonHandler(new ArcAutomatonInheritanceHandler());
    this.traverser.setComfortableArcHandler(new ComfortableArcInheritanceHandler());
    this.traverser.setArcCoreHandler(new ArcCoreInheritanceHandler());
    this.traverser.setGenericArcHandler(new GenericArcInheritanceHandler());
    this.traverser.setVariableArcHandler(new VariableArcInheritanceHandler());
    this.traverser.setBasicModeAutomataHandler(new BasicModeAutomataInheritanceHandler());
    this.traverser.setMontiArcHandler(new MontiArcInheritanceHandler());

    // Statechart handler
    this.traverser.setSCBasisHandler(new SCBasisInheritanceHandler());
    this.traverser.setSCActionsHandler(new SCActionsInheritanceHandler());
    this.traverser.setSCDoActionsHandler(new SCDoActionsInheritanceHandler());
    this.traverser.setSCStateHierarchyHandler(new SCStateHierarchyInheritanceHandler());
    this.traverser.setSCTransitions4CodeHandler(new SCTransitions4CodeInheritanceHandler());

    // MontiCore handler
    this.traverser.setMCBasicsHandler(new MCBasicsInheritanceHandler());

    // Expression handler
    this.traverser.setExpressionsBasisHandler(new ExpressionsBasisInheritanceHandler());
    this.traverser.setCommonExpressionsHandler(new CommonExpressionsInheritanceHandler());
    this.traverser.setAssignmentExpressionsHandler(new AssignmentExpressionsInheritanceHandler());

    // Statement handler
    this.traverser.setMCStatementsBasisHandler(new MCStatementsBasisInheritanceHandler());
    this.traverser.setMCCommonStatementsHandler(new MCCommonStatementsInheritanceHandler());
    this.traverser.setMCVarDeclarationStatementsHandler(new MCVarDeclarationStatementsInheritanceHandler());

    // Type handler
    this.traverser.setMCBasicTypesHandler(new MCBasicTypesInheritanceHandler());
    this.traverser.setMCCollectionTypesHandler(new MCCollectionTypesInheritanceHandler());
    this.traverser.setMCSimpleGenericTypesHandler(new MCSimpleGenericTypesInheritanceHandler());

    // Literal handler
    this.traverser.setMCLiteralsBasisHandler(new MCLiteralsBasisInheritanceHandler());
    this.traverser.setMCCommonLiteralsHandler(new MCCommonLiteralsInheritanceHandler());

    // Symbol handler
    this.traverser.setBasicSymbolsHandler(new BasicSymbolsInheritanceHandler());
    this.traverser.setOOSymbolsHandler(new OOSymbolsInheritanceHandler());

    // Stereotype handler
    this.traverser.setUMLStereotypeHandler(new UMLStereotypeInheritanceHandler());

    // SymbolTableVisitor
    this.traverser.add4IVisitor(new SymbolTableVisitor(findings));
  }

  /**
   * Checks the completeness of the symbol-table by traversing the ast,
   * scopes, and symbols using the provided ast node as root.
   *
   * @param node the root of the traversal
   * @return a list of findings, empty if the symbol-table is complete
   */
  public static List<String> checkComplete(@NotNull ASTNode node) {
    SymbolTableChecker checker = new SymbolTableChecker();
    node.accept(checker.getTraverser());
    return checker.getFindings();
  }

  protected List<String> getFindings() {
    return this.findings;
  }

  protected ArcBasisTraverser getTraverser() {
    return this.traverser;
  }
}