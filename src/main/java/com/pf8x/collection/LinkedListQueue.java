package com.pf8x.collection;

import com.pf8x.exception.Pf8xIllegalStateException;

import java.util.Deque;
import java.util.LinkedList;

public class LinkedListQueue<T> implements SimpleQueue<T> {
    private final Deque<T> queue;
    private final int maxSize;

    public LinkedListQueue(int maxSize) {
        this.maxSize = maxSize;
        queue = new LinkedList<>();
    }

    @Override
    public synchronized void enqueue(T e) {
        if(maxSize != -1 && queue.size() >= maxSize) throw new Pf8xIllegalStateException("LinkedListQueue reaches maximum!");
        queue.add(e);
    }

    @Override
    public synchronized T dequeue() {
        return queue.poll();
    }

    @Override
    public synchronized int size() {
        return queue.size();
    }

    public synchronized T removeOrDequeue(T e) {
        if(e == null || !queue.remove(e)) return queue.poll();
        return e;
    }
}
