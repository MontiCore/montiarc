/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.generator.MA2JSimTool;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

public class Ma2JsimToolCLI extends MA2JSimTool {

  public static void main(String[] args) {
    Preconditions.checkNotNull(args);
    Ma2JsimToolCLI tool = new Ma2JsimToolCLI();
    tool.init();
    tool.run(args);
  }

  @Override
  protected void initGlobalScope(CommandLine cl) {
    super.initGlobalScope(cl);
    copyResourceToTmp("montiarc-base-arcSymbols.zip").ifPresent(MontiArcMill.globalScope().getSymbolPath()::addEntry);
    copyResourceToTmp("montiarc-base-cd2pojoSymbols.zip").ifPresent(MontiArcMill.globalScope().getSymbolPath()::addEntry);
    copyResourceToTmp("simulator-rte-cd2pojoSymbols.zip").ifPresent(MontiArcMill.globalScope().getSymbolPath()::addEntry);
  }

  protected Optional<Path> copyResourceToTmp(String ressourceName) {
    URL url = this.getClass().getResource(ressourceName);
    if (url == null) {
      Log.error("Ressource not found " + ressourceName);
      return Optional.empty();
    }
    if (url.toString().startsWith("jar:")) {
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
        //file.deleteOnExit();
        return Optional.of(Paths.get(file.toURI()));
      } catch (IOException e) {
        Log.error(e.toString());
        return Optional.empty();
      }
    } else {
      //this will work in the IDE, but not from a JAR
      try {
        return Optional.of(Paths.get(url.toURI()));
      } catch (URISyntaxException e) {
        Log.error(e.toString());
        return Optional.empty();
      }
    }
  }
}
