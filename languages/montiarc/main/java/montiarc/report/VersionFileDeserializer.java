/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Deserializes a version recorded in a file.
 * <br>
 * The file must contain the version string in the first line. This line will be returned.
 * If a problem, such as a {@link IOException} occurs during the method execution, an empty
 * String is returned.
 */
public final class VersionFileDeserializer {

  private final String versionPath;

  public VersionFileDeserializer(@NotNull String path) {
    this.versionPath = Preconditions.checkNotNull(path);
  }

  public String loadVersion() {
    return deserializeFrom(versionPath).orElse("");
  }

  private static Optional<String> deserializeFrom(@NotNull String path) {
    URL fileLoc = VersionFileDeserializer.class.getClassLoader().getResource(path);
    if (fileLoc == null) {
      return Optional.empty();
    }

    try (
      InputStream fileStream = fileLoc.openStream();
      Reader streamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
      BufferedReader fileReader = new BufferedReader(streamReader);
    ) {
      return Optional.of(fileReader.readLine());
    } catch (IOException e) {
      return Optional.empty();
    }
  }
}
