package net.dean.jraw.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.meta.ModelManager;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;

import java.io.IOException;

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
