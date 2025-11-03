/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

import de.monticore.rte.tuples.Tuple2;
import de.monticore.rte.streams.internal.ConcatenatedStream;
import de.monticore.rte.streams.internal.FlattenStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Timed Event Stream
 */
public class EventStream<T> implements Stream<UntimedStream<T>>, TimeableStream<T> {

  protected final SyncStream<UntimedStream<T>> backing;

  EventStream(SyncStream<UntimedStream<T>> backing) {
    this.backing = backing;
  }

  public static <T> EventStream<T> of(List<UntimedStream<T>> List) {
    return new EventStream<>(SyncStream.of(List));
  }

  @SafeVarargs
  public static <T> EventStream<T> of(UntimedStream<T>... ele) {
    return new EventStream<>(SyncStream.of(List.of(ele)));
  }

  //
  // Interface Implementations
  //

  @Override
  public UntimedStream<T> first() throws IndexOutOfBoundsException {
    return this.backing.first();
  }

  @Override
  public EventStream<T> dropFirst() {
    return new EventStream<>(backing.dropFirst());
  }

  @Override
  public EventStream<T> dropMultiple(long n) {
    return new EventStream<>(backing.dropMultiple(n));
  }

  @Override
  public EventStream<T> dropWhile(Function<UntimedStream<T>, Boolean> predicate) {
    return new EventStream<>(backing.dropWhile(predicate));
  }

  @Override
  public EventStream<T> take(long n) {
    return new EventStream<>(backing.take(n));
  }

  @Override
  public EventStream<T> takeWhile(Function<UntimedStream<T>, Boolean> predicate) {
    return new EventStream<>(backing.takeWhile(predicate));
  }

  @Override
  public UntimedStream<T> nth(long n) throws IndexOutOfBoundsException {
    return this.backing.nth(n);
  }

  @Override
  public EventStream<T> times(long n) {
    return new EventStream<>(backing.times(n));
  }

  @Override
  public EventStream<T> infTimes() {
    return new EventStream<>(backing.infTimes());
  }

  @Override
  public long len() {
    return this.backing.len();
  }

  @Override
  public boolean hasInfiniteLen() {
    return this.backing.hasInfiniteLen();
  }

  @Override
  public boolean isEmpty() {
    return this.backing.isEmpty();
  }

  /**
   * Also see EventStream.eMap.
   * We cant give stronger guarantees than SyncStream here, as U might not be an UntimedStream<>,
   * but must be generic to comply with the Stream interface.
   */
  @Override
  public <U> SyncStream<U> map(Function<UntimedStream<T>, U> f) {
    return this.backing.map(f);
  }

  @Override
  public EventStream<T> filter(Function<UntimedStream<T>, Boolean> predicate) {
    return new EventStream<>(backing.filter(predicate));
  }

  @Override
  public EventStream<T> rmDups() {
    return new EventStream<>(backing.rmDups());
  }

  /**
   * Also see EventStream.eScanl.
   * We cant give stronger guarantees than SyncStream here, as U might not be an UntimedStream<>,
   * but must be generic to comply with the Stream interface.
   */
  @Override
  public <U> SyncStream<U> scanl(BiFunction<U, UntimedStream<T>, U> fn, U acc) {
    return this.backing.scanl(fn, acc);
  }

  /**
   * Also see EventStream.eForEach
   */
  @Override
  public void forEach(Consumer<UntimedStream<T>> action) {
    this.backing.forEach(action);
  }

  @Override
  public Set<UntimedStream<T>> values() {
    return this.backing.values();
  }

  @Override
  public EventStream<T> withPrepended(UntimedStream<T> element) {
    return new EventStream<>(this.backing.withPrepended(element));
  }

  @Override
  public UntimedStream<T> untimed() {
    return new FlattenStream<>(this.backing);
  }

  @Override
  public SyncStream<T> sync() {
    return untimed().sync();
  }

