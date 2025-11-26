/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An abstract component variant implementation.
 * Can be used as a starting point for implementing custom variants (e.g. {@link VariableArcVariantComponentTypeSymbol}).
 */
public abstract class VariantArcComponentTypeSymbol extends ComponentTypeSymbol {

  protected ComponentTypeSymbol typeSymbol;
  protected Map<PortSymbol, VariantPortSymbol> portSymbolMap;

  protected VariantArcComponentTypeSymbol(@NotNull ComponentTypeSymbol typeSymbol) {
    super(typeSymbol.getName());
    Preconditions.checkNotNull(typeSymbol);
    this.typeSymbol = typeSymbol;
    this.portSymbolMap = new LinkedHashMap<>();
    this.parameter = typeSymbol.getParameterList();
    this.superComponents = typeSymbol.getSuperComponentsList();
    this.accessModifier = typeSymbol.getAccessModifier();
    this.fullName = typeSymbol.getFullName();
    this.packageName = typeSymbol.getPackageName();
    this.refinements = typeSymbol.getRefinementsList();
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
  public Set<PortSymbol> getAllPorts() {
    return typeSymbol.getAllPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol).collect(Collectors.toSet());
  }

  @Override
  public List<PortSymbol> getPorts() {
    return typeSymbol.getPorts().stream().filter(this::containsSymbol).map(this::getVariantPortSymbol).collect(Collectors.toList());
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
    return typeSymbol.getSourcePosition();
  }

  @Override
  public String getFullName() {
    return typeSymbol.getFullName();
  }

  public ComponentTypeSymbol getAdaptee() {
    return this.typeSymbol;
  }
}
