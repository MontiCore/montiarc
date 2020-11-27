/* (c) https://github.com/MontiCore/monticore */
package genericarc._visitor;

import arcbasis._visitor.IArcBasisDelegatorVisitor;

import java.util.Optional;

public interface IGenericArcDelegatorVisitor
  extends IArcBasisDelegatorVisitor, GenericArcInheritanceVisitor {

  Optional<GenericArcVisitor> getGenericArcVisitor();

  void setGenericArcVisitor(GenericArcVisitor genericArcVisitor);
}