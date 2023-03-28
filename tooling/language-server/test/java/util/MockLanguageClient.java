/* (c) https://github.com/MontiCore/monticore */
package util;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.concurrent.CompletableFuture;

public class MockLanguageClient implements LanguageClient {

  @Override
  public void telemetryEvent(Object object) {

  }

  @Override
  public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {

  }

  @Override
  public void showMessage(MessageParams messageParams) {

  }

  @Override
  public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
    return null;
  }

  @Override
  public void logMessage(MessageParams message) {

  }
}
