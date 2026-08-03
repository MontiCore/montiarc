/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import montiarc.MontiArcMill;
import montiarc.MontiArcTool;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.jar.JarFile;

public class SymbolPathLoader {
  public static void addResource(String resource) throws IOException {
    URL streamURL = MontiArcTool.class.getClassLoader().getResource(resource);
    if (streamURL == null) throw new IOException("Resource not found: " + resource);
    try {
      JarURLConnection urlConnection = (JarURLConnection) streamURL.openConnection();
      JarFile jar = urlConnection.getJarFile();
      Path jarPath = Path.of(jar.getName());
      MontiArcMill.globalScope().getSymbolPath().addEntry(jarPath);
    } catch (IOException ignored) {}
  }
}
