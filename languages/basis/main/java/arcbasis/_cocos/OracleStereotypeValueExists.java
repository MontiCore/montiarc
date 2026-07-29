/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Set;

public class OracleStereotypeValueExists implements ArcBasisASTArcComponentTypeCoCo {

  final Set<String> oracleTypes;

  public OracleStereotypeValueExists() {
    this.oracleTypes = Set.of("first", "last", "leastUsed", "unexplored", "lowestHash", "random");
  }

  protected Set<String> getOracleTypes() {
    return oracleTypes;
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if (node.isPresentStereotype()) {
      for (ASTStereoValue value : node.getStereotype().getValuesList()) {
        check(value);
      }
    }
  }

  protected void check(@NotNull ASTStereoValue value) {
    Preconditions.checkNotNull(value);

    if (value.getName().equals("oracle")) {
      if (!value.isPresentText() || !getOracleTypes().contains(value.getValue())) {
        if (!value.isPresentExpression()) {
          Log.error(ArcError.UNEXPECTED_ORACLE_TYPE.format("", false),
            value.get_SourcePositionStart(), value.get_SourcePositionEnd());
        } else {
          Log.error(ArcError.UNEXPECTED_ORACLE_TYPE.format(ArcBasisMill.prettyPrint(value.getExpression(), false)),
            value.get_SourcePositionStart(), value.get_SourcePositionEnd());
        }
      }
    }
  }
}
