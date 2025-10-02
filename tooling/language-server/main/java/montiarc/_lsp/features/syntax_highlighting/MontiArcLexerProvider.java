/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.features.syntax_highlighting;

import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassification;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightComponentNameRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightFeatureNameRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightFieldNameRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightFieldTypeRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightImportNameRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightPackageNameRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightPortNameRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightPortTypeRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightStatechartStateNameRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightSubcomponentNameRule;
import montiarc._lsp.features.syntax_highlighting.rule.HighlightSubcomponentTypeRule;
import montiarc._lsp.features.syntax_highlighting.rule.MAHighlightLocalKeywordRule;
import montiarc._lsp.language_access.MontiArcLanguageAccess;

import java.util.ArrayList;
import java.util.Optional;

public class MontiArcLexerProvider extends MontiArcLexerProviderTOP {

  public MontiArcLexerProvider(MontiArcLanguageAccess languageAccess) {
    super(languageAccess);
    this.classificationRules = new ArrayList<>();

    addClassificationRule(new HighlightPackageNameRule());
    addClassificationRule(new HighlightImportNameRule());
    addClassificationRule(new HighlightComponentNameRule());
    addClassificationRule(new HighlightStatechartStateNameRule());
    addClassificationRule(new HighlightPortNameRule());
    addClassificationRule(new HighlightPortTypeRule());
    addClassificationRule(new HighlightSubcomponentNameRule());
    addClassificationRule(new HighlightSubcomponentTypeRule());
    addClassificationRule(new HighlightFieldNameRule());
    addClassificationRule(new HighlightFieldTypeRule());
    addClassificationRule(new HighlightFeatureNameRule());
    addClassificationRule(new MAHighlightLocalKeywordRule());
  }

  @Override
  protected Optional<TokenClassification> defaultMapToTokenType(Token token) {
    return Optional.empty();
  }
}
