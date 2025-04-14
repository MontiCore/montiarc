/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._lsp.features.syntax_highlighting;

import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassification;
import de.monticore.cd4analysis._lsp.language_access.CD4AnalysisLanguageAccess;

import java.util.Optional;

public class CD4AnalysisLexerProvider extends CD4AnalysisLexerProviderTOP {

  public CD4AnalysisLexerProvider(CD4AnalysisLanguageAccess languageAccess) {
    super(languageAccess);
  }

  @Override
  protected Optional<TokenClassification> classifyToken(Token token) {
    // Overrides the classification to not use semantic highlighting and only syntax highlighting
    return Optional.empty();
  }
}
