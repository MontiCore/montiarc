/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.assignmentexpressions._prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._symboltable.VariantPortSymbol;

import java.util.List;
import java.util.Optional;

public class AssignmentExpressionsMA2JSimPrinter extends AssignmentExpressionsPrettyPrinter {
  protected ComponentTypeSymbol currentVariant;

  public AssignmentExpressionsMA2JSimPrinter(@NotNull IndentPrinter printer, boolean printComments, @Nullable ComponentTypeSymbol currentVariant) {
    super(Preconditions.checkNotNull(printer), printComments);
    this.currentVariant = currentVariant;
  }

  @Override
  public void handle(ASTAssignmentExpression expr) {
    Preconditions.checkState(expr.getLeft().getEnclosingScope() != null);
    Preconditions.checkState(expr.getLeft().getEnclosingScope() instanceof IMontiArcScope);

    super.handle(expr);

    if (expr.getOperator() == ASTConstantsAssignmentExpressions.EQUALS
      && expr.getLeft() instanceof ASTNameExpression) {

      ASTNameExpression left = (ASTNameExpression) expr.getLeft();
      String name = left.getName();
      // Optional<ISymbol> optSym = left.getDefiningSymbol(); // Not available yet

      Optional<PortSymbol> port = Optional.ofNullable(currentVariant).flatMap(v -> v.getPort(name, true)).or(() -> ((IMontiArcScope) left.getEnclosingScope()).resolvePortMany(name).stream().findAny());

      if (port.isPresent()) {
        String sendValue = isUnboxedChar(port.get().getType()) ? "(int) " + name : name;

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

        this.getPrinter().print(String.format("context.port_%s().send(%s);", name + suffix, sendValue));
      }
    }
  }

  private boolean isUnboxedChar(SymTypeExpression expr) {
    return expr.isPrimitive()
      && BasicSymbolsMill.CHAR.equals(expr.asPrimitive().getPrimitiveName());
  }
}
