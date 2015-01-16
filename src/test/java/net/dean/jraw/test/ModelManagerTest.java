package net.dean.jraw.test;

import net.dean.jraw.models.Account;
import net.dean.jraw.models.meta.ModelManager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by matthew on 1/14/15.
 */
public class ModelManagerTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testParseJsonBadType() throws IOException {
        String json = "{" +
                "\"kind\": \"t6\"," + // t6 = award
                "\"data\": {}" +
                "}";
        JsonNode mockNode = new ObjectMapper().readTree(json);
        // Should throw an IllegalArgumentException
        ModelManager.create(mockNode, Account.class);
    }
}
