package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class EmojiS3UploadLeaseResponse {
    public abstract S3UploadLease getS3UploadLease();
    public abstract String getWebsocketUrl();

    public static JsonAdapter<EmojiS3UploadLeaseResponse> jsonAdapter(Moshi moshi) {
        return new AutoValue_EmojiS3UploadLeaseResponse.MoshiJsonAdapter(moshi);
    }

    @AutoValue
    public static abstract class S3UploadLease {
        public abstract String getAction();
        public abstract List<Field> getFields();

        public static JsonAdapter<S3UploadLease> jsonAdapter(Moshi moshi) {
            return new AutoValue_EmojiS3UploadLeaseResponse_S3UploadLease.MoshiJsonAdapter(moshi);
        }
    }

    @AutoValue
    public static abstract class Field {
        public abstract String getName();
        public abstract String getValue();

        public static JsonAdapter<Field> jsonAdapter(Moshi moshi) {
            return new AutoValue_EmojiS3UploadLeaseResponse_Field.MoshiJsonAdapter(moshi);
        }
    }
}
