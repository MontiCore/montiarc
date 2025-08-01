/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Optional;

public class DeSerializer {

  public String serialize(Object o) {
    ObjectMapper om = new ObjectMapper();
    try {
      return om.writeValueAsString(o);
    } catch (IOException ignored) { }

    return "";
  }

  public <R> Optional<R> deserialize(String s, TypeReference<R> typeRef) {
    ObjectMapper om = new ObjectMapper();
    try {
      return Optional.of(om.readValue(s, typeRef));
    } catch (IOException e) {
      Log.error(String.format("Cannot deserialize object of class %s. Reason: %s", typeRef.getType().getTypeName(), e));
    }
    return Optional.empty();
  }

  public <R> Optional<R> deserialize(String s, Class<R> clazz) {
    ObjectMapper om = new ObjectMapper();

    try {
      return Optional.of(om.readValue(s, clazz));
    } catch (IOException e) {
      Log.error(String.format("Cannot deserialize object of class %s. Reason: %s", clazz.getName(), e));
    }
    return Optional.empty();
  }
}
