package de.monticore.lang.montiarc.unit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Units {
  private Map<String, Power> powerMap;
  private Map<String, Time> timeMap;
  private Set<String> unitKinds;

  // List of maps to avoid casting (and this is reflection) and so
  // not wished by BR
  private List<Map<String, ? extends Unit>> units;

  protected static Units instance = new Units();

  protected Units() {
    unitKinds = createUnitKindSet();
    units = new ArrayList<>();

    powerMap = createPowerMap();
    units.add(powerMap);

    timeMap = createTimeMap();
    units.add(timeMap);
  }

  protected Set<String> createUnitKindSet() {
    Set<String> kindsSet = new LinkedHashSet<>();

    kindsSet.add("Power");
    kindsSet.add("Time");

    return kindsSet;
  }

  protected Map<String,Time> createTimeMap() {
    Map<String, Time> timeMap = new LinkedHashMap<>();

    Second s = new Second();
    timeMap.put("s", s);
    timeMap.put("second", s);
    timeMap.put("seconds", s);

    Minute min = new Minute();
    timeMap.put("min", min);
    timeMap.put("minute", min);
    timeMap.put("minutes", min);

    Day d = new Day();
    timeMap.put("d", d);
    timeMap.put("day", d);
    timeMap.put("days", d);

    Year a = new Year();
    timeMap.put("a", a);
    timeMap.put("year", a);
    timeMap.put("years", a);

    MilliSecond ms = new MilliSecond();
    timeMap.put("ms", ms);
    timeMap.put("millisecond", ms);
    timeMap.put("milliseconds", ms);

    MicroSecond us = new MicroSecond();
    timeMap.put("us", us);
    timeMap.put("microsecond", us);
    timeMap.put("microseconds", us);

    return timeMap;
  }

  protected Map<String, Power> createPowerMap() {
    Map<String, Power> powerMap = new LinkedHashMap<>();

    Watt W = new Watt();
    powerMap.put("W", W);
    powerMap.put("watt", W);
    powerMap.put("watts", W);

    MegaWatt MW = new MegaWatt();
    powerMap.put("MW", MW);
    powerMap.put("megawatt", MW);
    powerMap.put("megawatts", MW);

    KiloWatt kW = new KiloWatt();
    powerMap.put("kW", kW);
    powerMap.put("kilowatt", kW);
    powerMap.put("kilowatts", kW);

    MilliWatt mW = new MilliWatt();
    powerMap.put("mW", mW);
    powerMap.put("milliwatt", mW);
    powerMap.put("millwatts", mW);

    return powerMap;
  }

  public static Optional<Unit> getUnit(final String unit) {
    for(Map<String, ? extends Unit> map : instance.units) {
      if(map.containsKey(unit)) {
        return Optional.of(map.get(unit));
      }
    }
    return Optional.empty();
  }

  public static Optional<Power> getPower(final String power) {
    if(instance.powerMap.containsKey(power)) {
      return Optional.of(instance.powerMap.get(power));
    } else {
      return Optional.empty();
    }
  }

  public static String getAvailablePowerUnits() {
    return instance.powerMap.keySet().stream().collect(Collectors.joining(", "));
  }

  public static Optional<Time> getTime(final String time) {
    if(instance.timeMap.containsKey(time)) {
      return Optional.of(instance.timeMap.get(time));
    } else {
      return Optional.empty();
    }
  }

  public static String getAvailableTimeUnits() {
    return instance.timeMap.keySet().stream().collect(Collectors.joining(", "));
  }

  public static boolean isUnitKindAvailable(final String kind) {
    return instance.unitKinds.contains(kind);
  }

  public static String getAvailableUnitKinds() {
    return instance.unitKinds.stream().collect(Collectors.joining(", "));
  }

}