/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.features.syntax_highlighting;

import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassification;
import montiarc._lsp.language_access.MontiArcLanguageAccess;

import java.util.Optional;

public class MontiArcLexerProvider extends MontiArcLexerProviderTOP {

  public MontiArcLexerProvider(MontiArcLanguageAccess languageAccess) {
    super(languageAccess);
  }

  @Override
  protected Optional<TokenClassification> classifyToken(Token token) {
    // Overrides the classification to not use semantic highlighting and only syntax highlighting
    return Optional.empty();
  }
}
