<!--- Generated 2017-09-24 at 00:50:42 EDT. Use `./gradlew :meta:update` to update. DO NOT MODIFY DIRECTLY -->

Endpoints
=========

This file contains a list of all the endpoints (regardless of if they have been implemented) that can be found at the [official reddit API docs](https://www.reddit.com/dev/api/oauth). To update this file, run `./gradlew :meta:update`

So far, **68** endpoints (out of 175) have been implemented.

(any scope)
-----------

| Method | Endpoint                                                                                     | Implementation                                                                                                                                                            |
|:------:| -------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/comment`](https://www.reddit.com/dev/api/oauth#POST_api_comment)                      | [`PublicContributionReference.reply()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/PublicContributionReference.kt#L78) |
| `POST` | [`/api/friend`](https://www.reddit.com/dev/api/oauth#POST_api_friend)                        | None                                                                                                                                                                      |
| `GET`  | [`/api/needs_captcha`](https://www.reddit.com/dev/api/oauth#GET_api_needs_captcha)           | None                                                                                                                                                                      |
| `POST` | [`/api/unfriend`](https://www.reddit.com/dev/api/oauth#POST_api_unfriend)                    | None                                                                                                                                                                      |
| `GET`  | [`/api/username_available`](https://www.reddit.com/dev/api/oauth#GET_api_username_available) | None                                                                                                                                                                      |
| `GET`  | [`/api/v1/scopes`](https://www.reddit.com/dev/api/oauth#GET_api_v1_scopes)                   | None                                                                                                                                                                      |

account
-------

| Method  | Endpoint                                                                         | Implementation                                                                                                                                              |
|:-------:| -------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `PATCH` | [`/api/v1/me/prefs`](https://www.reddit.com/dev/api/oauth#PATCH_api_v1_me_prefs) | [`SelfUserReference.patchPrefs()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SelfUserReference.kt#L165) |
| `POST`  | [`/api/block_user`](https://www.reddit.com/dev/api/oauth#POST_api_block_user)    | None                                                                                                                                                        |

creddits
--------

| Method | Endpoint                                                                                                | Implementation |
|:------:| ------------------------------------------------------------------------------------------------------- | -------------- |
| `POST` | [`/api/v1/gold/gild/{fullname}`](https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_gild_{fullname}) | None           |
| `POST` | [`/api/v1/gold/give/{username}`](https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_give_{username}) | None           |

edit
----

| Method | Endpoint                                                                                                        | Implementation                                                                                                                                                                   |
|:------:| --------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/del`](https://www.reddit.com/dev/api/oauth#POST_api_del)                                                 | [`PublicContributionReference.delete()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/PublicContributionReference.kt#L95)       |
| `POST` | [`/api/editusertext`](https://www.reddit.com/dev/api/oauth#POST_api_editusertext)                               | [`PublicContributionReference.edit()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/PublicContributionReference.kt#L103)        |
| `POST` | [`/api/live/{thread}/delete_update`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_delete_update) | [`LiveThreadReference.deleteUpdate()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/LiveThreadReference.kt#L69)                 |
| `POST` | [`/api/live/{thread}/strike_update`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_strike_update) | [`LiveThreadReference.strikeUpdate()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/LiveThreadReference.kt#L64)                 |
| `POST` | [`/api/sendreplies`](https://www.reddit.com/dev/api/oauth#POST_api_sendreplies)                                 | [`PublicContributionReference.sendReplies()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/PublicContributionReference.kt#L115) |

flair
-----

| Method | Endpoint                                                                                | Implementation                                                                                                                                                             |
|:------:| --------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/flairselector`](https://www.reddit.com/dev/api/oauth#POST_api_flairselector)     | [`UserFlairReference.current()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/UserFlairReference.kt#L110)                 |
| `GET`  | [`/api/link_flair`](https://www.reddit.com/dev/api/oauth#GET_api_link_flair)            | [`SubredditReference.linkFlairOptions()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L131)        |
| `POST` | [`/api/selectflair`](https://www.reddit.com/dev/api/oauth#POST_api_selectflair)         | [`FlairReference.updateTo()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/FlairReference.kt#L30)                         |
| `POST` | [`/api/setflairenabled`](https://www.reddit.com/dev/api/oauth#POST_api_setflairenabled) | [`SelfUserFlairReference.setFlairEnabled()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SelfUserFlairReference.kt#L146) |
| `GET`  | [`/api/user_flair`](https://www.reddit.com/dev/api/oauth#GET_api_user_flair)            | [`SubredditReference.userFlairOptions()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L122)        |

history
-------

| Method | Endpoint                                                                                       | Implementation                                                                                                                                  |
|:------:| ---------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| `GET`  | [`/user/{username}/{where}`](https://www.reddit.com/dev/api/oauth#GET_user_{username}_{where}) | [`UserReference.history()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/UserReference.kt#L63) |

identity
--------

| Method | Endpoint                                                                             | Implementation                                                                                                                                         |
|:------:| ------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `GET`  | [`/api/v1/me`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me)                   | [`UserReference.about()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/UserReference.kt#L21)          |
| `GET`  | [`/api/v1/me/prefs`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_prefs)       | [`SelfUserReference.prefs()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SelfUserReference.kt#L151) |
| `GET`  | [`/api/v1/me/trophies`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_trophies) | [`UserReference.trophies()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/UserReference.kt#L33)       |

livemanage
----------

| Method | Endpoint                                                                                                                                    | Implementation                                                                                                                                            |
|:------:| ------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/live/{thread}/close_thread`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_close_thread)                               | [`LiveThreadReference.close()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/LiveThreadReference.kt#L88) |
| `POST` | [`/api/live/{thread}/edit`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_edit)                                               | [`LiveThreadReference.edit()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/LiveThreadReference.kt#L44)  |
| `POST` | [`/api/live/{thread}/accept_contributor_invite`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_accept_contributor_invite)     | None                                                                                                                                                      |
| `POST` | [`/api/live/{thread}/invite_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_invite_contributor)                   | None                                                                                                                                                      |
| `POST` | [`/api/live/{thread}/leave_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_leave_contributor)                     | None                                                                                                                                                      |
| `POST` | [`/api/live/{thread}/rm_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_rm_contributor)                           | None                                                                                                                                                      |
| `POST` | [`/api/live/{thread}/rm_contributor_invite`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_rm_contributor_invite)             | None                                                                                                                                                      |
| `POST` | [`/api/live/{thread}/set_contributor_permissions`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_set_contributor_permissions) | None                                                                                                                                                      |

modconfig
---------

| Method | Endpoint                                                                                               | Implementation |
|:------:| ------------------------------------------------------------------------------------------------------ | -------------- |
| `POST` | [`/api/delete_sr_banner`](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_banner)              | None           |
| `POST` | [`/api/delete_sr_header`](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_header)              | None           |
| `POST` | [`/api/delete_sr_icon`](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_icon)                  | None           |
| `POST` | [`/api/delete_sr_img`](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_img)                    | None           |
| `POST` | [`/api/site_admin`](https://www.reddit.com/dev/api/oauth#POST_api_site_admin)                          | None           |
| `POST` | [`/api/subreddit_stylesheet`](https://www.reddit.com/dev/api/oauth#POST_api_subreddit_stylesheet)      | None           |
| `POST` | [`/api/upload_sr_img`](https://www.reddit.com/dev/api/oauth#POST_api_upload_sr_img)                    | None           |
| `GET`  | [`/r/{subreddit}/about/edit`](https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about_edit)       | None           |
| `GET`  | [`/r/{subreddit}/about/traffic`](https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about_traffic) | None           |
| `GET`  | [`/stylesheet`](https://www.reddit.com/dev/api/oauth#GET_stylesheet)                                   | None           |

modcontributors
---------------

| Method | Endpoint                                                                                            | Implementation |
|:------:| --------------------------------------------------------------------------------------------------- | -------------- |
| `POST` | [`/api/mute_message_author`](https://www.reddit.com/dev/api/oauth#POST_api_mute_message_author)     | None           |
| `POST` | [`/api/unmute_message_author`](https://www.reddit.com/dev/api/oauth#POST_api_unmute_message_author) | None           |

modflair
--------

| Method | Endpoint                                                                                        | Implementation |
|:------:| ----------------------------------------------------------------------------------------------- | -------------- |
| `POST` | [`/api/clearflairtemplates`](https://www.reddit.com/dev/api/oauth#POST_api_clearflairtemplates) | None           |
| `POST` | [`/api/deleteflair`](https://www.reddit.com/dev/api/oauth#POST_api_deleteflair)                 | None           |
| `POST` | [`/api/deleteflairtemplate`](https://www.reddit.com/dev/api/oauth#POST_api_deleteflairtemplate) | None           |
| `POST` | [`/api/flair`](https://www.reddit.com/dev/api/oauth#POST_api_flair)                             | None           |
| `POST` | [`/api/flairconfig`](https://www.reddit.com/dev/api/oauth#POST_api_flairconfig)                 | None           |
| `POST` | [`/api/flaircsv`](https://www.reddit.com/dev/api/oauth#POST_api_flaircsv)                       | None           |
| `GET`  | [`/api/flairlist`](https://www.reddit.com/dev/api/oauth#GET_api_flairlist)                      | None           |
| `POST` | [`/api/flairtemplate`](https://www.reddit.com/dev/api/oauth#POST_api_flairtemplate)             | None           |

modlog
------

| Method | Endpoint                                                           | Implementation |
|:------:| ------------------------------------------------------------------ | -------------- |
| `GET`  | [`/about/log`](https://www.reddit.com/dev/api/oauth#GET_about_log) | None           |

modmail
-------

|  Method  | Endpoint                                                                                                                                             | Implementation |
|:--------:| ---------------------------------------------------------------------------------------------------------------------------------------------------- | -------------- |
|  `POST`  | [`/api/mod/bulk_read`](https://www.reddit.com/dev/api/oauth#POST_api_mod_bulk_read)                                                                  | None           |
|  `GET`   | [`/api/mod/conversations`](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations)                                                           | None           |
|  `POST`  | [`/api/mod/conversations`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations)                                                          | None           |
|  `POST`  | [`/api/mod/conversations/read`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_read)                                                | None           |
|  `GET`   | [`/api/mod/conversations/subreddits`](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_subreddits)                                     | None           |
|  `POST`  | [`/api/mod/conversations/unread`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_unread)                                            | None           |
|  `GET`   | [`/api/mod/conversations/unread/count`](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_unread_count)                                 | None           |
|  `GET`   | [`/api/mod/conversations/{conversation_id}`](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_:conversation_id)                        | None           |
|  `POST`  | [`/api/mod/conversations/{conversation_id}`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id)                       | None           |
|  `POST`  | [`/api/mod/conversations/{conversation_id}/archive`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_archive)       | None           |
| `DELETE` | [`/api/mod/conversations/{conversation_id}/highlight`](https://www.reddit.com/dev/api/oauth#DELETE_api_mod_conversations_:conversation_id_highlight) | None           |
|  `POST`  | [`/api/mod/conversations/{conversation_id}/highlight`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_highlight)   | None           |
|  `POST`  | [`/api/mod/conversations/{conversation_id}/mute`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_mute)             | None           |
|  `POST`  | [`/api/mod/conversations/{conversation_id}/unarchive`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_unarchive)   | None           |
|  `POST`  | [`/api/mod/conversations/{conversation_id}/unmute`](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_unmute)         | None           |
|  `GET`   | [`/api/mod/conversations/{conversation_id}/user`](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_:conversation_id_user)              | None           |

modothers
---------

| Method | Endpoint                                                                              | Implementation |
|:------:| ------------------------------------------------------------------------------------- | -------------- |
| `POST` | [`/api/setpermissions`](https://www.reddit.com/dev/api/oauth#POST_api_setpermissions) | None           |

modposts
--------

| Method | Endpoint                                                                                          | Implementation |
|:------:| ------------------------------------------------------------------------------------------------- | -------------- |
| `POST` | [`/api/approve`](https://www.reddit.com/dev/api/oauth#POST_api_approve)                           | None           |
| `POST` | [`/api/distinguish`](https://www.reddit.com/dev/api/oauth#POST_api_distinguish)                   | None           |
| `POST` | [`/api/ignore_reports`](https://www.reddit.com/dev/api/oauth#POST_api_ignore_reports)             | None           |
| `POST` | [`/api/lock`](https://www.reddit.com/dev/api/oauth#POST_api_lock)                                 | None           |
| `POST` | [`/api/marknsfw`](https://www.reddit.com/dev/api/oauth#POST_api_marknsfw)                         | None           |
| `POST` | [`/api/remove`](https://www.reddit.com/dev/api/oauth#POST_api_remove)                             | None           |
| `POST` | [`/api/set_contest_mode`](https://www.reddit.com/dev/api/oauth#POST_api_set_contest_mode)         | None           |
| `POST` | [`/api/set_subreddit_sticky`](https://www.reddit.com/dev/api/oauth#POST_api_set_subreddit_sticky) | None           |
| `POST` | [`/api/set_suggested_sort`](https://www.reddit.com/dev/api/oauth#POST_api_set_suggested_sort)     | None           |
| `POST` | [`/api/spoiler`](https://www.reddit.com/dev/api/oauth#POST_api_spoiler)                           | None           |
| `POST` | [`/api/unignore_reports`](https://www.reddit.com/dev/api/oauth#POST_api_unignore_reports)         | None           |
| `POST` | [`/api/unlock`](https://www.reddit.com/dev/api/oauth#POST_api_unlock)                             | None           |
| `POST` | [`/api/unmarknsfw`](https://www.reddit.com/dev/api/oauth#POST_api_unmarknsfw)                     | None           |
| `POST` | [`/api/unspoiler`](https://www.reddit.com/dev/api/oauth#POST_api_unspoiler)                       | None           |

modself
-------

| Method | Endpoint                                                                                                | Implementation |
|:------:| ------------------------------------------------------------------------------------------------------- | -------------- |
| `POST` | [`/api/accept_moderator_invite`](https://www.reddit.com/dev/api/oauth#POST_api_accept_moderator_invite) | None           |
| `POST` | [`/api/leavecontributor`](https://www.reddit.com/dev/api/oauth#POST_api_leavecontributor)               | None           |
| `POST` | [`/api/leavemoderator`](https://www.reddit.com/dev/api/oauth#POST_api_leavemoderator)                   | None           |

modwiki
-------

| Method | Endpoint                                                                                              | Implementation |
|:------:| ----------------------------------------------------------------------------------------------------- | -------------- |
| `POST` | [`/api/wiki/alloweditor/{act}`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_{act}) | None           |
| `POST` | [`/api/wiki/hide`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_hide)                           | None           |
| `POST` | [`/api/wiki/revert`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_revert)                       | None           |
| `GET`  | [`/wiki/settings/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_settings_{page})              | None           |
| `POST` | [`/wiki/settings/{page}`](https://www.reddit.com/dev/api/oauth#POST_wiki_settings_{page})             | None           |

mysubreddits
------------

| Method | Endpoint                                                                                                 | Implementation                                                                                                                                              |
|:------:| -------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `GET`  | [`/api/v1/me/karma`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_karma)                           | [`SelfUserReference.karma()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SelfUserReference.kt#L188)      |
| `GET`  | [`/subreddits/mine/{where}`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_{where})           | [`SelfUserReference.subreddits()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SelfUserReference.kt#L180) |
| `GET`  | [`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends_{username}) | None                                                                                                                                                        |

privatemessages
---------------

| Method | Endpoint                                                                                                 | Implementation                                                                                                                                        |
|:------:| -------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/compose`](https://www.reddit.com/dev/api/oauth#POST_api_compose)                                  | [`InboxReference.compose()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/InboxReference.kt#L25)     |
| `POST` | [`/api/del_msg`](https://www.reddit.com/dev/api/oauth#POST_api_del_msg)                                  | [`InboxReference.delete()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/InboxReference.kt#L70)      |
| `POST` | [`/api/read_all_messages`](https://www.reddit.com/dev/api/oauth#POST_api_read_all_messages)              | [`InboxReference.markAllRead()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/InboxReference.kt#L54) |
| `POST` | [`/api/read_message`](https://www.reddit.com/dev/api/oauth#POST_api_read_message)                        | [`InboxReference.markRead()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/InboxReference.kt#L46)    |
| `POST` | [`/api/unread_message`](https://www.reddit.com/dev/api/oauth#POST_api_unread_message)                    | [`InboxReference.markRead()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/InboxReference.kt#L46)    |
| `GET`  | [`/message/{where}`](https://www.reddit.com/dev/api/oauth#GET_message_{where})                           | [`InboxReference.iterate()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/InboxReference.kt#L22)     |
| `POST` | [`/api/block`](https://www.reddit.com/dev/api/oauth#POST_api_block)                                      | None                                                                                                                                                  |
| `POST` | [`/api/collapse_message`](https://www.reddit.com/dev/api/oauth#POST_api_collapse_message)                | None                                                                                                                                                  |
| `POST` | [`/api/unblock_subreddit`](https://www.reddit.com/dev/api/oauth#POST_api_unblock_subreddit)              | None                                                                                                                                                  |
| `POST` | [`/api/uncollapse_message`](https://www.reddit.com/dev/api/oauth#POST_api_uncollapse_message)            | None                                                                                                                                                  |
| `GET`  | [`/api/user_data_by_account_ids`](https://www.reddit.com/dev/api/oauth#GET_api_user_data_by_account_ids) | None                                                                                                                                                  |

read
----

| Method | Endpoint                                                                                                           | Implementation                                                                                                                                                          |
|:------:| ------------------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `GET`  | [`/api/info`](https://www.reddit.com/dev/api/oauth#GET_api_info)                                                   | [`RedditClient.lookup()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/RedditClient.kt#L314)                                      |
| `GET`  | [`/api/live/happening_now`](https://www.reddit.com/dev/api/oauth#GET_api_live_happening_now)                       | [`RedditClient.happeningNow()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/RedditClient.kt#L338)                                |
| `GET`  | [`/api/morechildren`](https://www.reddit.com/dev/api/oauth#GET_api_morechildren)                                   | [`CommentNode.loadMore()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/tree/CommentNode.kt#L-1)                                  |
| `GET`  | [`/api/multi/mine`](https://www.reddit.com/dev/api/oauth#GET_api_multi_mine)                                       | [`UserReference.listMultis()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/UserReference.kt#L80)                      |
| `GET`  | [`/api/multi/user/{username}`](https://www.reddit.com/dev/api/oauth#GET_api_multi_user_{username})                 | [`UserReference.listMultis()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/UserReference.kt#L80)                      |
| `GET`  | [`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath})                         | [`MultiredditReference.about()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L40)             |
| `GET`  | [`/api/multi/{multipath}/description`](https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath}_description) | [`MultiredditReference.description()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L48)       |
| `PUT`  | [`/api/multi/{multipath}/description`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath}_description) | [`MultiredditReference.updateDescription()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L57) |
| `GET`  | [`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath}_r_{srname})   | [`MultiredditReference.subredditInfo()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L113)    |
| `GET`  | [`/api/v1/user/{username}/trophies`](https://www.reddit.com/dev/api/oauth#GET_api_v1_user_{username}_trophies)     | [`UserReference.trophies()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/UserReference.kt#L33)                        |
| `GET`  | [`/comments/{article}`](https://www.reddit.com/dev/api/oauth#GET_comments_{article})                               | [`SubmissionReference.comments()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubmissionReference.kt#L28)            |
| `GET`  | [`/hot`](https://www.reddit.com/dev/api/oauth#GET_hot)                                                             | [`SubredditReference.posts()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L34)                 |
| `GET`  | [`/live/{thread}`](https://www.reddit.com/dev/api/oauth#GET_live_{thread})                                         | [`LiveThreadReference.latestUpdates()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/LiveThreadReference.kt#L27)       |
| `GET`  | [`/live/{thread}/about`](https://www.reddit.com/dev/api/oauth#GET_live_{thread}_about)                             | [`LiveThreadReference.about()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/LiveThreadReference.kt#L17)               |
| `GET`  | [`/new`](https://www.reddit.com/dev/api/oauth#GET_new)                                                             | [`SubredditReference.posts()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L34)                 |
| `GET`  | [`/r/{subreddit}/about`](https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about)                             | [`SubredditReference.about()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L25)                 |
| `GET`  | [`/random`](https://www.reddit.com/dev/api/oauth#GET_random)                                                       | [`RedditClient.randomSubreddit()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/RedditClient.kt#L285)                             |
| `GET`  | [`/rising`](https://www.reddit.com/dev/api/oauth#GET_rising)                                                       | [`SubredditReference.posts()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L34)                 |
| `GET`  | [`/user/{username}/about`](https://www.reddit.com/dev/api/oauth#GET_user_{username}_about)                         | [`UserReference.about()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/UserReference.kt#L21)                           |
| `GET`  | [`/{sort}`](https://www.reddit.com/dev/api/oauth#GET_{sort})                                                       | [`SubredditReference.posts()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L34)                 |
| `GET`  | [`/about/{location}`](https://www.reddit.com/dev/api/oauth#GET_about_{location})                                   | None                                                                                                                                                                    |
| `GET`  | [`/about/{where}`](https://www.reddit.com/dev/api/oauth#GET_about_{where})                                         | None                                                                                                                                                                    |
| `GET`  | [`/api/live/by_id/{names}`](https://www.reddit.com/dev/api/oauth#GET_api_live_by_id_{names})                       | None                                                                                                                                                                    |
| `GET`  | [`/api/recommend/sr/{srnames}`](https://www.reddit.com/dev/api/oauth#GET_api_recommend_sr_{srnames})               | None                                                                                                                                                                    |
| `POST` | [`/api/search_reddit_names`](https://www.reddit.com/dev/api/oauth#POST_api_search_reddit_names)                    | None                                                                                                                                                                    |
| `POST` | [`/api/search_subreddits`](https://www.reddit.com/dev/api/oauth#POST_api_search_subreddits)                        | None                                                                                                                                                                    |
| `GET`  | [`/api/subreddits_by_topic`](https://www.reddit.com/dev/api/oauth#GET_api_subreddits_by_topic)                     | None                                                                                                                                                                    |
| `GET`  | [`/by_id/{names}`](https://www.reddit.com/dev/api/oauth#GET_by_id_{names})                                         | None                                                                                                                                                                    |
| `GET`  | [`/duplicates/{article}`](https://www.reddit.com/dev/api/oauth#GET_duplicates_{article})                           | None                                                                                                                                                                    |
| `GET`  | [`/live/{thread}/contributors`](https://www.reddit.com/dev/api/oauth#GET_live_{thread}_contributors)               | None                                                                                                                                                                    |
| `GET`  | [`/live/{thread}/discussions`](https://www.reddit.com/dev/api/oauth#GET_live_{thread}_discussions)                 | None                                                                                                                                                                    |
| `GET`  | [`/prefs/{where}`](https://www.reddit.com/dev/api/oauth#GET_prefs_{where})                                         | None                                                                                                                                                                    |
| `GET`  | [`/r/{subreddit}/about/rules`](https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about_rules)                 | None                                                                                                                                                                    |
| `GET`  | [`/search`](https://www.reddit.com/dev/api/oauth#GET_search)                                                       | None                                                                                                                                                                    |
| `GET`  | [`/sidebar`](https://www.reddit.com/dev/api/oauth#GET_sidebar)                                                     | None                                                                                                                                                                    |
| `GET`  | [`/sticky`](https://www.reddit.com/dev/api/oauth#GET_sticky)                                                       | None                                                                                                                                                                    |
| `GET`  | [`/subreddits/search`](https://www.reddit.com/dev/api/oauth#GET_subreddits_search)                                 | None                                                                                                                                                                    |
| `GET`  | [`/subreddits/{where}`](https://www.reddit.com/dev/api/oauth#GET_subreddits_{where})                               | None                                                                                                                                                                    |
| `GET`  | [`/users/{where}`](https://www.reddit.com/dev/api/oauth#GET_users_{where})                                         | None                                                                                                                                                                    |

report
------

| Method | Endpoint                                                                                          | Implementation                                                                                                                                                |
|:------:| ------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/hide`](https://www.reddit.com/dev/api/oauth#POST_api_hide)                                 | [`SubmissionReference.setHidden()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubmissionReference.kt#L75) |
| `POST` | [`/api/unhide`](https://www.reddit.com/dev/api/oauth#POST_api_unhide)                             | [`SubmissionReference.setHidden()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubmissionReference.kt#L75) |
| `POST` | [`/api/live/{thread}/report`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_report) | None                                                                                                                                                          |
| `POST` | [`/api/report`](https://www.reddit.com/dev/api/oauth#POST_api_report)                             | None                                                                                                                                                          |
| `POST` | [`/api/report_user`](https://www.reddit.com/dev/api/oauth#POST_api_report_user)                   | None                                                                                                                                                          |

save
----

| Method | Endpoint                                                                                 | Implementation                                                                                                                                                               |
|:------:| ---------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/save`](https://www.reddit.com/dev/api/oauth#POST_api_save)                        | [`PublicContributionReference.setSaved()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/PublicContributionReference.kt#L65) |
| `POST` | [`/api/unsave`](https://www.reddit.com/dev/api/oauth#POST_api_unsave)                    | [`PublicContributionReference.setSaved()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/PublicContributionReference.kt#L65) |
| `GET`  | [`/api/saved_categories`](https://www.reddit.com/dev/api/oauth#GET_api_saved_categories) | None                                                                                                                                                                         |
| `POST` | [`/api/store_visits`](https://www.reddit.com/dev/api/oauth#POST_api_store_visits)        | None                                                                                                                                                                         |

submit
------

| Method | Endpoint                                                                                          | Implementation                                                                                                                                                    |
|:------:| ------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/live/create`](https://www.reddit.com/dev/api/oauth#POST_api_live_create)                   | [`SelfUserReference.createLiveThread()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SelfUserReference.kt#L132) |
| `POST` | [`/api/live/{thread}/update`](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_update) | [`LiveThreadReference.postUpdate()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/LiveThreadReference.kt#L53)    |
| `POST` | [`/api/submit`](https://www.reddit.com/dev/api/oauth#POST_api_submit)                             | [`SubredditReference.submit()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L56)          |
| `GET`  | [`/api/submit_text`](https://www.reddit.com/dev/api/oauth#GET_api_submit_text)                    | [`SubredditReference.submitText()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L84)      |

subscribe
---------

|  Method  | Endpoint                                                                                                            | Implementation                                                                                                                                                         |
|:--------:| ------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|  `POST`  | [`/api/multi/copy`](https://www.reddit.com/dev/api/oauth#POST_api_multi_copy)                                       | [`MultiredditReference.copyTo()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L81)           |
|  `POST`  | [`/api/multi/rename`](https://www.reddit.com/dev/api/oauth#POST_api_multi_rename)                                   | [`MultiredditReference.rename()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L89)           |
| `DELETE` | [`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#DELETE_api_multi_{multipath})                       | [`MultiredditReference.delete()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L69)           |
|  `POST`  | [`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#POST_api_multi_{multipath})                         | [`MultiredditReference.createOrUpdate()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L29)   |
|  `PUT`   | [`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath})                          | [`MultiredditReference.createOrUpdate()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L29)   |
| `DELETE` | [`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#DELETE_api_multi_{multipath}_r_{srname}) | [`MultiredditReference.removeSubreddit()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L135) |
|  `PUT`   | [`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath}_r_{srname})    | [`MultiredditReference.addSubreddit()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/MultiredditReference.kt#L123)    |
|  `POST`  | [`/api/subscribe`](https://www.reddit.com/dev/api/oauth#POST_api_subscribe)                                         | [`SubredditReference.setSubscribed()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/SubredditReference.kt#L99)        |
| `DELETE` | [`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#DELETE_api_v1_me_friends_{username})         | None                                                                                                                                                                   |
|  `PUT`   | [`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#PUT_api_v1_me_friends_{username})            | None                                                                                                                                                                   |

vote
----

| Method | Endpoint                                                          | Implementation                                                                                                                                                              |
|:------:| ----------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `POST` | [`/api/vote`](https://www.reddit.com/dev/api/oauth#POST_api_vote) | [`PublicContributionReference.setVote()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/PublicContributionReference.kt#L39) |

wikiedit
--------

| Method | Endpoint                                                                    | Implementation |
|:------:| --------------------------------------------------------------------------- | -------------- |
| `POST` | [`/api/wiki/edit`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_edit) | None           |

wikiread
--------

| Method | Endpoint                                                                                       | Implementation                                                                                                                                           |
|:------:| ---------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `GET`  | [`/wiki/discussions/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_discussions_{page}) | [`WikiReference.discussionsAbout()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/WikiReference.kt#L41) |
| `GET`  | [`/wiki/pages`](https://www.reddit.com/dev/api/oauth#GET_wiki_pages)                           | [`WikiReference.pages()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/WikiReference.kt#L15)            |
| `GET`  | [`/wiki/revisions`](https://www.reddit.com/dev/api/oauth#GET_wiki_revisions)                   | [`WikiReference.revisions()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/WikiReference.kt#L31)        |
| `GET`  | [`/wiki/revisions/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_revisions_{page})     | [`WikiReference.revisionsFor()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/WikiReference.kt#L36)     |
| `GET`  | [`/wiki/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_{page})                         | [`WikiReference.page()`](https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/net/dean/jraw/references/WikiReference.kt#L23)             |

