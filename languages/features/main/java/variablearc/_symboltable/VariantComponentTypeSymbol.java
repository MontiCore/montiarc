/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An abstract component variant implementation.
 * Can be used as a starting point for implementing custom variants (e.g. {@link VariableArcVariantComponentTypeSymbol}).
 */
public abstract class VariantComponentTypeSymbol extends ComponentTypeSymbol {

  protected ComponentTypeSymbol typeSymbol;
  protected Map<ArcPortSymbol, VariantPortSymbol> portSymbolMap;

  protected VariantComponentTypeSymbol(@NotNull ComponentTypeSymbol typeSymbol) {
    super(typeSymbol.getName());
    Preconditions.checkNotNull(typeSymbol);
    this.typeSymbol = typeSymbol;
    this.portSymbolMap = new HashMap<>();
    this.parameters = typeSymbol.getParameters();
    this.superComponents = typeSymbol.getSuperComponentsList();
    this.accessModifier = typeSymbol.getAccessModifier();
    this.fullName = typeSymbol.getFullName();
    this.packageName = typeSymbol.getPackageName();
    this.refinements = typeSymbol.getRefinementsList();
    this.outerComponent = typeSymbol.getOuterComponent().orElse(null);
    this.spannedScope = typeSymbol.getSpannedScope();
    this.enclosingScope = typeSymbol.getEnclosingScope();
    this.setAstNodeAbsent();
  }

  public abstract boolean containsSymbol(ISymbol symbol);

  @Override
  public List<SubcomponentSymbol> getSubcomponents() {
    return typeSymbol.getSubcomponents().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public List<VariableSymbol> getFields() {
    return typeSymbol.getFields().stream().filter(this::containsSymbol).collect(Collectors.toList());
  }

  @Override
  public List<ArcPortSymbol> getAllArcPorts() {
    return typeSymbol.getAllArcPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol).collect(Collectors.toList());
  }

  @Override
  public List<ArcPortSymbol> getArcPorts() {
    return typeSymbol.getArcPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol).collect(Collectors.toList());
  }

  @Override
  public Optional<ArcPortSymbol> getArcPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getSpannedScope().resolveArcPortLocallyMany(false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, this::containsSymbol).stream().findFirst().map(this::getVariantPortSymbol);
  }

  protected ArcPortSymbol getVariantPortSymbol(ArcPortSymbol port) {
    if (!portSymbolMap.containsKey(port)) {
      portSymbolMap.put(port, new VariantPortSymbol(port, this));
    }
    return portSymbolMap.get(port);
  }

  @Override
  public SourcePosition getSourcePosition() {
    return typeSymbol.getSourcePosition();
  }

  @Override
  public List<ASTArcArgument> getParentConfiguration(CompKindExpression parent) {
    return typeSymbol.getParentConfiguration(parent);
  }

  @Override
  public String getFullName() {
    return typeSymbol.getFullName();
  }

}
