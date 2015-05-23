package net.dean.jraw.fluent;

import net.dean.jraw.models.Thing;
import net.dean.jraw.paginators.Paginator;

public class PaginatorWrapper<T extends Thing, W extends AbstractPaginatorWrapper<? super W, T>>
        extends AbstractPaginatorWrapper<W, T> {

    protected PaginatorWrapper(Paginator<T> paginator) {
        super(paginator);
    }
}
