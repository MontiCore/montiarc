/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcautomaton._symboltable.Port2EventDefAdapter;
import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.expressions.Expression;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class VariableArcFullVariantComponentTypeSymbol extends ComponentTypeSymbol {

  protected IVariableArcComponentTypeSymbol typeSymbol;
  protected Set<VariableArcVariationPoint> includedVariationPoints;
  protected ExpressionSet conditions;
  protected Map<PortSymbol, VariantPortSymbol> portSymbolMap;

  /**
   * Creates a new component variant type symbol.
   *
   * @param typeSymbol      The original type symbol this variant is derived of.
   * @param variationPoints The variation points included in this variant (is a subset of all parent variation points).
   */
  public VariableArcFullVariantComponentTypeSymbol(@NotNull IVariableArcComponentTypeSymbol typeSymbol,
                                               @NotNull Set<VariableArcVariationPoint> variationPoints,
                                               @NotNull ExpressionSet conditions,
                                               @NotNull List<CompKindExpression> superComponents) {
    super(typeSymbol.getTypeInfo().getName());
    Preconditions.checkNotNull(typeSymbol);
    this.typeSymbol = typeSymbol;
    this.portSymbolMap = new LinkedHashMap<>();
    this.parameter = typeSymbol.getTypeInfo().getParameterList();
    this.superComponents = typeSymbol.getTypeInfo().getSuperComponentsList();
    this.accessModifier = typeSymbol.getTypeInfo().getAccessModifier();
    this.fullName = typeSymbol.getTypeInfo().getFullName();
    this.packageName = typeSymbol.getTypeInfo().getPackageName();
    this.refinements = typeSymbol.getTypeInfo().getRefinementsList();
    this.spannedScope = typeSymbol.getTypeInfo().getSpannedScope();
    this.enclosingScope = typeSymbol.getTypeInfo().getEnclosingScope();
    this.setAstNodeAbsent();

    this.typeSymbol = typeSymbol;
    this.includedVariationPoints = variationPoints;
    this.conditions = conditions;
    this.superComponents = superComponents;

    if (this.typeSymbol.getTypeInfo().isPresentAstNode()) {
      // Shadow the AST structure
      this.setAstNode(new ASTVariableArcFullVariantComponentType((ASTArcComponentType) this.typeSymbol.getTypeInfo().getAstNode(), this));
    } else {
      this.setAstNodeAbsent();
    }
  }


  @Override
  public List<SubcomponentSymbol> getSubcomponents() {
    return typeSymbol.getTypeInfo().getSubcomponents().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public List<VariableSymbol> getFields() {
    return typeSymbol.getTypeInfo().getFields().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public Set<PortSymbol> getAllPorts() {
    return typeSymbol.getTypeInfo().getAllPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol).collect(Collectors.toSet());
  }

  @Override
  public List<PortSymbol> getPorts() {
    return typeSymbol.getTypeInfo().getPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol).collect(Collectors.toList());
  }

  @Override
  public Optional<PortSymbol> getPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getSpannedScope().resolvePortLocallyMany(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, this::containsSymbol).stream().findFirst().map(this::getVariantPortSymbol);
  }

  protected PortSymbol getVariantPortSymbol(PortSymbol port) {
    if (!portSymbolMap.containsKey(port)) {
      portSymbolMap.put(port, new VariantPortSymbol(port, this));
    }
    return portSymbolMap.get(port);
  }

  @Override
  public SourcePosition getSourcePosition() {
    return typeSymbol.getTypeInfo().getSourcePosition();
  }

  @Override
  public String getFullName() {
    return typeSymbol.getTypeInfo().getFullName();
  }

  public ComponentTypeSymbol getAdaptee() {
    return this.typeSymbol.getTypeInfo();
  }

  /**
   * @return All conditions that need to hold for this variant to be selected (including subcomponent conditions)
   */
  public ExpressionSet getConditions() {
    ExpressionSet conditions = this.conditions.copy().add(typeSymbol.getConstraints());
    for (SubcomponentSymbol instanceSymbol : getSubcomponents()) {
      // Adds the required conditions of subcomponents to this component
      conditions.add(((VariableArcVariantComponentTypeSymbol) instanceSymbol.getType().getTypeInfo()).getConditions()
        .copyAddPrefix(instanceSymbol.getName()));
    }
    return conditions;
  }

  /**
   * @return Local conditions that need to hold for this variant to be selected (excluding subcomponent conditions and constraints)
   */
  public ExpressionSet getLocalConditions() {
    return conditions;
  }


  public boolean containsSymbol(@NotNull ISymbol symbol) {
    if (symbol instanceof VariantPortSymbol) {
      symbol = ((VariantPortSymbol) symbol).getOriginal();
    }
    if (symbol instanceof Port2VariableAdapter) {
      symbol = ((Port2VariableAdapter) symbol).getAdaptee();
    }
    if (symbol instanceof Port2EventDefAdapter) {
      symbol = ((Port2EventDefAdapter) symbol).getAdaptee();
    }
    ISymbol finalSymbol = symbol;
    return typeSymbol.variationPointsContainSymbol(includedVariationPoints, symbol) ||
      !isEmptySuperComponents() && getSuperComponentsList().stream().anyMatch(parent -> ((VariableArcFullVariantComponentTypeSymbol) parent.getTypeInfo()).containsSymbol(finalSymbol) &&
        !((VariableArcFullVariantComponentTypeSymbol) parent.getTypeInfo()).isRootSymbol(finalSymbol));
  }

  public boolean isRootSymbol(ISymbol symbol) {
    return typeSymbol.isRootSymbol(symbol);
  }

  public Set<VariableArcVariationPoint> getIncludedVariationPoints() {
    return includedVariationPoints;
  }

  @Override
  public String toString() {
    return "Full-Variant (" + getIncludedVariationPoints().stream().map(VariableArcVariationPoint::getCondition).map(Expression::print).reduce((a, b) -> a + ", " + b).orElse("") + ")";
  }
}
