/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams.internal;

import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FiniteUntimedStream<T> extends UntimedStream<T> {

  protected final List<T> list;

  static protected final FiniteUntimedStream<?> EMPTY =
    FiniteUntimedStream.of(List.of());

  public static <T> FiniteUntimedStream<T> empty() {
    @SuppressWarnings("unchecked")
    FiniteUntimedStream<T> s = (FiniteUntimedStream<T>) EMPTY;
    return s;
  }

  protected FiniteUntimedStream(List<T> list) {
    this.list = list;
  }

  public static <T> FiniteUntimedStream<T> of(List<T> List) {
    return new FiniteUntimedStream<>(List);
  }

  @SafeVarargs
  public static <T> FiniteUntimedStream<T> of(T... ele) {
    return new FiniteUntimedStream<>(List.of(ele));
  }

  public long len() {
    return this.list.size();
  }

  @Override
  public UntimedStream<T> dropFirst() {
    List<T> list = new ArrayList<>(this.list);
    list.remove(0);
    return FiniteUntimedStream.of(list);
  }

  @Override
  public Tuple2<Optional<T>, UntimedStream<T>> _internal_next() {
    if (list.isEmpty())
      return Tuple2.of(Optional.empty(), UntimedStream.empty());
    return Tuple2.of(Optional.of(list.get(0)), this.dropFirst());
  }

  @Override
  public T first() throws IndexOutOfBoundsException {
    if (list.isEmpty())
      throw new IndexOutOfBoundsException();
    return this.list.get(0);
  }

  @Override
  public UntimedStream<T> dropMultiple(long n) {
    // we could certainly handle long -> int casting better
    List<T> list = new ArrayList<>(this.list);
    while (n > 0) {
      list.remove(0);
      n--;
    }
    return FiniteUntimedStream.of(list);
  }

}