  @Override
  public ToptStream<T> topt() {
    return untimed().topt();
  }

  @Override
  public EventStream<T> event() {
    return this;
  }

  // Event Specific

  public <U> EventStream<U> eMap(Function<T, U> f) {
    return new EventStream<>(this.map(s -> s.map(f)));
  }

  // TODO: zip()
  // The return type would be an UntimedStream<Tuple2<UntimedStream<T>, UntimedStream<U>>>, which can not be nicely represented as an EventStream
  // At that point, we are better of letting the user convert to UntimedStreams manually.

  public EventStream<T> concat(EventStream<T> other) {
    return new EventStream<>(this.backing.concat(other.backing));
  }

  public <U> EventStream<U> eScanl(BiFunction<UntimedStream<U>, UntimedStream<T>, UntimedStream<U>> fn,
                                   UntimedStream<U> acc) {
    return new EventStream<>(this.scanl(fn, acc));
  }

  /**
   * Prepend an element into the first time-slice and return the resulting stream.
   */
  public EventStream<T> eWithPrepended(T element) {
    if (this.isEmpty())
      return EventStream.of(UntimedStream.of(element));

    UntimedStream<T> newHead = this.first().withPrepended(element);
    return this.withPrepended(newHead);
  }

  /**
   * {@link EventStream#forEach(Consumer)} but on each element, rather than each slice.
   */
  public void eForEach(Consumer<T> action) {
    this.backing.forEach(s -> s.forEach(action));
  }

  public EventStream<T> delay(long n) {
    return new EventStream<T>(this.backing.delay(n, UntimedStream.empty()));
  }

  public EventStream<T> rougherTime(long slices) {
    if (slices <= 0)
      throw new IllegalArgumentException("slices must be a positive long");

        /*
        The implementation looks more complex than it is.
        We simply create "buffers" (lists) of the slices until they are full (i.e. have slices-many items).
        Then we start the next buffer.
        We later filter out buffers which are not full.
        The edge-case of the end of the stream is handled by ziping-in a boolean,
        which can predict, if there will be a next slice.
        If not, even the un-full buffer will be "released".
         */

    // fill the previous list, or start a new one, if full
    BiFunction<List<UntimedStream<T>>, UntimedStream<T>, List<UntimedStream<T>>> grouper
      = (l, s) -> {
      if (l.size() < slices) {
        l.add(0, s);
        return l;
      }
      return List.of(s);
    };

    // say slices = 3, then
    // grouped = <[], [<>], [<>, <>], [<>, <>, <>], [<>], [<>, <>], ...>
    SyncStream<List<UntimedStream<T>>> grouped = this.backing.scanl(grouper, List.of());

    // false for every time slice, append true at the end
    SyncStream<Boolean> isEndingNextSliceStream = new SyncStream<>(
      new ConcatenatedStream<>(
        grouped.map(s -> Boolean.FALSE).untimed(),
        UntimedStream.of(true)
      )
    );

    grouped = grouped.delay(1, List.of()); // as the other stream is one slice "ahead"
    SyncStream<Tuple2<List<UntimedStream<T>>, Boolean>> zipped = grouped.zip(isEndingNextSliceStream);

    // filter out lists, which are not full
    SyncStream<List<UntimedStream<T>>> filtered = zipped
      .filter(t -> t.get0().size() == slices || t.get1())
      .map(Tuple2::get0);

    // map each to the concatenated stream
    SyncStream<UntimedStream<T>> mapped = filtered
      .map(l -> {
        List<UntimedStream<T>> res = new ArrayList<>(l);
        Collections.reverse(res);
        return ConcatenatedStream.many(res);
      });

    return new EventStream<>(mapped);
  }

  // TODO public Set<EventStream<T>> finerTime(long slices)

  @Override
  public int hashCode() {
    return this.backing.hashCode() ^ 1;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof EventStream))
      return false;

    return this.backing.equals(((EventStream<?>) obj).backing);
  }
}
