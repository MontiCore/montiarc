/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTArcTypeParameter;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class TypeParamNameIsNoReservedKeyword implements GenericArcASTArcTypeParameterCoCo {

  protected final Collection<String> reservedKeywords;
  protected final String languageName;

  public TypeParamNameIsNoReservedKeyword(@NotNull String languageName, @NotNull Collection<String> reservedKeywords) {
    this.reservedKeywords = Preconditions.checkNotNull(reservedKeywords);
    this.languageName = Preconditions.checkNotNull(languageName);
  }

  @Override
  public void check(ASTArcTypeParameter node) {
    Preconditions.checkNotNull(node);
    String typeParamName = node.getName();

    if(reservedKeywords.stream().anyMatch(keyword -> keyword.equals(typeParamName))) {
      Log.error(
        ArcError.RESTRICTED_IDENTIFIER.format(typeParamName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
