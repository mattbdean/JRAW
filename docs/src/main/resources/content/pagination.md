# Pagination

The reddit API handles pagination through the [[@Listing]] structure. Each Listing holds the data from one page and the ID of the model that is next in the list.

There are several endpoints that return listings, such as `/{sort}` (e.g. `/hot` and `/top`) and `/message/{where}` (e.g. `/message/inbox`, `/message/sent`). JRAW handles pagination through [[@Paginator]]s. Paginators keep track of the last Listing received and handle sending future requests.

The API generally gives you Paginator builders so you can customize the query before you start requesting pages. Here's a quick example:

{{ Pagination.simple }}

Notice we used [[@DefaultPaginator]] instead of a normal [[@Paginator]]. Paginators come in two different flavors:

 - Barebones: Only supports an explicit limit to the amount of data per page.
 - Default: Supports a limit, sorting (e.g. `top`), and optionally a time period (e.g. `hour`)

There has to be a distinction because not every endpoint that serves listings can handle a sorting and time period.

For the sake of example, here's what specifying all properties of the builder could look like:

{{ Pagination.usingAllOptions }}

## Convenience

The Paginator class implements Iterable, so it's entirely possible to do something like this:

{{ Pagination.iterableWhile }}

Or to simplify even more:

{{ Pagination.iterableForEach }}

However, it's usually not a good idea to try to iterate through the entire front page since it could take a while. Instead, Paginator brings some handy methods to help you write less code.

The `accumulate` method will fetch a certain amount of pages for you.

{{ Pagination.accumulate }}

If keeping track of what data is in what page isn't important, we can use the `accumulateMerged` method, which does the same thing as `accumulate` and then merges it into one List. The classic example is fetching the user's subscriptions.

{{ Pagination.accumulateMerged }}
