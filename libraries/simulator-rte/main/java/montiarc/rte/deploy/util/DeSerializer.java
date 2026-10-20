/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.monticore.rte.tuples.Tuple2;
import de.monticore.rte.tuples.Tuple3;
import de.monticore.rte.tuples.Tuple4;
import de.monticore.rte.tuples.Tuple5;
import de.monticore.rte.tuples.Tuple6;
import de.monticore.rte.tuples.Tuple7;
import de.monticore.rte.tuples.Tuple8;
import de.monticore.rte.tuples.Tuple9;
import de.monticore.rte.tuples.Tuple10;
import de.monticore.rte.tuples.Tuple11;
import de.monticore.rte.tuples.Tuple12;
import de.se_rwth.commons.logging.Log;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

public class DeSerializer {

  protected final ObjectMapper mapper;

  public DeSerializer() {
    mapper = JsonMapper.builder()
      .addMixIn(Tuple2.class, Tuple2Mixin.class)
      .addMixIn(Tuple3.class, Tuple3Mixin.class)
      .addMixIn(Tuple4.class, Tuple4Mixin.class)
      .addMixIn(Tuple5.class, Tuple5Mixin.class)
      .addMixIn(Tuple6.class, Tuple6Mixin.class)
      .addMixIn(Tuple7.class, Tuple7Mixin.class)
      .addMixIn(Tuple8.class, Tuple8Mixin.class)
      .addMixIn(Tuple9.class, Tuple9Mixin.class)
      .addMixIn(Tuple10.class, Tuple10Mixin.class)
      .addMixIn(Tuple11.class, Tuple11Mixin.class)
      .addMixIn(Tuple12.class, Tuple12Mixin.class)
      .build();
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

  abstract static class Tuple2Mixin {

    @JsonCreator
    Tuple2Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1) { }

    @JsonProperty("0")
    abstract Object get0();

    @JsonProperty("1")
    abstract Object get1();
  }

  abstract static class Tuple3Mixin {
    @JsonCreator Tuple3Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
  }

  abstract static class Tuple4Mixin {
    @JsonCreator Tuple4Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
  }

  abstract static class Tuple5Mixin {
    @JsonCreator Tuple5Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3, @JsonProperty("4") Object e4) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
    @JsonProperty("4") abstract Object get4();
  }

  abstract static class Tuple6Mixin {
    @JsonCreator Tuple6Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3, @JsonProperty("4") Object e4, @JsonProperty("5") Object e5) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
    @JsonProperty("4") abstract Object get4();
    @JsonProperty("5") abstract Object get5();
  }

  abstract static class Tuple7Mixin {
    @JsonCreator Tuple7Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3, @JsonProperty("4") Object e4, @JsonProperty("5") Object e5, @JsonProperty("6") Object e6) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
    @JsonProperty("4") abstract Object get4();
    @JsonProperty("5") abstract Object get5();
    @JsonProperty("6") abstract Object get6();
  }

  abstract static class Tuple8Mixin {
    @JsonCreator Tuple8Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3, @JsonProperty("4") Object e4, @JsonProperty("5") Object e5, @JsonProperty("6") Object e6, @JsonProperty("7") Object e7) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
    @JsonProperty("4") abstract Object get4();
    @JsonProperty("5") abstract Object get5();
    @JsonProperty("6") abstract Object get6();
    @JsonProperty("7") abstract Object get7();
  }

  abstract static class Tuple9Mixin {
    @JsonCreator Tuple9Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3, @JsonProperty("4") Object e4, @JsonProperty("5") Object e5, @JsonProperty("6") Object e6, @JsonProperty("7") Object e7, @JsonProperty("8") Object e8) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
    @JsonProperty("4") abstract Object get4();
    @JsonProperty("5") abstract Object get5();
    @JsonProperty("6") abstract Object get6();
    @JsonProperty("7") abstract Object get7();
    @JsonProperty("8") abstract Object get8();
  }

  abstract static class Tuple10Mixin {
    @JsonCreator Tuple10Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3, @JsonProperty("4") Object e4, @JsonProperty("5") Object e5, @JsonProperty("6") Object e6, @JsonProperty("7") Object e7, @JsonProperty("8") Object e8, @JsonProperty("9") Object e9) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
    @JsonProperty("4") abstract Object get4();
    @JsonProperty("5") abstract Object get5();
    @JsonProperty("6") abstract Object get6();
    @JsonProperty("7") abstract Object get7();
    @JsonProperty("8") abstract Object get8();
    @JsonProperty("9") abstract Object get9();
  }

  abstract static class Tuple11Mixin {
    @JsonCreator Tuple11Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3, @JsonProperty("4") Object e4, @JsonProperty("5") Object e5, @JsonProperty("6") Object e6, @JsonProperty("7") Object e7, @JsonProperty("8") Object e8, @JsonProperty("9") Object e9, @JsonProperty("10") Object e10) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
    @JsonProperty("4") abstract Object get4();
    @JsonProperty("5") abstract Object get5();
    @JsonProperty("6") abstract Object get6();
    @JsonProperty("7") abstract Object get7();
    @JsonProperty("8") abstract Object get8();
    @JsonProperty("9") abstract Object get9();
    @JsonProperty("10") abstract Object get10();
  }

  abstract static class Tuple12Mixin {
    @JsonCreator Tuple12Mixin(@JsonProperty("0") Object e0, @JsonProperty("1") Object e1, @JsonProperty("2") Object e2, @JsonProperty("3") Object e3, @JsonProperty("4") Object e4, @JsonProperty("5") Object e5, @JsonProperty("6") Object e6, @JsonProperty("7") Object e7, @JsonProperty("8") Object e8, @JsonProperty("9") Object e9, @JsonProperty("10") Object e10, @JsonProperty("11") Object e11) { }
    @JsonProperty("0") abstract Object get0();
    @JsonProperty("1") abstract Object get1();
    @JsonProperty("2") abstract Object get2();
    @JsonProperty("3") abstract Object get3();
    @JsonProperty("4") abstract Object get4();
    @JsonProperty("5") abstract Object get5();
    @JsonProperty("6") abstract Object get6();
    @JsonProperty("7") abstract Object get7();
    @JsonProperty("8") abstract Object get8();
    @JsonProperty("9") abstract Object get9();
    @JsonProperty("10") abstract Object get10();
    @JsonProperty("11") abstract Object get11();
  }
}
