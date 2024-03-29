/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTArcElement;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a variable point in a Component. Variations are structured as a tree.
 * The {@code condition} has to be fulfilled for the {@code symbols} to be added to the component.
 */
public class VariableArcVariationPoint {

  protected Expression condition;

  protected VariableArcVariationPoint dependsOn;
  protected List<VariableArcVariationPoint> childVariationPoints;

  protected List<ISymbol> symbols;
  protected List<ASTArcElement> elements;


  public VariableArcVariationPoint(@NotNull Expression condition) {
    this(condition, null);
  }

  public VariableArcVariationPoint(@NotNull Expression condition, @Nullable VariableArcVariationPoint dependsOn) {
    this(condition, dependsOn, Collections.emptyList());
  }

  public VariableArcVariationPoint(@NotNull Expression condition,
                                   @Nullable VariableArcVariationPoint dependsOn,
                                   @NotNull List<ASTArcElement> elements) {
    Preconditions.checkNotNull(condition);
    Preconditions.checkNotNull(elements);

    this.condition = condition;
    this.dependsOn = dependsOn;
    this.symbols = new ArrayList<>();
    this.elements = elements;
    this.childVariationPoints = new ArrayList<>();
    if (dependsOn != null) {
      dependsOn.addChild(this);
    }
  }

  public Optional<VariableArcVariationPoint> getDependsOn() {
    return Optional.ofNullable(this.dependsOn);
  }

  public List<VariableArcVariationPoint> getChildVariationPoints() {
    return childVariationPoints;
  }

  public Expression getCondition() {
    return condition;
  }

  public List<Expression> getAllConditions() {
    if (this.getDependsOn().isEmpty()) {
      return new ArrayList<>(Collections.singletonList(condition));
    } else {
      List<Expression> parent = this.getDependsOn().get().getAllConditions();
      parent.add(condition);
      return parent;
    }
  }

  public List<ASTArcElement> getArcElements() {
    return elements;
  }

  public List<ISymbol> getSymbols() {
    return symbols;
  }

  public boolean containsSymbol(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return symbols.contains(symbol);
  }

  /**
   * Adds a child variation point
   *
   * @param variationPoint a variation point that depends on this
   */
  public void addChild(@NotNull VariableArcVariationPoint variationPoint) {
    Preconditions.checkNotNull(variationPoint);
    Preconditions.checkArgument(variationPoint.getDependsOn().isPresent());
    Preconditions.checkArgument(variationPoint.getDependsOn().get().equals(this));
    this.childVariationPoints.add(variationPoint);
  }

  public void add(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    this.symbols.add(symbol);
  }
}
