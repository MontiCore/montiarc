/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
    ) {
      java.util.Properties properties = new java.util.Properties();
      properties.load(fileStream);

      return Optional.ofNullable(properties.getProperty("version"));
    } catch (IOException e) {
      return Optional.empty();
    }
  }
}
