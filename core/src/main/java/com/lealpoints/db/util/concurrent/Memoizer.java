package com.lealpoints.db.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer<A, V> implements Computable<A, V> {

    private final ConcurrentMap<A, Future<V>> _cache = new ConcurrentHashMap<>();
    private final Computable<A, V> _computable;

    public Memoizer(Computable<A, V> computable) {
        _computable = computable;
    }

    private static RuntimeException launderThrowable(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        } else if (throwable instanceof Error) {
            throw (Error) throwable;
        } else {
            throw new IllegalArgumentException("Not unchecked", throwable);
        }
    }

    @Override
    public V compute(final A arg) throws InterruptedException {
        while (true) {
            Future<V> value = _cache.get(arg);
            if (value == null) {
                FutureTask<V> futureTask = createFutureTask(arg);
                value = _cache.putIfAbsent(arg, futureTask);
                if (value == null) {
                    value = futureTask;
                    futureTask.run();
                }
            }
            try {
                final V v = value.get();
                return v;
            } catch (CancellationException e) {
                _cache.remove(arg, value);
            } catch (ExecutionException e) {
                throw launderThrowable(e);
            }
        }
    }

    private FutureTask<V> createFutureTask(final A arg) {
        Callable<V> eval = new Callable<V>() {
            @Override
            public V call() throws Exception {
                return _computable.compute(arg);
            }
        };
        return new FutureTask<>(eval);
    }
}
