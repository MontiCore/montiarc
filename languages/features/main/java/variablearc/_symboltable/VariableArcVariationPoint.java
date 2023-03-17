/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTArcElement;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.evaluation.Expression;

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

  public VariableArcVariationPoint(@NotNull Expression condition,
                                   @Nullable VariableArcVariationPoint dependsOn) {
    Preconditions.checkNotNull(condition);

    this.condition = condition;
    this.dependsOn = dependsOn;
    this.symbols = new ArrayList<>();
    this.elements = new ArrayList<>();
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

  public List<ASTArcElement> getElements() {
    return elements;
  }

  public boolean containsSymbol(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return symbols.contains(symbol);
  }

  public boolean containsElement(@NotNull ASTArcElement element) {
    Preconditions.checkNotNull(element);
    return elements.contains(element);
  }

  /**
   * Adds a child variation point
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

  public void add(@NotNull ASTArcElement element) {
    Preconditions.checkNotNull(element);
    this.elements.add(element);
  }
}
