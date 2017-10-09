# Live Threads

reddit rolled out support for WebSocket-based event streams in the form of a "live thread" in 2014. These threads are typically dedicated to timely and newsworthy topics like natural disasters, big sporting events, or major political happenings (see [/r/live](https://www.reddit.com/r/live/) for examples).

Creating a live thread requires the ability to create a subreddit. Generally, the user needs to have more than 100 karma. Once the user jumps this hurdle, creating a live thread is a simple process.

{{ LiveThreads.create }}

If you want information about an existing thread, you can create a reference for it by its ID.

{{ LiveThreads.byId }}

Sometimes reddit features a live thread for a particularly prominent event.

{{ LiveThreads.happeningNow }}

## Updates

A live thread consists of a stream of updates. Once an update is posted by an approved thread contributor, it is available to everyone.

{{ LiveThreads.readUpdates }}

> If Paginators are unfamiliar to you, make sure to check out the [Pagination](pagination.md) page.

We can update a live thread fairly easily.

{{ LiveThreads.postUpdate }}

If we find out that an update is incorrect or no longer applicable, we can strike it from the thread. The update will still appear on the thread, but it will have a ~~strikethrough~~ effect. If this isn't good enough, we can permanently remove the update from the thread.

{{ LiveThreads.strikeAndDeleteUpdate }}

Updates cannot be edited, if there's a typo the user has to either strike it or delete it and post a new update.

Once the contributors have decided that there's no more content to share about this event, it can be closed. After that, no further updates can be sent. This process is not reversible.

{{ LiveThreads.close }}

## WebSockets

As stated in the introduction, live threads are based on WebSockets. Instead of polling for updates with `latestUpdates` at a fixed interval, we can leverage WebSockets to let us know when something happens to the thread.

{{ LiveThreads.websocket }}
