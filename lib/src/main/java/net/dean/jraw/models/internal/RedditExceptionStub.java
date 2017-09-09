package net.dean.jraw.models.internal;

import net.dean.jraw.RedditException;
import net.dean.jraw.http.NetworkException;
import org.jetbrains.annotations.Nullable;

public interface RedditExceptionStub<T extends RedditException> {
    boolean containsError();
    @Nullable T create(NetworkException cause);
}
