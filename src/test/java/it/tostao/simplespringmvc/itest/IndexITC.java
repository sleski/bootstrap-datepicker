package it.tostao.simplespringmvc.itest;

import org.apache.http.HttpResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests on the index page.
 *
 * @author Slawomir Leski <s.leski@e-media.de>
 *
 */

public class IndexITC extends AbstractIntegrationTest {

    /**
     * Checks for 200 on / and /index.
     */
    @Test
    public void testGet() {
        HttpResponse httpResponse = getResponse(Method.GET, getUrl("/"));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
        httpResponse = getResponse(Method.GET, getUrl("/index"));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

}
