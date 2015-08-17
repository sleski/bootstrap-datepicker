package it.tostao.simplespringmvc.controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for Index.
 * @author sleski
 *
 */
public class IndexTest {

    /**
     * tests if the correct view information is coming back.
     */
    @Test
    public void testGet() {
        Index index = new Index();
        assertEquals("index", index.get().getViewName());
    }

}
