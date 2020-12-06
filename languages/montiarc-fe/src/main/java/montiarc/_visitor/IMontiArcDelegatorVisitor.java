/* (c) https://github.com/MontiCore/monticore */
package montiarc._visitor;

import arccore._visitor.IArcCoreDelegatorVisitor;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor;
import de.monticore.statements.mcstatementsbasis._visitor.MCStatementsBasisVisitor;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor;

import java.util.Optional;

public interface IMontiArcDelegatorVisitor
  extends IArcCoreDelegatorVisitor, MontiArcInheritanceVisitor {

  Optional<MCCommonLiteralsVisitor> getMCCommonLiteralsVisitor();

  void setMCCommonLiteralsVisitor(MCCommonLiteralsVisitor mCCommonLiteralsVisitor);

  Optional<CommonExpressionsVisitor> getCommonExpressionsVisitor();

  void setCommonExpressionsVisitor(CommonExpressionsVisitor commonExpressionsVisitor);

  Optional<AssignmentExpressionsVisitor> getAssignmentExpressionsVisitor();

  void setAssignmentExpressionsVisitor(AssignmentExpressionsVisitor assignmentExpressionsVisitor);

  Optional<MCSimpleGenericTypesVisitor> getMCSimpleGenericTypesVisitor();

  void setMCSimpleGenericTypesVisitor(MCSimpleGenericTypesVisitor mCSimpleGenericTypesVisitor);

  Optional<MCCommonStatementsVisitor> getMCCommonStatementsVisitor();

  void setMCCommonStatementsVisitor(MCCommonStatementsVisitor mCCommonStatementsVisitor);

  Optional<MCVarDeclarationStatementsVisitor> getMCVarDeclarationStatementsVisitor();

  void setMCVarDeclarationStatementsVisitor(MCVarDeclarationStatementsVisitor mCVarDeclarationStatementsVisitor);

  Optional<MCCollectionTypesVisitor> getMCCollectionTypesVisitor();

  void setMCCollectionTypesVisitor(MCCollectionTypesVisitor mCCollectionTypesVisitor);

  Optional<MCStatementsBasisVisitor> getMCStatementsBasisVisitor();

  void setMCStatementsBasisVisitor(MCStatementsBasisVisitor mCStatementsBasisVisitor);

  Optional<MontiArcVisitor> getMontiArcVisitor();

  void setMontiArcVisitor(MontiArcVisitor montiArcVisitor);
}