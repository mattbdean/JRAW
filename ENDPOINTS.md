<!--- Generated 2014-08-19 at 07:59:23 EDT. Use ./gradlew :endpoints:update to update. DO NOT MODIFY DIRECTLY -->
#Unimplemented (130/166)
####account
~~~
/api/clear_sessions
/api/delete_user
/api/register
/api/update
/api/update_email
/api/update_password
/api/v1/me
/api/v1/me/blocked
/api/v1/me/friends
/api/v1/me/karma
/api/v1/me/prefs
/api/v1/me/trophies
/prefs/blocked
/prefs/friends
/prefs/where
~~~

####apps
~~~
/api/deleteapp
/api/revokeapp
/api/setappicon
/api/updateapp
~~~

####flair
~~~
/api/clearflairtemplates
/api/deleteflair
/api/deleteflairtemplate
/api/flair
/api/flairconfig
/api/flaircsv
/api/flairlist
/api/flairselector
/api/flairtemplate
/api/selectflair
/api/setflairenabled
~~~

####links & comments
~~~
/api/comment
/api/editusertext
/api/info
/api/morechildren
/api/report
/api/saved_categories.json
/api/set_contest_mode
/api/set_subreddit_sticky
/api/store_visits
~~~

####listings
~~~
/comments/article
~~~

####live threads
~~~
/api/live/create
/api/live/thread/accept_contributor_invite
/api/live/thread/close_thread
/api/live/thread/delete_update
/api/live/thread/edit
/api/live/thread/invite_contributor
/api/live/thread/leave_contributor
/api/live/thread/report
/api/live/thread/rm_contributor
/api/live/thread/rm_contributor_invite
/api/live/thread/set_contributor_permissions
/api/live/thread/strike_update
/api/live/thread/update
/live/thread
/live/thread/about.json
/live/thread/contributors.json
/live/thread/discussions
~~~

####moderation
~~~
/about/log
/api/accept_moderator_invite
/api/approve
/api/distinguish
/api/ignore_reports
/api/leavecontributor
/api/leavemoderator
/api/remove
/api/unignore_reports
/stylesheet
~~~

####multis
~~~
/api/multi/multipath/copy
PUT /api/multi/multipath/description
PUT /api/multi/multipath/r/srname
DELETE /api/multi/multipath/r/srname
/api/multi/multipath/r/srname
/api/multi/multipath/rename
~~~

####private messages
~~~
/api/block
/api/compose
/api/read_message
/api/unread_message
/message/inbox
/message/sent
/message/unread
/message/where
~~~

####reddit gold
~~~
/api/v1/gold/gild/fullname
/api/v1/gold/give/username
~~~

####search
~~~
/search
~~~

####subreddits
~~~
/about/banned
/about/contributors
/about/moderators
/about/wikibanned
/about/wikicontributors
/about/where
/api/delete_sr_header
/api/delete_sr_img
/api/recommend/sr/srnames
/api/search_reddit_names.json
/api/site_admin
/api/submit_text.json
/api/subreddit_stylesheet
/api/subreddits_by_topic.json
/api/subscribe
/api/upload_sr_img
/r/subreddit/about/edit.json
/subreddits/mine/contributor
/subreddits/mine/moderator
/subreddits/mine/subscriber
/subreddits/mine/where
/subreddits/new
/subreddits/popular
/subreddits/search
/subreddits/where
~~~

####users
~~~
/api/friend
/api/setpermissions
/api/unfriend
/api/v1/me/friends/username
/api/v1/user/username/trophies
/user/username/comments
/user/username/gilded
/user/username/overview
/user/username/where
~~~

####wiki
~~~
/api/wiki/alloweditor/add
/api/wiki/alloweditor/del
/api/wiki/alloweditor/act
/api/wiki/edit
/api/wiki/hide
/api/wiki/revert
/wiki/discussions/page
/wiki/pages
/wiki/revisions
/wiki/revisions/page
/wiki/settings/page
/wiki/page
~~~

#Implemented (36/166)
####account
[`/api/login`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L136)

[`/api/me.json`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L172)

####apps
[`/api/adddeveloper`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L197)

[`/api/removedeveloper`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L206)

####captcha
[`/api/needs_captcha.json`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L202)

[`/api/new_captcha`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L218)

[`/captcha/iden`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L239)

####links & comments
[`/api/vote`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L84)

[`/api/sendreplies`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L135)

[`/api/hide`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L215)

[`/api/unhide`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L215)

[`/api/marknsfw`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L146)

[`/api/unmarknsfw`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L146)

[`/api/del`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L162)

[`/api/save`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L128)

[`/api/unsave`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L128)

[`/api/submit`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L29)

####listings
[`/by_id/names`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/SpecificPaginator.java#L25)

[`/random`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L312)

[`/controversial`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/SimplePaginator.java#L23)

[`/hot`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/SimplePaginator.java#L23)

[`/new`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/SimplePaginator.java#L23)

[`/top`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/SimplePaginator.java#L23)

[`/sort`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/SimplePaginator.java#L23)

####multis
[`/api/multi/multipath`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L290)

[`GET /api/multi/multipath/r/srname`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L290)

[`GET /api/multi/multipath/description`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L305)

[`/api/multi/mine`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/models/LoggedInAccount.java#L222)

####subreddits
[`/r/subreddit/about.json`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L272)

####users
[`/user/username/about.json`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L256)

[`/user/username/disliked`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/UserPaginatorSubmission.java#L30)

[`/user/username/hidden`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/UserPaginatorSubmission.java#L30)

[`/user/username/liked`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/UserPaginatorSubmission.java#L30)

[`/user/username/saved`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/UserPaginatorSubmission.java#L30)

[`/user/username/submitted`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/pagination/UserPaginatorSubmission.java#L30)

[`/api/username_available.json`](https://github.com/thatJavaNerd/JRAW/blob/master/src/main/java/net/dean/jraw/RedditClient.java#L277)

