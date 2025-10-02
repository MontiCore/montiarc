/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.features.syntax_highlighting.rule;

import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.sematic_tokens.impl.SemanticTokenTypesWrapper;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassification;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassificationRule;

import java.util.Optional;

public class HighlightComponentNameRule implements TokenClassificationRule {

  @Override
  public boolean matches(Token token) {
    return (token.tokenPathMatches("mACompilationUnit.arcComponentType") || token.tokenPathMatches(
      ".*.arcComponentType") || token.tokenPathMatches(".*.cDClass.cDExtendUsage.*.mCQualifiedName"))
      && token.isNameToken();
  }

  @Override
  public Optional<TokenClassification> apply(Token token) {
    return Optional.of(new TokenClassification(SemanticTokenTypesWrapper.Class));
  }
}
