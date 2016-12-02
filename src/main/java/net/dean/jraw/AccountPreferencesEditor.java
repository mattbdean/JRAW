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

    /** Sets an setting value that will be returned with getArgs() (used in saving the AccountPreferenceEditor in AccountManager.java
     *
     * @param setting the setting value to set in the HashMap
     * @param value the value for the setting
     *
     * The setting string and value string must be valid!
     * A list of valid settings and values can be found here https://www.reddit.com/dev/api#PATCH_api_v1_me_prefs*/
    public void setArgs(String setting, String value){
        args.put(setting, value);
    }

    /** Returns a defensive copy of the arguments that will be sent to the API */
    public Map<String, String> getArgs() {
        return new HashMap<>(args);
    }
}
