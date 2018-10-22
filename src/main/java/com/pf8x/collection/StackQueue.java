package com.pf8x.collection;

import com.pf8x.exception.Pf8xIllegalStateException;

import java.util.Stack;

public class StackQueue<T> implements SimpleQueue<T> {
    private int capacity;
    private final Stack<T> stack;

    public StackQueue(int capacity) {
        this.capacity = capacity;
        stack = new Stack<>();
    }

    @Override
    public synchronized void enqueue(T e) {
        if(stack.size() >= capacity) throw new Pf8xIllegalStateException("StackQueue reaches maximum!");
        stack.push(e);
    }

    @Override
    public synchronized T dequeue() {
        if(stack.size() == 0) return null;
        return stack.pop();
    }

    @Override
    public synchronized int size() {
        return stack.size();
    }
}
