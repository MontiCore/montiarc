/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._visitor;

import arcbasis._visitor.IArcBasisDelegatorVisitor;

import java.util.Optional;

public interface IComfortableArcDelegatorVisitor
  extends IArcBasisDelegatorVisitor, ComfortableArcInheritanceVisitor {

  Optional<ComfortableArcVisitor> getComfortableArcVisitor();

  void setComfortableArcVisitor(ComfortableArcVisitor comfortableArcVisitor);
}