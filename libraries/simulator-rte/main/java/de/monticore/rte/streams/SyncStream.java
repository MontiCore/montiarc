/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

import de.monticore.rte.streams.internal.ConcatenatedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A sync stream is a timed stream, each time slice has exactly one message.
 * If you can't guarantee that, use ToptStream.
 */
public class SyncStream<T> implements Stream<T>, TimeableStream<T> {

  protected final UntimedStream<T> backing;

  SyncStream(UntimedStream<T> backing) {
    this.backing = backing;
  }

  @SafeVarargs
  public static <S> SyncStream<S> of(S... elem) {
    return SyncStream.of(List.of(elem));
  }

  public static <T> SyncStream<T> of(List<T> List) {
    return new SyncStream<>(UntimedStream.of(List));
  }

  /**
   * Construct a stream, which repeats elem n times (n may be Stream.INFINITY).
   */
  public static <S> SyncStream<S> repeat(S elem, long n) {
    return new SyncStream<>(Stream.repeat(elem, n));
  }

  public static <S> SyncStream<S> syncIterate(Function<S, S> fn, S elem) {
    return new SyncStream<>(UntimedStream.iterate(fn, elem));
  }

  public static <S, U> SyncStream<S> syncProjFst(SyncStream<Tuple2<S, U>> s) {
    // Untimed-ness guaranteed by .zip()
    return new SyncStream<>((UntimedStream<S>) Stream.projFst(s.backing));
  }

  public static <S, U> SyncStream<U> syncProjSnd(SyncStream<Tuple2<S, U>> s) {
    // Untimed-ness guaranteed by .zip()
    return new SyncStream<>((UntimedStream<U>) Stream.projSnd(s.backing));
  }

  //
  // Interface Implementations
  //

  @Override
  public T first() throws IndexOutOfBoundsException {
    return this.backing.first();
  }

  @Override
  public SyncStream<T> dropFirst() {
    return new SyncStream<>(backing.dropFirst());
  }

  @Override
  public SyncStream<T> take(long n) {
    return new SyncStream<>(backing.take(n));
  }

  @Override
  public SyncStream<T> dropMultiple(long n) {
    return new SyncStream<>(backing.dropMultiple(n));
  }

  @Override
  public SyncStream<T> times(long n) {
    return new SyncStream<>(backing.times(n));
  }

  @Override
  public SyncStream<T> infTimes() {
    return new SyncStream<>(backing.infTimes());
  }

  @Override
  public <U> SyncStream<U> map(Function<T, U> f) {
    return new SyncStream<>(backing.map(f));
  }

  @Override
  public SyncStream<T> filter(Function<T, Boolean> predicate) {
    return new SyncStream<>(backing.filter(predicate));
  }

  @Override
  public SyncStream<T> takeWhile(Function<T, Boolean> predicate) {
    return new SyncStream<>(backing.takeWhile(predicate));
  }

  @Override
  public T nth(long n) throws IndexOutOfBoundsException {
    return backing.nth(n);
  }

  @Override
  public SyncStream<T> dropWhile(Function<T, Boolean> predicate) {
    return new SyncStream<>(backing.dropWhile(predicate));
  }

  @Override
  public SyncStream<T> rmDups() {
    return new SyncStream<>(backing.rmDups());
  }

  @Override
  public <U> SyncStream<U> scanl(BiFunction<U, T, U> fn, U acc) {
    return new SyncStream<>(backing.scanl(fn, acc));
  }

  @Override
  public void forEach(Consumer<T> action) {
    backing.forEach(action);
  }

  @Override
  public Set<T> values() {
    return backing.values();
  }

  @Override
  public long len() {
    return this.backing.len();
  }

  @Override
  public boolean hasInfiniteLen() {
    return this.len() == Stream.INFINITY;
  }

  @Override
  public boolean isEmpty() {
    return this.backing.isEmpty();
  }

  @Override
  public SyncStream<T> withPrepended(T element) {
    return SyncStream.of(element).concat(this);
  }

  @Override
  public UntimedStream<T> untimed() {
    return backing;
  }

  @Override
  public ToptStream<T> topt() {
    return new ToptStream<>(backing.map(Optional::of));
  }

  @Override
  public EventStream<T> event() {
    return new EventStream<>(this.map(UntimedStream::of));
  }

  @Override
  public SyncStream<T> sync() {
    return this;
  }

  // Sync specific methods

  public <U> SyncStream<Tuple2<T, U>> zip(SyncStream<U> second) {
    return new SyncStream<>(backing.zip(second.backing));
  }

  public SyncStream<T> concat(SyncStream<T> other) {
    return new SyncStream<>(new ConcatenatedStream<>(this.backing, other.backing));
  }

  public SyncStream<T> delay(long n, T elem) {
    return new SyncStream<>(new ConcatenatedStream<>(UntimedStream.repeat(elem, n), this.backing));
  }

  @Override
  public int hashCode() {
    return this.backing.hashCode() ^ 2;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SyncStream)) return false;

    return this.backing.equals(((SyncStream<?>) obj).backing);
  }

  UntimedStream<T> getBacking() {
    return this.backing;
  }
}
