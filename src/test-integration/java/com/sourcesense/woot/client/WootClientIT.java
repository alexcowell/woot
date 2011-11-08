package com.sourcesense.woot.client;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class WootClientIT {

    @Test
    public void simpleTest() throws Exception {
        WootClient client1 = new WootClient(1);
        WootClient client2 = new WootClient(2);
        WootClient client3 = new WootClient(3);

        client1.setPeers(Arrays.asList(client2, client3));
        client2.setPeers(Arrays.asList(client1, client3));
        client3.setPeers(Arrays.asList(client1, client2));

        client1.ins(0, 'a');
        assertEquals("a", client1.getString().value());
        assertEquals("a", client2.getString().value());
        assertEquals("a", client3.getString().value());
    }
}
