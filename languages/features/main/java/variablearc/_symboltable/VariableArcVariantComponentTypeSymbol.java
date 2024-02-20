/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.Port2VariableAdapter;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariantComponentType;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.expressions.Expression;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a configured component type variant. Excludes all symbols not found in this specific variant.
 */
public class VariableArcVariantComponentTypeSymbol extends ComponentTypeSymbol {

  protected IVariableArcComponentTypeSymbol typeSymbol;
  protected Set<VariableArcVariationPoint> includedVariationPoints;
  protected ExpressionSet conditions;
  protected Map<SubcomponentSymbol, VariantSubcomponentSymbol> subcomponentMap;

  protected Map<ArcPortSymbol, VariantPortSymbol> portSymbolMap;

  public VariableArcVariantComponentTypeSymbol(@NotNull IVariableArcComponentTypeSymbol type,
                                               @NotNull Set<VariableArcVariationPoint> variationPoints,
                                               @NotNull ExpressionSet conditions,
                                               @NotNull List<CompKindExpression> superComponents) {
    this(type, variationPoints, conditions, superComponents, Collections.emptyMap());
  }

  /**
   * Creates a new component variant type symbol.
   *
   * @param type            The original type symbol this variant is derived of.
   * @param variationPoints The variation points included in this variant (is a subset of all parent variation points).
   * @param subcomponentMap A mapping of component instance symbol to their configured instance variants.
   */
  public VariableArcVariantComponentTypeSymbol(@NotNull IVariableArcComponentTypeSymbol type,
                                               @NotNull Set<VariableArcVariationPoint> variationPoints,
                                               @NotNull ExpressionSet conditions,
                                               @NotNull List<CompKindExpression> superComponents,
                                               @NotNull Map<SubcomponentSymbol, VariantSubcomponentSymbol> subcomponentMap) {
    super(type.getTypeInfo().getName());

    typeSymbol = type;
    includedVariationPoints = variationPoints;
    this.setAstNodeAbsent();
    this.conditions = conditions.add(type.getConstraints());
    this.superComponents = superComponents;
    this.subcomponentMap = subcomponentMap;
    for (VariantSubcomponentSymbol instanceSymbol : subcomponentMap.values()) {
      // Adds the required conditions of subcomponents to this component
      conditions.add(((VariableArcVariantComponentTypeSymbol) instanceSymbol.getType().getTypeInfo()).getConditions()
        .copyAddPrefix(instanceSymbol.getName()));
    }
    portSymbolMap = new HashMap<>();

    if (typeSymbol.getTypeInfo().isPresentAstNode()) {
      // Shadow the AST structure
      this.setAstNode(new ASTVariantComponentType(typeSymbol.getTypeInfo().getAstNode(), this));
    } else {
      this.setAstNodeAbsent();
    }
  }

  /**
   * @return All conditions that need to hold for this variant to be selected (including subcomponent conditions)
   */
  public ExpressionSet getConditions() {
    return conditions;
  }

  public boolean containsSymbol(@NotNull ISymbol symbol) {
    if (symbol instanceof Port2VariableAdapter) {
      symbol = ((Port2VariableAdapter) symbol).getAdaptee();
    }
    ISymbol finalSymbol = symbol;
    return typeSymbol.variationPointsContainSymbol(includedVariationPoints, symbol) ||
      !isEmptySuperComponents() && getSuperComponentsList().stream().anyMatch(parent -> ((VariableArcVariantComponentTypeSymbol) parent.getTypeInfo()).containsSymbol(finalSymbol) &&
        !((VariableArcVariantComponentTypeSymbol) parent.getTypeInfo()).isRootSymbol(finalSymbol));
  }

  public boolean isRootSymbol(ISymbol symbol) {
    return typeSymbol.isRootSymbol(symbol);
  }

  public Set<VariableArcVariationPoint> getIncludedVariationPoints() {
    return includedVariationPoints;
  }

  @Override
  public List<VariableSymbol> getFields() {
    return typeSymbol.getTypeInfo().getFields().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public List<ArcPortSymbol> getAllArcPorts() {
    return typeSymbol.getTypeInfo().getAllArcPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol)
      .collect(Collectors.toList());
  }

  @Override
  public List<ArcPortSymbol> getArcPorts() {
    return typeSymbol.getTypeInfo().getArcPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol)
      .collect(Collectors.toList());
  }

  @Override
  public Optional<ArcPortSymbol> getArcPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getSpannedScope().resolveArcPortLocallyMany(
      false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, this::containsSymbol
    ).stream().findFirst().map(this::getVariantPortSymbol);
  }

  protected ArcPortSymbol getVariantPortSymbol(ArcPortSymbol port) {
    if (!portSymbolMap.containsKey(port)) {
      portSymbolMap.put(port, new VariantPortSymbol(port, this));
    }
    return portSymbolMap.get(port);
  }

  @Override
  public List<TypeVarSymbol> getTypeParameters() {
    return typeSymbol.getTypeInfo().getTypeParameters();
  }

  @Override
  public List<SubcomponentSymbol> getSubcomponents() {
    return typeSymbol.getTypeInfo().getSubcomponents().stream()
      .filter(this::containsSymbol)
      .map(e -> Optional.ofNullable((SubcomponentSymbol) subcomponentMap.get(e)).orElse(e))
      .collect(Collectors.toList());
  }

  @Override
  public IArcBasisScope getSpannedScope() {
    return typeSymbol.getTypeInfo().getSpannedScope();
  }

  @Override
  public IArcBasisScope getEnclosingScope() {
    return typeSymbol.getTypeInfo().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return typeSymbol.getTypeInfo().getSourcePosition();
  }

  @Override
  public List<ASTArcArgument> getParentConfiguration(CompKindExpression parent) {
    return typeSymbol.getTypeInfo().getParentConfiguration(parent);
  }

  @Override
  public String toString() {
    return "Variant (" + getIncludedVariationPoints().stream().map(VariableArcVariationPoint::getCondition).map(Expression::print).reduce((a, b) -> a + ", " + b).orElse("") + ")";
  }
}
