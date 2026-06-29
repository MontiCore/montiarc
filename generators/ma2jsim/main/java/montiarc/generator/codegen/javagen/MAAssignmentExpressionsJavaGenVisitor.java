/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.assignmentexpressions.codegen.javagen.AssignmentExpressionsJavaGenVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.ISymbol;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._symboltable.VariantPortSymbol;

import java.util.List;
import java.util.Optional;

public class MAAssignmentExpressionsJavaGenVisitor extends AssignmentExpressionsJavaGenVisitor {

  protected ComponentTypeSymbol currentVariant;

  public MAAssignmentExpressionsJavaGenVisitor(@NotNull JavaGenVisitorState state, @Nullable ComponentTypeSymbol currentVariant) {
    super(Preconditions.checkNotNull(state));
    this.currentVariant = currentVariant;
  }

  @Override
  public void handle(ASTAssignmentExpression expr) {
    Preconditions.checkState(expr.getLeft().getEnclosingScope() != null);
    Preconditions.checkState(expr.getLeft().getEnclosingScope() instanceof IMontiArcScope);

    super.handle(expr);

    if (expr.getOperator() == ASTConstantsAssignmentExpressions.EQUALS
      && expr.getLeft() instanceof ASTNameExpression left) {

      String name = left.getName();
      // Optional<ISymbol> optSym = left.getDefiningSymbol(); // Not available yet

      Optional<PortSymbol> port = Optional.ofNullable(currentVariant).flatMap(v -> v.getPort(name, true)).or(() -> ((IMontiArcScope) left.getEnclosingScope()).resolvePortMany(name).stream().findAny());

      if (port.isPresent()) {

        this.getPrinter().println(";");
        if (!port.get().getType().isPrimitive()) {
          this.getPrinter().print(String.format("if (%s != null) ", name));
        }
        // calc variant suffix
        if (port.get() instanceof VariantPortSymbol) {
          port = port.map(p -> ((VariantPortSymbol) p).getOriginal());
        }
        List<PortSymbol> ports = ISymbol.sortSymbolsByPosition(port.get().getEnclosingScope().resolvePortMany(name));
        String suffix = ports.size() <= 1 ? "" : Integer.toString(ports.indexOf(port.get()));

        this.getPrinter().print(String.format("context.port_%s().send(%s);", name + suffix, name));
      }
    }
  }
}
