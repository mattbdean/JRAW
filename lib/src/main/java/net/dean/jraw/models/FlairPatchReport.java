package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;
import java.util.Map;

@AutoValue
public abstract class FlairPatchReport implements Serializable {
    /** A summary of the action the API took. For example, "skipped," or "added flair for user _vargas_" */
    public abstract String getStatus();

    /** True if the operation completed successfully */
    public abstract boolean isOk();

    /** Any errors that occurred during processing */
    public abstract Map<String, String> getErrors();

    // public abstract List<Map<String, String>> getWarnings();

    public static JsonAdapter<FlairPatchReport> jsonAdapter(Moshi moshi) {
        return new AutoValue_FlairPatchReport.MoshiJsonAdapter(moshi);
    }

    public static FlairPatchReport create(String newStatus, boolean newOk, Map<String, String> newErrors) {
        return new AutoValue_FlairPatchReport(newStatus, newOk, newErrors);
    }
}
