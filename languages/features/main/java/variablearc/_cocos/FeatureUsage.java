/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._cocos.util.ComponentVarIfHandler;
import variablearc._cocos.util.GenericASTNameExpressionVisitor;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._util.VariableArcTypeDispatcher;
import variablearc._visitor.VariableArcTraverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Convention: Features should be used at least once.
 */
public class FeatureUsage implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
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

  protected Collection<String> getNamesOfFeatures(@NotNull ASTComponentType node) {
    return ((IVariableArcScope) node.getSpannedScope()).getLocalArcFeatureSymbols()
      .stream().map(ArcFeatureSymbol::getName).collect(Collectors.toList());
  }

  protected Collection<String> getNamesInIfConditions(@NotNull ASTComponentType node) {
    List<String> names = new ArrayList<>();
    ComponentVarIfHandler handler = new ComponentVarIfHandler(node, (varif) -> {
      Preconditions.checkNotNull(varif);
      VariableArcTraverser traverser = VariableArcMill.traverser();
      traverser.add4ExpressionsBasis(new GenericASTNameExpressionVisitor((
        astNameExpression -> names.add(astNameExpression.getName())))
      );
      varif.getCondition().accept(traverser);
    });
    node.accept(handler.getTraverser());
    return names;
  }

  protected Collection<String> getNamesInConstraints(@NotNull ASTComponentType node) {
    List<String> names = new ArrayList<>();
    VariableArcTraverser traverser = VariableArcMill.traverser();
    traverser.add4ExpressionsBasis(new GenericASTNameExpressionVisitor((
      astNameExpression -> names.add(astNameExpression.getName())))
    );

    VariableArcTypeDispatcher typeDispatcher = VariableArcMill.typeDispatcher();

    node.getBody().getArcElementList().stream()
      .filter(typeDispatcher::isASTArcConstraintDeclaration)
      .map(typeDispatcher::asASTArcConstraintDeclaration)
      .map(ASTArcConstraintDeclaration::getExpression)
      .forEach(e -> e.accept(traverser));
    return names;
  }

  protected SourcePosition getSourcePosition(ComponentTypeSymbol symbol,
                                             ASTComponentType node,
                                             String feature) {
    return ((IVariableArcScope) symbol.getSpannedScope()).getArcFeatureSymbols()
      .get(feature).stream().findFirst()
      .map(p -> p.getAstNode().get_SourcePositionStart())
      .orElse(node.get_SourcePositionEnd());
  }
}