package com.lealpoints.db.util.concurrent;

public interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
