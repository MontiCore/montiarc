/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis._visitor;

import java.util.Optional;

public interface IArcBehaviorBasisDelegatorVisitor
  extends ArcBehaviorBasisInheritanceVisitor {

  Optional<ArcBehaviorBasisVisitor> getArcBehaviorBasisVisitor();

  void setArcBehaviorBasisVisitor(ArcBehaviorBasisVisitor arcBehaviorBasisVisitor);
}