/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariantComponentType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a configured component type variant. Excludes all symbols not found in this specific variant.
 */
public class VariantComponentTypeSymbol extends VariableComponentTypeSymbol {

  protected VariableComponentTypeSymbol parent;
  protected Set<VariableArcVariationPoint> includedVariationPoints;
  protected Map<ComponentInstanceSymbol, VariantComponentInstanceSymbol> subcomponentMap;

  protected VariantComponentTypeSymbol(@NotNull VariableComponentTypeSymbol type,
                                       @NotNull Set<VariableArcVariationPoint> variationPoints) {
    this(type, variationPoints, Collections.emptyMap());
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
                                       @NotNull Map<ComponentInstanceSymbol, VariantComponentInstanceSymbol> subcomponentMap) {
    super(type.getName());

    parent = type;
    includedVariationPoints = variationPoints;
    this.subcomponentMap = subcomponentMap;
  }

  public boolean containsSymbol(@NotNull ISymbol symbol) {
    return variationPointsContainSymbol(includedVariationPoints, symbol);
  }

  public Set<VariableArcVariationPoint> getIncludedVariationPoints() {
    return includedVariationPoints;
  }

  @Override
  public List<VariableArcVariationPoint> getAllVariationPoints() {
    return parent.getAllVariationPoints();
  }

  @Override
  public List<VariantComponentTypeSymbol> getVariants() {
    return parent.getVariants();
  }

  @Override
  public List<VariableSymbol> getFields() {
    return parent.getFields().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public List<PortSymbol> getAllPorts() {
    return parent.getAllPorts().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public List<PortSymbol> getPorts() {
    return parent.getPorts().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public Optional<PortSymbol> getPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getSpannedScope().resolvePortLocallyMany(
      false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, this::containsSymbol
    ).stream().findFirst();
  }

  @Override
  public List<TypeVarSymbol> getTypeParameters() {
    return parent.getTypeParameters();
  }

  @Override
  public List<ComponentInstanceSymbol> getSubComponents() {
    return parent.getSubComponents().stream()
      .filter(this::containsSymbol)
      .map(e -> Optional.ofNullable((ComponentInstanceSymbol) subcomponentMap.get(e)).orElse(e))
      .collect(Collectors.toList());
  }

  @Override
  public IArcBasisScope getSpannedScope() {
    return parent.getSpannedScope();
  }

  @Override
  public IArcBasisScope getEnclosingScope() {
    return parent.getEnclosingScope();
  }

  @Override
  public ASTComponentType getAstNode() {
    if (isPresentAstNode()) {
      return new ASTVariantComponentType(parent.getAstNode(), this);
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean isPresentAstNode() {
    return parent.isPresentAstNode();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return parent.getSourcePosition();
  }
}
