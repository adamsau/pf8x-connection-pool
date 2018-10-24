package com.pf8x.collection;

import com.pf8x.exception.Pf8xIllegalStateException;

import java.util.ArrayDeque;
import java.util.Queue;

public class ArrayQueue<T> implements SimpleQueue<T> {
    private final Queue<T> queue;
    private final int maxSize;

    public ArrayQueue(int maxSize) {
        this.maxSize = maxSize;
        queue = new ArrayDeque<>(maxSize);
    }

    @Override
    public synchronized void enqueue(T e) {
        if(queue.size() >= maxSize) throw new Pf8xIllegalStateException("ArrayQueue reaches maximum!");
        queue.add(e);
    }

    @Override
    public synchronized T dequeue() {
        return queue.poll();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
