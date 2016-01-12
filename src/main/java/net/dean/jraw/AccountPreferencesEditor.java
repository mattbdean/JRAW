package net.dean.jraw;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.AccountPreferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AccountPreferencesEditor {
    private final Map<String, String> args;

    /** Instantiates a new AccountPreferencesEditor */
    public AccountPreferencesEditor() {
        this(null);
    }

    /** Instantiates a new AccountPreferencesEditor whose initial values are set to the original's values */
    public AccountPreferencesEditor(AccountPreferences original) {
        this.args = new HashMap<>();

        if (original != null) {
            for (Iterator<Map.Entry<String, JsonNode>> it = original.getDataNode().fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (!entry.getValue().isNull())
                    args.put(entry.getKey(), entry.getValue().asText());
            }
        }
    }

    /** Returns a defensive copy of the arguments that will be sent to the API */
    public Map<String, String> getArgs() {
        return new HashMap<>(args);
    }
}
