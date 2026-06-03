/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy.util;

import de.se_rwth.commons.logging.Log;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

public class DeSerializer {

  protected final ObjectMapper mapper;

  public DeSerializer() {
    mapper = JsonMapper.builder().build();
  }

  public String serialize(Object o) {
    try {
      return mapper.writeValueAsString(o);
    } catch (JacksonException ignored) { }

    return "";
  }

  public <R> Optional<R> deserialize(String s, TypeReference<R> typeRef) {
    try {
      return Optional.of(mapper.readValue(s, typeRef));
    } catch (JacksonException e) {
      Log.error(String.format("Cannot deserialize object of class %s. Reason: %s", typeRef.getType().getTypeName(), e));
    }
    return Optional.empty();
  }

  public <R> Optional<R> deserialize(String s, Class<R> clazz) {
    try {
      return Optional.of(mapper.readValue(s, clazz));
    } catch (JacksonException e) {
      Log.error(String.format("Cannot deserialize object of class %s. Reason: %s", clazz.getName(), e));
    }
    return Optional.empty();
  }
}
