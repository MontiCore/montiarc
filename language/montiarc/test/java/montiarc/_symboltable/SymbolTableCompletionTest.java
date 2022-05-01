/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcautomaton._visitor.ArcAutomatonInheritanceHandler;
import arcbasis._visitor.ArcBasisInheritanceHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arccore._visitor.ArcCoreInheritanceHandler;
import basicmodeautomata._visitor.BasicModeAutomataInheritanceHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeInheritanceHandler;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsInheritanceHandler;
import de.monticore.statements.mcstatementsbasis._visitor.MCStatementsBasisInheritanceHandler;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsInheritanceHandler;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsInheritanceHandler;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsInheritanceHandler;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesInheritanceHandler;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesInheritanceHandler;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesInheritanceHandler;
import de.monticore.umlstereotype._visitor.UMLStereotypeInheritanceHandler;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;
import genericarc._visitor.GenericArcInheritanceHandler;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcInheritanceHandler;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._visitor.VariableArcInheritanceHandler;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Integration test for the {@link MontiArcScopesGenitorDelegator} and the
 * {@link MontiArcSymbolTableCompleterDelegator} that checks that the symbol
 * table is constructed fully and correctly with respect to basic properties.
 * That is, checks that all symbols, symbols and ast nodes have an enclosing
 * scope, and that all spanning symbols and ast nodes have a spanned scope.
 */
public class SymbolTableCompletionTest extends AbstractTest {

  protected static final String TEST_MODEL_PATH = "montiarc/_symboltable/completion/";

  @ParameterizedTest
  @ValueSource(strings = {"ComplexComponent.arc"})
  public void checkSymbolTableCompletion(@NotNull String model) {
    Preconditions.checkNotNull(model);
    Preconditions.checkState(Log.getFindings().isEmpty());

    // Given
    MontiArcTool tool = new MontiArcTool();
    tool.initializeClass2MC();

    // When
    ASTMACompilationUnit ast = tool.parse(Paths.get(RELATIVE_MODEL_PATH, TEST_MODEL_PATH, model))
      .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));
    tool.createSymbolTable(ast);
    tool.runDefaultCoCos(ast);
    tool.completeSymbolTable(ast);

    // Then
    SymbolTableCompletionChecker.Result r = SymbolTableCompletionChecker.checkComplete(ast);
    Assertions.assertTrue(r.isComplete(), "The symbol-table is not complete, findings: " + r.getFindings().toString());
  }

  /**
   * The checker used to check the completion of the symbol table. Completion
   * of the symbol table for any ast node can be checked via the {@link Result}
   * provided by {@link #checkComplete(ASTNode)}.
   */
  public static class SymbolTableCompletionChecker {

    protected MontiArcTraverser traverser;

    protected List<String> findings;

    protected SymbolTableCompletionChecker() {
      this(new ArrayList<>());
    }

    protected SymbolTableCompletionChecker(@NotNull List<String> findings) {
      Preconditions.checkNotNull(findings);
      this.findings = findings;

      this.traverser = MontiArcMill.traverser();

      // MontiArc handler
      this.traverser.setArcBasisHandler(new ArcBasisInheritanceHandler());
      this.traverser.setArcCoreHandler(new ArcCoreInheritanceHandler());
      this.traverser.setGenericArcHandler(new GenericArcInheritanceHandler());
      this.traverser.setComfortableArcHandler(new ComfortableArcInheritanceHandler());
      this.traverser.setVariableArcHandler(new VariableArcInheritanceHandler());
      this.traverser.setBasicModeAutomataHandler(new BasicModeAutomataInheritanceHandler());
      this.traverser.setMontiArcHandler(new MontiArcInheritanceHandler());
      this.traverser.setArcAutomatonHandler(new ArcAutomatonInheritanceHandler());

      // Statechart handler
      this.traverser.setSCBasisHandler(new SCBasisInheritanceHandler());
      this.traverser.setSCActionsHandler(new SCActionsInheritanceHandler());
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

      // Visitor
      this.traverser.add4IVisitor(new Visitor(findings));
    }

    public static Result checkComplete(@NotNull ASTNode node) {
      SymbolTableCompletionChecker checker = new SymbolTableCompletionChecker();
      node.accept(checker.getTraverser());
      return new Result(checker.getFindings());
    }

    protected List<String> getFindings() {
      return this.findings;
    }

    protected ArcBasisTraverser getTraverser() {
      return this.traverser;
    }

    public static class Result {

      protected ImmutableList<String> findings;

      protected Result(@NotNull List<String> findings) {
        Preconditions.checkNotNull(findings);
        Preconditions.checkArgument(!findings.contains(null));
        this.findings = ImmutableList.copyOf(findings);
      }

      public ImmutableList<String> getFindings() {
        return this.findings;
      }

      public boolean isComplete() {
        return this.getFindings().isEmpty();
      }
    }

    public static class Visitor implements IVisitor {

      protected List<String> findings;

      public Visitor(@NotNull List<String> findings) {
        Preconditions.checkNotNull(findings);
        this.findings = findings;
      }

      protected List<String> getFindings() {
        return this.findings;
      }

      protected void addFinding(@NotNull String finding) {
        Preconditions.checkNotNull(finding);
        this.getFindings().add(finding);
      }

      protected void addFinding(@NotNull String finding, @NotNull String type, @NotNull Object subject) {
        Preconditions.checkNotNull(finding);
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(subject);
        this.addFinding(finding + " " + "The " + type + " is a " + subject.getClass().getName() + ".");
      }

      @Override
      public void visit(@Nullable IScope scope) {
        Preconditions.checkNotNull(scope);
        if (scope.getEnclosingScope() == null) {
          this.addFinding("The enclosing scope of a scope is missing.", "scope", scope);
        }
        if (!scope.isPresentAstNode()) {
          this.addFinding("The spanning ast node of the scope is missing.", "scope", scope);
        }
      }

      @Override
      public void visit(@Nullable ISymbol symbol) {
        Preconditions.checkNotNull(symbol);
        Preconditions.checkNotNull(symbol.getName());
        Preconditions.checkArgument(!symbol.getName().isEmpty());
        if (symbol.getEnclosingScope() == null) {
          this.addFinding("The enclosing scope of the symbol is missing.", "symbol", symbol);
        }
        if (!symbol.isPresentAstNode()) {
          this.addFinding("The ast node of the symbol is missing.", "symbol", symbol);
        }

      }

      @Override
      public void visit(@NotNull IScopeSpanningSymbol symbol) {
        Preconditions.checkNotNull(symbol);
        Preconditions.checkNotNull(symbol.getName());
        Preconditions.checkArgument(!symbol.getName().isEmpty());
        if (symbol.getSpannedScope() == null) {
          this.addFinding("The spanned scope of the scope spanning symbol is missing.", "symbol", symbol);
        } else if (symbol.getSpannedScope().getSpanningSymbol() == null) {
          this.addFinding("The spanning symbol of the spanned scope of symbol is missing.", "symbol", symbol);
        } else if (!symbol.getSpannedScope().getSpanningSymbol().equals(symbol)) {
          this.addFinding("The scope spanning symbol '" + symbol.getName()
            + "' and the symbol of its spanned scope do not match.", "symbol", symbol);
        }
      }

      @Override
      public void visit(@NotNull ASTNode node) {
        Preconditions.checkNotNull(node);
        if (node.getEnclosingScope() == null) {
          this.addFinding("The enclosing scope of the ast node is missing.", "ast-node", node);
        }
      }
    }
  }
}
