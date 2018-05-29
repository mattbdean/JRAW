package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * The main function of an AccountQuery is to store the status of an account. If an account exists and is not suspended,
 * then the {@link Account} object can be used normally.
 */
@AutoValue
public abstract class AccountQuery implements Serializable {
    /** The reddit username being queried */
    public abstract String getName();

    public abstract AccountStatus getStatus();

    /** The account data. Only non-null when the status is {@link AccountStatus#EXISTS}. */
    @Nullable public abstract Account getAccount();

    public static AccountQuery create(String name, AccountStatus status) {
        return create(name, status, null);
    }

    public static AccountQuery create(String name, AccountStatus status, @Nullable Account data) {
        return new AutoValue_AccountQuery(name, status, data);
    }
}
