package com.pf8x.collection;

import com.pf8x.exception.Pf8xIllegalStateException;

import java.util.LinkedList;
import java.util.Queue;

public class SwapQueue<T> implements SimpleQueue<T> {
    private Queue<T> outQueue;
    private Queue<T> inQueue;
    private final Object outPermit = new Object();
    private final Object inPermit = new Object();

    private final int softLimit;

    public SwapQueue(int softLimit) {
        this.softLimit = softLimit;
        outQueue = new LinkedList<>();
        inQueue = new LinkedList<>();
    }

    @Override
    public void enqueue(T e) {
        synchronized (inPermit) {
            if(softLimit != -1 && inQueue.size() >= softLimit) throw new Pf8xIllegalStateException("SwapQueue hits soft limit: " + inQueue.size());

            inQueue.add(e);
        }
    }

    @Override
    public T dequeue() {
        synchronized (outPermit) {
            T e = outQueue.poll();
            if (e != null) {

                return e;
            }

            synchronized (inPermit) {
                if(inQueue.size() > 0) {
                    Queue<T> tmp = outQueue;
                    outQueue = inQueue;
                    inQueue = tmp;

                    return outQueue.poll();
                }
            }

            return null;
        }
    }

    @Override
    public int size() {
        synchronized (outPermit) {
            synchronized (inPermit) {
                return inQueue.size() + outQueue.size();
            }
        }
    }
}
