/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.myproject.util.event;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author david baldin
 */
public class ChainableProcessTest {

    static Integer result = 0;

    @Test
    public void testOperatorChain() {

        ChainableProcess<Integer> root = new ChainableProcess<Integer>() {

            @Override
            public void process(Integer argument) {
                ChainableProcess<Integer> next = this.getNext();
                if (next != null) {
                    next.process(argument * 2);
                }
            }

        };
        root.add(new ChainableProcess<Integer>() {
            @Override
            public void process(Integer argument) {
                ChainableProcess<Integer> next = this.getNext();
                if (next != null) {
                    next.process(argument + 1);
                }
            }
        });
        root.add(new ChainableProcess<Integer>() {
            @Override
            public void process(Integer argument) {
                result = argument;
            }
        });

        root.process(10);

        Assert.assertEquals((Integer) 21, result);
    }
}
