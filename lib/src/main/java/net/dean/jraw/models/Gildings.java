package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;

@AutoValue
public abstract class Gildings implements Serializable {

    /** Number of silver coins of the object */
    @Json(name = "gid_1")
    public abstract short getSilvers();

    /** Number of gold coins of the object */
    @Json(name = "gid_2")
    public abstract short getGolds();

    /** Number of platinum coins of the object */
    @Json(name = "gid_3")
    public abstract short getPlatinums();

    public static JsonAdapter<Gildings> jsonAdapter(Moshi moshi) {
        return new AutoValue_Gildings.MoshiJsonAdapter(moshi);
    }
}
