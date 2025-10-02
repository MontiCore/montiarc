/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.features.syntax_highlighting.rule;

import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.syntax_highlighting.HighlightLocalKeywordRule;

public class MAHighlightLocalKeywordRule extends HighlightLocalKeywordRule {

  public boolean matches(Token token) {
    return token.getMatchedToken().filter((mt) -> mt.isLocalKeyword && !mt.tokenPathMatches(".*mCPrimitiveType.*")).isPresent();
  }
}
