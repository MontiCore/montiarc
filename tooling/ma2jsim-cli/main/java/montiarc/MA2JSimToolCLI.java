/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.generator.MA2JSimTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

public class MA2JSimToolCLI extends MA2JSimTool {

  public static void main(String[] args) {
    Preconditions.checkNotNull(args);
    MA2JSimToolCLI tool = new MA2JSimToolCLI();
    tool.init();
    tool.run(args);
  }

  @Override
  public void initGlobalScope(String... entries) {
    super.initGlobalScope(entries);

    // Add montiarc-base
    copyZipResourceToJarTmp("montiarc-base-arcSymbols.zip").ifPresent(MontiArcMill.globalScope().getSymbolPath()::addEntry);
    copyZipResourceToJarTmp("montiarc-base-cd2pojoSymbols.zip").ifPresent(MontiArcMill.globalScope().getSymbolPath()::addEntry);
    // Add maunit
    copyZipResourceToJarTmp("maunit-arcSymbols.zip").ifPresent(MontiArcMill.globalScope().getSymbolPath()::addEntry);
    copyZipResourceToJarTmp("maunit-cd2pojoSymbols.zip").ifPresent(MontiArcMill.globalScope().getSymbolPath()::addEntry);
  }

  protected Optional<Path> copyZipResourceToJarTmp(String ressourceName) {
    URL url = this.getClass().getResource(ressourceName);
    if (url == null) {
      Log.warn("Ressource not found " + ressourceName);
      return Optional.empty();
    }
    try {
      InputStream input = getClass().getResourceAsStream(ressourceName);
      File file = File.createTempFile(new Date().getTime() + "", ".jar");
      OutputStream out = new FileOutputStream(file);
      int read;
      byte[] bytes = new byte[1024];

      while ((read = input.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
      out.flush();
      out.close();
      input.close();
      file.deleteOnExit();
      return Optional.of(Paths.get(file.toURI()));
    } catch (IOException e) {
      Log.error(e.toString());
      return Optional.empty();
    }
  }
}
