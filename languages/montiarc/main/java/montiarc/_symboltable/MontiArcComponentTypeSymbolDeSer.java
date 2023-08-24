/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ArcBasisSymbols2Json;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolDeSer;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.serialization.json.JsonObject;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.IVariableArcComponentTypeSymbolDeSer;
import variablearc._symboltable.VariableArcVariationPointDeSer;
import variablearc.evaluation.ExpressionSetDeSer;

import java.io.IOException;
import java.util.Optional;

public class MontiArcComponentTypeSymbolDeSer extends ComponentTypeSymbolDeSer implements IVariableArcComponentTypeSymbolDeSer {

  protected final ExpressionSetDeSer expressionSetDeSer;
  protected final VariableArcVariationPointDeSer variationPointDeSer;

  public MontiArcComponentTypeSymbolDeSer() {
    expressionSetDeSer = new ExpressionSetDeSer(this::parse);
    variationPointDeSer = new VariableArcVariationPointDeSer(this::parse);
  }

  protected Optional<ASTExpression> parse(String e) {
    try {
      return MontiArcMill.parser().parse_StringExpression(e);
    } catch (IOException ex) {
      return Optional.empty();
    }
  }

  @Override
  protected void serializeAddons(@NotNull ComponentTypeSymbol toSerialize, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(toSerialize);
    Preconditions.checkNotNull(s2j);
    if (toSerialize instanceof IVariableArcComponentTypeSymbol) {
      serializeArcFeatures((IVariableArcComponentTypeSymbol) toSerialize, s2j);
      serializeConstraints((IVariableArcComponentTypeSymbol) toSerialize, s2j);
      serializeVariationPoint((IVariableArcComponentTypeSymbol) toSerialize, s2j);
    }
    super.serializeAddons(toSerialize, s2j);
  }

  @Override
  protected void deserializeAddons(@NotNull ComponentTypeSymbol component, @NotNull JsonObject json) {
    super.deserializeAddons(component, json);
    if (component instanceof IVariableArcComponentTypeSymbol) {
      deserializeArcFeatures((IVariableArcComponentTypeSymbol) component, json);
      deserializeConstraints((IVariableArcComponentTypeSymbol) component, json);
      deserializeVariationPoints((IVariableArcComponentTypeSymbol) component, json);
    }
  }

  @Override
  public ExpressionSetDeSer getExpressionSetDeSer() {
    return expressionSetDeSer;
  }

  @Override
  public VariableArcVariationPointDeSer getVariationPointDeSer() {
    return variationPointDeSer;
  }
}
