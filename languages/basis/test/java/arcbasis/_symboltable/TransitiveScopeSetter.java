/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisInheritanceHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsInheritanceHandler;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisInheritanceHandler;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor2;
import de.monticore.mcbasics._visitor.MCBasicsInheritanceHandler;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsInheritanceHandler;
import de.monticore.symbols.oosymbols._visitor.OOSymbolsInheritanceHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesInheritanceHandler;
import de.monticore.visitor.ITraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Sets the scope as enclosing scope of the provided node and all child nodes by traversing the ast structure.
 */
public class TransitiveScopeSetter {

  /**
   * Sets the provided scope as the node's enclosing scope and as enclosing
   * scope for all transitive child nodes
   *
   * @param ast the AST for whose nodes to set the enclosing scope
   * @param scope the scope to set as enclosing scope
   */
  public void setScope(@NotNull ASTExpression ast, @NotNull IArcBasisScope scope) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(scope);
    this.setScope(ast, this.traverser(scope));
  }

  protected void setScope(@NotNull ASTExpression ast, @NotNull ITraverser traverser) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(traverser);
    ast.accept(traverser);
  }

  /**
   * Creates a traverser for traversing the ast structure and calling the respective visitors
   */
  protected ITraverser traverser(@NotNull IArcBasisScope scope) {
    Preconditions.checkNotNull(scope);
    ArrayDeque<IArcBasisScope> scopeStack = new ArrayDeque<>();
    scopeStack.push(scope);
    return this.traverser(new ScopeSetter(scopeStack));
  }

  protected ITraverser traverser(@NotNull ScopeSetter scopeSetter) {
    Preconditions.checkNotNull(scopeSetter);
    ArcBasisTraverser traverser = ArcBasisMill.inheritanceTraverser();
    this.init(traverser, scopeSetter);
    return traverser;
  }

  /**
   * Initialized the traverser with the provided visitor and initializes the
   * traverser for inheritance handling.
   *
   * @param traverser the traverser to initialize
   * @param scopeSetter the scope setter to use as visitor
   */
  protected void init(@NotNull ArcBasisTraverser traverser, @NotNull ScopeSetter scopeSetter) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(scopeSetter);
    traverser.add4IVisitor(scopeSetter);
    traverser.add4ExpressionsBasis(scopeSetter);
    traverser.add4MCLiteralsBasis(scopeSetter);
    traverser.setMCBasicsHandler(new MCBasicsInheritanceHandler());
    traverser.setMCBasicTypesHandler(new MCBasicTypesInheritanceHandler());
    traverser.setBasicSymbolsHandler(new BasicSymbolsInheritanceHandler());
    traverser.setOOSymbolsHandler(new OOSymbolsInheritanceHandler());
    traverser.setExpressionsBasisHandler(new ExpressionsBasisInheritanceHandler());
    traverser.setMCLiteralsBasisHandler(new MCLiteralsBasisInheritanceHandler());
    traverser.setMCCommonLiteralsHandler(new MCCommonLiteralsInheritanceHandler());
  }

  /**
   * A visitor used when traversing the ast to set the enclosing scope of visited nodes.
   */
  public static class ScopeSetter implements ExpressionsBasisVisitor2, MCLiteralsBasisVisitor2 {

    protected Deque<? extends IArcBasisScope> scopeStack;

    /**
     * Initializes the scope setter with a scope stack that can be shared
     * across different scope setters. When visiting a node, sets the top-most
     * scope on the scope stack as the node's enclosing scope.
     */
    public ScopeSetter(@NotNull Deque<? extends IArcBasisScope> scopeStack) {
      this.scopeStack = scopeStack;
    }

    /**
     * @return the scope stack
     */
    protected Deque<? extends IArcBasisScope> getScopeStack() {
      return this.scopeStack;
    }

    /**
     * If the node has no enclosing scope, sets the top-most scope on the scope
     * stack as the node's enclosing scope.
     */
    @Override
    public void endVisit(@NotNull ASTExpression node) {
      Preconditions.checkNotNull(node);
      Preconditions.checkState(!this.getScopeStack().isEmpty());
      if (node.getEnclosingScope() == null) {
        node.setEnclosingScope(this.getScopeStack().getLast());
      }
    }

    @Override
    public void endVisit(@NotNull ASTLiteral node) {
      Preconditions.checkNotNull(node);
      Preconditions.checkState(!this.getScopeStack().isEmpty());
      if (node.getEnclosingScope() == null) {
        node.setEnclosingScope(this.getScopeStack().getLast());
      }
    }
  }
}
