package com.pf8x.collection;

public interface SimpleQueue<T> {
    void enqueue(T e);
    T dequeue();
    int size();
}
