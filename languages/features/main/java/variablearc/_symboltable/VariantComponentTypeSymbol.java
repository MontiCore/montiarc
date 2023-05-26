/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc._ast.ASTVariantComponentType;
import variablearc.evaluation.ExpressionSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a configured component type variant. Excludes all symbols not found in this specific variant.
 */
public class VariantComponentTypeSymbol extends VariableComponentTypeSymbol {

  protected VariableComponentTypeSymbol typeSymbol;
  protected Set<VariableArcVariationPoint> includedVariationPoints;
  protected Map<ComponentInstanceSymbol, VariantComponentInstanceSymbol> subcomponentMap;

  protected Map<PortSymbol, VariantPortSymbol> portSymbolMap;

  protected VariantComponentTypeSymbol(@NotNull VariableComponentTypeSymbol type,
                                       @NotNull Set<VariableArcVariationPoint> variationPoints,
                                       @NotNull ExpressionSet conditions,
                                       @Nullable CompTypeExpression parent) {
    this(type, variationPoints, conditions, parent, Collections.emptyMap());
  }

  /**
   * Creates a new component variant type symbol.
   *
   * @param type            The original type symbol this variant is derived of.
   * @param variationPoints The variation points included in this variant (is a subset of all parent variation points).
   * @param subcomponentMap A mapping of component instance symbol to their configured instance variants.
   */
  protected VariantComponentTypeSymbol(@NotNull VariableComponentTypeSymbol type,
                                       @NotNull Set<VariableArcVariationPoint> variationPoints,
                                       @NotNull ExpressionSet conditions,
                                       @Nullable CompTypeExpression parent,
                                       @NotNull Map<ComponentInstanceSymbol, VariantComponentInstanceSymbol> subcomponentMap) {
    super(type.getName());

    typeSymbol = type;
    includedVariationPoints = variationPoints;
    this.setAstNodeAbsent();
    this.conditions = conditions.add(type.getConditions());
    this.parent = Optional.ofNullable(parent);
    this.subcomponentMap = subcomponentMap;
    for (VariantComponentInstanceSymbol instanceSymbol : subcomponentMap.values()) {
      // Adds the required conditions of subcomponents to this component
      conditions.add(((VariantComponentTypeSymbol) instanceSymbol.getType().getTypeInfo()).getConditions()
        .copyAddPrefix(instanceSymbol.getName()));
    }
    portSymbolMap = new HashMap<>();

    if (typeSymbol.isPresentAstNode()) {
      // Shadow the AST structure
      this.setAstNode(new ASTVariantComponentType(typeSymbol.getAstNode(), this));
    } else {
      this.setAstNodeAbsent();
    }
  }

  /**
   * @return All conditions that need to hold for this variant to be selected (including subcomponent conditions)
   */
  @Override
  public ExpressionSet getConditions() {
    return conditions;
  }

  public boolean containsSymbol(@NotNull ISymbol symbol) {
    return variationPointsContainSymbol(includedVariationPoints, symbol) ||
      isPresentParent() && ((VariantComponentTypeSymbol) getParent().getTypeInfo()).containsSymbol(symbol) &&
        !((VariantComponentTypeSymbol) getParent().getTypeInfo()).isRootSymbol(symbol);
  }

  public Set<VariableArcVariationPoint> getIncludedVariationPoints() {
    return includedVariationPoints;
  }

  @Override
  public List<VariableArcVariationPoint> getAllVariationPoints() {
    return typeSymbol.getAllVariationPoints();
  }

  @Override
  public List<VariantComponentTypeSymbol> getVariants() {
    return typeSymbol.getVariants();
  }

  @Override
  public List<VariableSymbol> getFields() {
    return typeSymbol.getFields().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public List<PortSymbol> getAllPorts() {
    return typeSymbol.getAllPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol)
      .collect(Collectors.toList());
  }

  @Override
  public List<PortSymbol> getPorts() {
    return typeSymbol.getPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol)
      .collect(Collectors.toList());
  }

  @Override
  public Optional<PortSymbol> getPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getSpannedScope().resolvePortLocallyMany(
      false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, this::containsSymbol
    ).stream().findFirst().map(this::getVariantPortSymbol);
  }

  protected PortSymbol getVariantPortSymbol(PortSymbol port) {
    if (!portSymbolMap.containsKey(port)) {
      portSymbolMap.put(port, new VariantPortSymbol(port, this));
    }
    return portSymbolMap.get(port);
  }

  @Override
  public List<TypeVarSymbol> getTypeParameters() {
    return typeSymbol.getTypeParameters();
  }

  @Override
  public List<ComponentInstanceSymbol> getSubComponents() {
    return typeSymbol.getSubComponents().stream()
      .filter(this::containsSymbol)
      .map(e -> Optional.ofNullable((ComponentInstanceSymbol) subcomponentMap.get(e)).orElse(e))
      .collect(Collectors.toList());
  }

  @Override
  public IArcBasisScope getSpannedScope() {
    return typeSymbol.getSpannedScope();
  }

  @Override
  public IArcBasisScope getEnclosingScope() {
    return typeSymbol.getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return typeSymbol.getSourcePosition();
  }
}
