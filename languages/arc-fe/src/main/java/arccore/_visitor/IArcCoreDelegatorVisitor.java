/* (c) https://github.com/MontiCore/monticore */
package arccore._visitor;

import comfortablearc._visitor.IComfortableArcDelegatorVisitor;
import genericarc._visitor.IGenericArcDelegatorVisitor;

import java.util.Optional;

public interface IArcCoreDelegatorVisitor
  extends IComfortableArcDelegatorVisitor, IGenericArcDelegatorVisitor, ArcCoreInheritanceVisitor {

  Optional<ArcCoreVisitor> getArcCoreVisitor();

  void setArcCoreVisitor(ArcCoreVisitor arcCoreVisitor);
}