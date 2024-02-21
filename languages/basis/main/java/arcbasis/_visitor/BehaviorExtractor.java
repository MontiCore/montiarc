/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BehaviorExtractor extends ArcBasisInheritanceHandler implements ArcBasisVisitor2 {

  protected ArcBasisTraverser traverser;

  protected boolean isHandlingComponent;

  protected List<ASTArcBehaviorElement> foundBehaviors;

  public BehaviorExtractor() {
    isHandlingComponent = false;
    foundBehaviors = new ArrayList<>();
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ArcBasisTraverser traverser) {
    this.traverser = traverser;
  }

  public List<ASTArcBehaviorElement> getFoundBehaviors() {
    return foundBehaviors;
  }

  protected void clearFoundBehaviors() {
    foundBehaviors.clear();
  }

  @Override
  public void handle(ASTComponentType node) {
    if (!isHandlingComponent) {
      isHandlingComponent = true;
      super.handle(node);
      isHandlingComponent = false;
    }
  }

  @Override
  public void visit(ASTArcBehaviorElement node) {
    this.getFoundBehaviors().add(node);
  }


  public List<ASTArcBehaviorElement> findBehaviors(@NotNull ASTComponentType componentType) {
    Preconditions.checkNotNull(componentType);


    this.clearFoundBehaviors();
    this.setTraverser(ArcBasisMill.inheritanceTraverser());
    this.getTraverser().setArcBasisHandler(this);
    this.getTraverser().add4ArcBasis(this);

    componentType.accept(getTraverser());

    return this.getFoundBehaviors();
  }

  public <T extends ASTArcBehaviorElement> Stream<T> streamFindBehaviors(@NotNull ASTComponentType componentType, Class<T> typeToSearch) {
    Preconditions.checkNotNull(componentType);
    return this.findBehaviors(componentType).stream().filter(typeToSearch::isInstance)
      .map(typeToSearch::cast);
  }
}
