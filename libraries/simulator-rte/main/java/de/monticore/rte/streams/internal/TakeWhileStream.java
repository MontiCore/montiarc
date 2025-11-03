/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams.internal;

import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;
import java.util.function.Function;

public class TakeWhileStream<T> extends UntimedStream<T> {
  protected final UntimedStream<T> stream;
  protected final Function<T, Boolean> predicate;

  public TakeWhileStream(UntimedStream<T> stream, Function<T, Boolean> predicate) {
    this.stream = stream;
    this.predicate = predicate;
  }

  @Override
  public Tuple2<Optional<T>, UntimedStream<T>> _internal_next() {
    Tuple2<Optional<T>, UntimedStream<T>> headTail = stream._internal_next();
    Optional<T> optHead = headTail.get0();
    UntimedStream<T> tail = headTail.get1();

    if (optHead.isEmpty() || !predicate.apply(optHead.get())) {
      // stream is drained or predicate became false
      return Tuple2.of(Optional.empty(), UntimedStream.empty());
    }

    tail = new TakeWhileStream<>(tail, predicate);
    return Tuple2.of(optHead, tail);
  }
}
