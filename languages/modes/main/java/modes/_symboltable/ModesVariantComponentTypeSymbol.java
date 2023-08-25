/* (c) https://github.com/MontiCore/monticore */
package modes._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.Port2VariableAdapter;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.SourcePosition;
import modes._ast.ASTVariantComponentType;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a configured component type variant. Includes all symbols found in the mode.
 */
public class ModesVariantComponentTypeSymbol extends ComponentTypeSymbol {

  protected ComponentTypeSymbol typeSymbol;
  protected ArcModeSymbol mode;

  public ModesVariantComponentTypeSymbol(@NotNull ComponentTypeSymbol type, @NotNull ArcModeSymbol mode) {
    super(type.getName());
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(mode);

    typeSymbol = type;
    this.mode = mode;

    if (typeSymbol.isPresentAstNode() && mode.isPresentAstNode()) {
      // Shadow the AST structure
      this.setAstNode(new ASTVariantComponentType(typeSymbol.getAstNode(), this));
    } else {
      this.setAstNodeAbsent();
    }
  }

  public ArcModeSymbol getMode() {
    return mode;
  }

  @Override
  public List<VariableSymbol> getFields() {
    List<VariableSymbol> fields = new ArrayList<>(typeSymbol.getFields());
    fields.addAll(mode.getSpannedScope().getLocalVariableSymbols().stream()
      .filter(f -> !(f instanceof Port2VariableAdapter))
      .collect(Collectors.toList()));
    return fields;
  }

  @Override
  public List<TypeVarSymbol> getTypeParameters() {
    return typeSymbol.getTypeParameters();
  }

  @Override
  public List<ComponentInstanceSymbol> getSubComponents() {
    List<ComponentInstanceSymbol> subComponents = new ArrayList<>(typeSymbol.getSubComponents());
    subComponents.addAll(mode.getSpannedScope().getLocalComponentInstanceSymbols());
    return subComponents;
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

  @Override
  public String toString() {
    return "Mode (" + mode.getName() + ")";
  }
}
