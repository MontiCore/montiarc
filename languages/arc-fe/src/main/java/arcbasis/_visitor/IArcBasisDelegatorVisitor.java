/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import montiarc.util._visitor.IArcDelegatorVisitor;

import java.util.Optional;

public interface IArcBasisDelegatorVisitor
  extends IArcDelegatorVisitor, ArcBasisInheritanceVisitor {

  Optional<MCBasicTypesVisitor> getMCBasicTypesVisitor();

  void setMCBasicTypesVisitor(MCBasicTypesVisitor mCBasicTypesVisitor);

  Optional<ArcBasisVisitor> getArcBasisVisitor();

  void setArcBasisVisitor(ArcBasisVisitor arcBasisVisitor);
}