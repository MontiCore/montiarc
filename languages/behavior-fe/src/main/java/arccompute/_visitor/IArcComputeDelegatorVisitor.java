/* (c) https://github.com/MontiCore/monticore */
package arccompute._visitor;

import arcbehaviorbasis._visitor.IArcBehaviorBasisDelegatorVisitor;
import de.monticore.statements.mcstatementsbasis._visitor.MCStatementsBasisVisitor;

import java.util.Optional;

public interface IArcComputeDelegatorVisitor
  extends IArcBehaviorBasisDelegatorVisitor, ArcComputeInheritanceVisitor {

  Optional<MCStatementsBasisVisitor> getMCStatementsBasisVisitor();

  void setMCStatementsBasisVisitor(MCStatementsBasisVisitor mCStatementsBasisVisitor);

  Optional<ArcComputeVisitor> getArcComputeVisitor();

  void setArcComputeVisitor(ArcComputeVisitor arcComputeVisitor);

}