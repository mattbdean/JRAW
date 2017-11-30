package net.dean.jraw.docs.samples;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import net.dean.jraw.databind.Enveloped;
import net.dean.jraw.docs.CodeSample;

import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings("unused")
final class Internals {
    @CodeSample
    void map(String jsonString) throws Exception {
        Moshi moshi = new Moshi.Builder().build();

        // Create a Type that represents Map<String, Object>
        Type mapType = Types.newParameterizedType(Map.class, String.class, Object.class);

        // Use Moshi to create a JsonAdapter that can (de)serialize JSON into a Map<String, Object>
        JsonAdapter<Map<String, Object>> mapAdapter = moshi.adapter(mapType);

        // Deserialize the JSON into a Map<String, Object>
        Map<String, Object> data = mapAdapter.fromJson(jsonString);

        // Now we can query the data however we like:
        int linkKarma = (int) data.get("link_karma");
        boolean isGold = (boolean) data.get("is_gold");
        boolean isVerified = (boolean) data.get("verified");
    }

    @CodeSample
    void plainObject(Moshi moshi, String jsonString) throws Exception {
        // Use Moshi to create a JsonAdapter for our Account class
        JsonAdapter<Account> accountAdapter = moshi.adapter(Account.class);

        // Deserialize the JSON into an Account instance
        Account data = accountAdapter.fromJson(jsonString);

        int linkKarma = data.getLinkKarma();
        boolean isGold = data.isGold();
        boolean isVerified = data.isVerified();
    }

    @CodeSample
    void enveloped(Moshi moshi, String jsonString) throws Exception {
        // Get a JsonAdapter that knows how to handle enveloped data
        JsonAdapter<Account> accountAdapter = moshi.adapter(Account.class, Enveloped.class);

        // Deserialization is just like before
        Account data = accountAdapter.fromJson(jsonString);
    }

    private static final class Account {
        @Json(name = "link_karma") private int linkKarma;
        @Json(name = "is_gold") private boolean isGold;

        // Notice we don't need the @Json annotation here, the implied JSON key is "verified"
        private boolean verified;

        int getLinkKarma() {
            return linkKarma;
        }

        boolean isGold() {
            return isGold;
        }

        boolean isVerified() {
            return verified;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Account account = (Account) o;

            if (linkKarma != account.linkKarma) return false;
            if (isGold != account.isGold) return false;
            return verified == account.verified;
        }

        @Override
        public int hashCode() {
            int result = linkKarma;
            result = 31 * result + (isGold ? 1 : 0);
            result = 31 * result + (verified ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Account{" +
                "linkKarma=" + linkKarma +
                ", isGold=" + isGold +
                ", verified=" + verified +
                '}';
        }
    }
}
