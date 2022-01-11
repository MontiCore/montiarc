/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._symboltable;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.*;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis.check.CompTypeExpression;
import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._ast.ASTModeDeclaration;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wraps a component-type-symbol, so that its method account for additional elements that are added by a certain mode.
 * This class kind of immutable, since methods that manipulate this object (like setters) are not forwarded
 * to the {@link #original wrapped element} and thus have no effect.
 */
public class DynmaicVersionOfAComponentSymbol extends ComponentTypeSymbol {

  protected final ComponentTypeSymbol original;
  protected final ASTComponentType ast;
  protected final ModeSymbol mode;

  /**
   * @param ast an already modified ast-element that accounts for the dynamic changes. It is also used to derive the name for this symbol
   * @param original base-symbol that should be wrapped here
   * @param mode the mode for which the component symbol should be wrapped
   */
  public DynmaicVersionOfAComponentSymbol(ASTComponentType ast, ComponentTypeSymbol original, ModeSymbol mode) {
    super(Preconditions.checkNotNull(ast).getName());
    this.original = Preconditions.checkNotNull(original);
    this.mode = Preconditions.checkNotNull(mode);
    this.ast = ast;
  }

  /**
   * @return all additional scopes that are associated with {@link #mode}
   */
  public Stream<IBasicModeAutomataScope> streamSpannedModeScopes() {
    return BasicModeAutomataMill.getModeTool().streamDeclarations(original.getAstNode()).filter(d -> d.containsName(mode.getName())).map(ASTModeDeclaration::getSpannedScope);
  }

  /**
   * @return all sub-scopes that are relevant in this mode
   */
  public Stream<IArcBasisScope> streamSpannedScopes() {
    return Stream.concat(Stream.of(original.getSpannedScope()), streamSpannedModeScopes());
  }

  /*---------------------------------------------------------------------*\
  |*- Overwriting hand-written methods ----------------------------------*|
  \*---------------------------------------------------------------------*/

  @Override
  public List<ComponentTypeSymbol> getInnerComponents() {
    return streamSpannedScopes().map(IArcBasisScopeTOP::getLocalComponentTypeSymbols).flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public Optional<ComponentTypeSymbol> getInnerComponent(String name) {
    Preconditions.checkNotNull(name);
    return streamSpannedScopes().map(s -> s.resolveComponentTypeLocally(name)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
  }

  @Override
  public boolean isInnerComponent() {
    return original.isInnerComponent();
  }

  @Override
  public Optional<ComponentTypeSymbol> getOuterComponent() {
    return original.getOuterComponent();
  }

  @Override
  public boolean isPresentParentComponent() {
    return original.isPresentParentComponent();
  }

  @Override
  public CompTypeExpression getParent() {
    return original.getParent();
  }

  @Override
  public List<VariableSymbol> getParameters() {
    return original.getParameters();
  }

  @Override
  public List<TypeVarSymbol> getTypeParameters() {
    return original.getTypeParameters();
  }

  @Override
  public List<VariableSymbol> getFields() {
    return streamSpannedScopes().map(IBasicSymbolsScope::getLocalVariableSymbols).flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public List<PortSymbol> getPorts() {
    return streamSpannedScopes().map(IArcBasisScopeTOP::getLocalPortSymbols).flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public Optional<PortSymbol> getPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return streamSpannedScopes().map(s -> s.resolvePortLocally(name)).filter(Optional::isPresent).map(Optional::get).findFirst();
  }

  @Override
  public List<ComponentInstanceSymbol> getSubComponents() {
    return streamSpannedScopes().map(IArcBasisScopeTOP::getLocalComponentInstanceSymbols).flatMap(Collection::stream).collect(Collectors.toList());
  }

  /*---------------------------------------------------------------------*\
  |*- Overwriting generated methods -------------------------------------*|
  \*---------------------------------------------------------------------*/

  @Override
  public IArcBasisScope getEnclosingScope() {
    return original.getEnclosingScope();
  }

  @Override
  public ASTComponentType getAstNode() {
    return ast;
  }

  @Override
  public boolean isPresentAstNode() {
    return true;
  }

  @Override
  public AccessModifier getAccessModifier() {
    return original.getAccessModifier();
  }

  @Override
  public String getFullName() {
    return original.getFullName();
  }

  @Override
  public String getPackageName() {
    return original.getPackageName();
  }

  @Override
  public void accept(ArcBasisTraverser visitor) {
    // does it work like this?
    original.accept(visitor);
    streamSpannedModeScopes().forEach(visitor::handle);
  }

  @Override
  protected String determinePackageName() {
    return original.getPackageName();
  }

  @Override
  protected String determineFullName() {
    return original.getFullName();
  }

  @Override
  public IArcBasisScope getSpannedScope() {
    Log.warn("The returned scope does not contain sub-elements introduced by modes.");
    return original.getSpannedScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return original.getSourcePosition();
  }

  @Override
  public String toString() {
    return original.toString();
  }
}