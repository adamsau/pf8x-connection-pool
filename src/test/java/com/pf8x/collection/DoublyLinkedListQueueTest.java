package com.pf8x.collection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DoublyLinkedListQueueTest {
    private DoublyLinkedListQueue<Object> doublyLinkedListQueue = new DoublyLinkedListQueue<>(5);
    @Test
    public void shouldEnqueueAndDequeue() {
        assertEquals(null, doublyLinkedListQueue.dequeue());

        Object o1 = new Object();
        doublyLinkedListQueue.enqueue(o1);
        assertEquals(o1, doublyLinkedListQueue.dequeue());
    }

    @Test
    public void shouldEnqueueAndDequeueMultipleTimesCorrectly() {
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        Object o4 = new Object();
        Object o5 = new Object();

        doublyLinkedListQueue.enqueue(o1);
        doublyLinkedListQueue.enqueue(o2);
        assertEquals(o1, doublyLinkedListQueue.dequeue());
        doublyLinkedListQueue.enqueue(o3);
        doublyLinkedListQueue.enqueue(o4);
        doublyLinkedListQueue.enqueue(o5);
        assertEquals(o2, doublyLinkedListQueue.dequeue());
        assertEquals(o3, doublyLinkedListQueue.dequeue());
        doublyLinkedListQueue.enqueue(o1);
        doublyLinkedListQueue.enqueue(o2);
        doublyLinkedListQueue.enqueue(o3);

        boolean ok = false;
        try {
            doublyLinkedListQueue.enqueue(o3);
        }
        catch (Exception e) {
            ok = true;
        }

        assertEquals(true, ok);

        assertEquals(o4, doublyLinkedListQueue.dequeue());
        assertEquals(o5, doublyLinkedListQueue.dequeue());
        assertEquals(o1, doublyLinkedListQueue.dequeue());
        assertEquals(o2, doublyLinkedListQueue.dequeue());
        assertEquals(o3, doublyLinkedListQueue.dequeue());
        assertEquals(null, doublyLinkedListQueue.dequeue());
    }
}
