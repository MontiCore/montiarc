/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcVarIf;
import variablearc._ast.VariableArcElements;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._visitor.VariableArcTraverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Convention: Features should be used at least once.
 */
public class FeatureUsage implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol symbol = node.getSymbol();

    // CoCo does not apply to atomic components
    if (symbol.getSubcomponents().isEmpty()) {
      return;
    }

    Collection<String> varifs = getNamesInIfConditions(node);
    Collection<String> constraints = getNamesInConstraints(node);
    Collection<String> features = getNamesOfFeatures(node);
    for (String feature : features) {
      final SourcePosition sourcePosition = this.getSourcePosition(symbol, node, feature);
      if (!(varifs.contains(feature) || constraints.contains(feature))) {
        Log.warn(VariableArcError.FEATURE_UNUSED.format(feature), sourcePosition);
      }
    }
  }

  protected Collection<String> getNamesOfFeatures(@NotNull ASTArcComponentType node) {
    return ((IVariableArcScope) node.getSpannedScope()).getLocalArcFeatureSymbols()
      .stream().map(ArcFeatureSymbol::getName).collect(Collectors.toList());
  }

  protected Collection<String> getNamesInIfConditions(@NotNull ASTArcComponentType node) {
    return this.getNamesIn(VariableArcElements.of(node, ASTArcVarIf.class).stream()
      .map(ASTArcVarIf::getCondition)
      .toList());
  }

  protected Collection<String> getNamesInConstraints(@NotNull ASTArcComponentType node) {
    return this.getNamesIn(VariableArcElements.of(node, ASTArcConstraintDeclaration.class).stream()
      .map(ASTArcConstraintDeclaration::getExpression)
      .toList());
  }

  protected Collection<String> getNamesIn(@NotNull List<ASTExpression> expressions) {
    List<String> names = new ArrayList<>();
    VariableArcTraverser traverser = VariableArcMill.traverser();
    traverser.add4ExpressionsBasis(new ExpressionsBasisVisitor2() {
      @Override
      public void visit(ASTNameExpression node) {
        names.add(node.getName());
      }
    });
    expressions.forEach(expression -> expression.accept(traverser));
    return names;
  }

  protected SourcePosition getSourcePosition(ComponentTypeSymbol symbol,
                                             ASTArcComponentType node,
                                             String feature) {
    return ((IVariableArcScope) symbol.getSpannedScope()).getArcFeatureSymbols()
      .get(feature).stream().findFirst()
      .map(p -> p.getAstNode().get_SourcePositionStart())
      .orElse(node.get_SourcePositionEnd());
  }
}
