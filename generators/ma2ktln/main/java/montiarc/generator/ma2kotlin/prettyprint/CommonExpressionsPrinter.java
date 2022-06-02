/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ISymbol;
import montiarc._symboltable.IMontiArcScope;
import montiarc._visitor.MontiArcFullPrettyPrinter;

import java.util.Optional;
import java.util.function.BiFunction;

public class CommonExpressionsPrinter extends CommonExpressionsPrettyPrinter {

  final MontiArcFullPrettyPrinter standard = new MontiArcFullPrettyPrinter();
  final SymbolPrinter helper;

  public CommonExpressionsPrinter(IndentPrinter printer) {
    super(printer);
    helper = new SymbolPrinter(printer);
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    // port queries are not printed the usual way, instead require access to the event
    if (!helper.findAndConsumeSymbol(
        node.getEnclosingScope(),
        helper.clearComments(standard.prettyprint(node)),
        helper::printPortQuery,
        null)) {
      // other field accessors are similar to java but might have to use the null-safe dot ("?." instead of ".")
      nullSafeHandle(node);
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * replaces all accessors with null safe ones, this is necessary since kotlin differs between nullable and non-null types.
   * Exceptions are static members, who must be accesses using the normal dot
   */
  public void nullSafeHandle(ASTFieldAccessExpression node) {
    node.getExpression().accept(getTraverser());
    if(canResolve(standard.prettyprint(node.getExpression()), node, IMontiArcScope::resolveType)){
      getPrinter().print(".");
    } else {
      getPrinter().print("?.");
    }
    getPrinter().print(node.getName());
  }

  @Override
  public void handle(ASTCallExpression node) {
    String methodName = standard.prettyprint(node.getExpression());
    if(canResolve(methodName, node, IMontiArcScope::resolveComponentType)){
      // component constructors require extra treatment, since they require an instance name
      CommentPrettyPrinter.printPreComments(node, getPrinter());
      node.getExpression().accept(getTraverser());
      getPrinter().print("(generateName(\"");
      int i = methodName.lastIndexOf(".")+1;
      getPrinter().print(Character.toLowerCase(methodName.charAt(i)));
      getPrinter().print(methodName.substring(i+1));
      getPrinter().print("\")");
      for(ASTExpression parameter: node.getArguments().getExpressionList()){
        getPrinter().print(", ");
        parameter.accept(getTraverser());
      }
      getPrinter().print(").also { create(it) }");
    } else {
      super.handle(node);
    }
  }

  @Override
  public void handle(ASTBooleanNotExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print(".inv()");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * checks whether the given name refers to a specific type of symbol
   * @param name name of the symbol to find (can be fully qualified)
   * @param node node in whose enclosing scope to search for the variable
   * @param resolver defines the type of symbol to find
   * @return true, if the name is a valid symbol's name and if there is such a symbol with this name
   */
  protected boolean canResolve(String name, ASTNode node, BiFunction<IMontiArcScope, String, Optional<? extends ISymbol>> resolver){
    name = helper.clearComments(name);
    return name.matches("\\w+(\\.\\w+)*") && resolver.apply(((IMontiArcScope) node.getEnclosingScope()), name).isPresent();
  }
}