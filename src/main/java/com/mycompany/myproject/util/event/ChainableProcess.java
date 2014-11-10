/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.myproject.util.event;

/**
 *
 * A "chain of responsibility" inspired type. Its purpose is to be able to define some logic by implementing
 * the abstract {@link ChainableProcess#process(java.lang.Object)
 * } for multiple {@link ChainableProcess} that are linked together.
 *
 * The simplest usage is to build a chain, remember the first chain-link and call {@link ChainableProcess#processLinks(java.lang.Object)
 * } on the the remembered chain-link to process all links as a series.
 *
 * Another usecase is to implement {@link ChainableProcess#process(java.lang.Object)
 * } and {@link ChainableProcess#process(java.lang.Object)
 * }call {@link ChainableProcess#getNext()}.{@link ChainableProcess#process(java.lang.Object)} with the result
 * of the caller passed to the next link. With this you can for example unfold asynchronous (callback-based)
 * calls.
 *
 * @author david baldin
 * @param <T> - Type argument to be passed to the {@link ChainableProcess#process(java.lang.Object)} method.
 */
public abstract class ChainableProcess<T> {

    private ChainableProcess<T> next;

    public abstract void process(T argument);

    public void processLinks(T argument) {
        ChainableProcess<T> currentHandler = this;
        do {
            currentHandler.process(argument);
        } while ((currentHandler = currentHandler.getNext()) != null);
    }

    public void add(ChainableProcess<T> next) {
        ChainableProcess<T> me = this;
        while (me.getNext() != null) {
            me = me.getNext();
        }
        me.setNext(next);
    }

    public void setNext(ChainableProcess<T> next) {
        this.next = next;
    }

    public ChainableProcess<T> getNext() {
        return this.next;
    }
}
