/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.visitor.IVisitor;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;

/**
 * A visitor that checks if the symbol table is well-structured and complete by
 * visiting the top level interfaces of ast nodes ({@link ASTNode}), symbols
 * ({@link ISymbol}) and scopes ({@link IScope}). Intended to be used as the
 * basic visitor of a traverser together with the inheritance handlers of each
 * super language (e.g., {@link montiarc._visitor.MontiArcInheritanceHandler}).
 */
public class SymbolTableVisitor implements IVisitor {

  protected List<String> findings;

  /**
   * Creates a new visitor that stores any findings in the provided list.
   *
   * @param findings the list of findings, must be mutable
   */
  public SymbolTableVisitor(@NotNull List<String> findings) {
    Preconditions.checkNotNull(findings);
    this.findings = findings;
  }

  /**
   * @return the list of findings
   */
  protected List<String> findings() {
    return this.findings;
  }

  /**
   * Adds a finding to the internal list of findings.
   *
   * @param finding the finding to add
   */
  protected void addFinding(@NotNull String finding) {
    Preconditions.checkNotNull(finding);
    this.findings().add(finding);
  }

  /**
   * Adds a finding and context information to the internal list of findings.
   *
   * @param finding the finding to add
   * @param subject the subject, e.g., symbol, scope, or ast
   * @param object the object of the finding (specific instance of the subject)
   */
  protected void addFinding(@NotNull String finding,
                            @NotNull String subject,
                            @NotNull Object object) {
    Preconditions.checkNotNull(finding);
    Preconditions.checkNotNull(subject);
    Preconditions.checkNotNull(object);
    this.addFinding(finding + " " + "The " + subject + " is a " + object.getClass().getName() + ".");
  }

  /**
   * Visits a scope to check if its attributes with respect to the symbol table
   * are set as expected. That is, it checks if the scope has an enclosing scope
   * and is linked with its ast. Any findings are stored internally.
   *
   * @param scope the scope to visit
   */
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

  /**
   * Visits a symbol to check if its attributes with respect to the symbol table
   * are set as expected. That is, it checks if the symbol has an enclosing
   * scope and is linked with its ast. Any findings are stored internally.
   *
   * @param symbol the symbol to visit
   */
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

  /**
   * Visits a spanning symbol to check if its attributes with respect to the
   * symbol table are set as expected. That is, it checks if the spanning symbol
   * has a spanned scope, the spanned scope has a spanning symbol, and whether
   * the spanning symbols match. Any findings are stored internally.
   *
   * @param symbol the scope-spanning symbol to visit
   */
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

  /**
   * Visits an ast node to check if its attributes with respect to the symbol
   * table are set as expected. That is, it checks if the ast node
   * has an enclosing scope. Any findings are stored internally.
   *
   * @param node the ast node to visit
   */
  @Override
  public void visit(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);
    if (node.getEnclosingScope() == null) {
      this.addFinding("The enclosing scope of the ast node is missing.", "ast", node);
    }
  }
}