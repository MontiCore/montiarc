/* (c) https://github.com/MontiCore/monticore */
package modes._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.Port2VariableAdapter;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import modes._ast.ASTVariantComponentType;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.VariantComponentTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a configured component type variant. Includes all symbols found in the mode.
 */
public class ModesVariantComponentTypeSymbol extends VariantComponentTypeSymbol {

  protected ArcModeSymbol mode;

  public ModesVariantComponentTypeSymbol(@NotNull ComponentTypeSymbol typeSymbol, @NotNull ArcModeSymbol mode) {
    super(typeSymbol);
    Preconditions.checkNotNull(typeSymbol);
    Preconditions.checkNotNull(mode);

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
    List<VariableSymbol> fields = new ArrayList<>(super.getFields());
    fields.addAll(mode.getSpannedScope().getLocalVariableSymbols().stream()
      .filter(f -> !(f instanceof Port2VariableAdapter))
      .collect(Collectors.toList()));
    return fields;
  }

  @Override
  public List<SubcomponentSymbol> getSubcomponents() {
    List<SubcomponentSymbol> subComponents = new ArrayList<>(super.getSubcomponents());
    subComponents.addAll(mode.getSpannedScope().getLocalSubcomponentSymbols());
    return subComponents;
  }

  @Override
  public String toString() {
    return "Mode (" + mode.getName() + ")";
  }

  @Override
  public boolean containsSymbol(ISymbol symbol) {
    return true;
  }
}
