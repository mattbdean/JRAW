<!--- Generated 2016-01-09 at 21:54:03 EST. Use `gradle endpoints:update` to update. DO NOT MODIFY DIRECTLY -->
#Endpoints

This file contains a list of all the endpoints (regardless of if they have been implemented) that can be found at the [official reddit API docs](https://www.reddit.com/dev/api). To update this file, run `gradle endpoints:update`.

So far **122** endpoints (out of 180 total) have been implemented.

##(any scope)
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/friend`](https://www.reddit.com/dev/api/oauth#POST_api_friend)|No
`POST`|[`/api/unfriend`](https://www.reddit.com/dev/api/oauth#POST_api_unfriend)|No
`GET`|[`/api/needs_captcha.json`](https://www.reddit.com/dev/api/oauth#GET_api_needs_captcha.json)|[`RedditClient.needsCaptcha()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#needsCaptcha())
`POST`|[`/api/new_captcha`](https://www.reddit.com/dev/api/oauth#POST_api_new_captcha)|[`RedditClient.getNewCaptcha()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getNewCaptcha())
`GET`|[`/captcha/{iden}`](https://www.reddit.com/dev/api/oauth#GET_captcha_%7Biden%7D)|[`RedditClient.getCaptcha(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getCaptcha(java.lang.String))

##account
Method|Endpoint|Implemented?
:----:|--------|------------
`PATCH`|[`/api/v1/me/prefs`](https://www.reddit.com/dev/api/oauth#PATCH_api_v1_me_prefs)|[`AccountManager.updatePreferences(AccountPreferencesEditor)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#updatePreferences(net.dean.jraw.AccountPreferencesEditor))

##creddits
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/v1/gold/gild/{fullname}`](https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_gild_%7Bfullname%7D)|[`AccountManager.giveGold(PublicContribution)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#giveGold(net.dean.jraw.models.PublicContribution))
`POST`|[`/api/v1/gold/give/{username}`](https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_give_%7Busername%7D)|[`AccountManager.giveGold(String, int)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#giveGold(java.lang.String, int))

##edit
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/del`](https://www.reddit.com/dev/api/oauth#POST_api_del)|[`ModerationManager.delete(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/ModerationManager.html#delete(java.lang.String))
`POST`|[`/api/editusertext`](https://www.reddit.com/dev/api/oauth#POST_api_editusertext)|[`AccountManager.updateContribution(PublicContribution, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#updateContribution(net.dean.jraw.models.PublicContribution, java.lang.String))
`POST`|[`/api/live/{thread}/delete_update`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_delete_update)|[`LiveThreadManager.deleteUpdate(LiveThread, LiveUpdate)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/LiveThreadManager.html#deleteUpdate(net.dean.jraw.models.LiveThread, net.dean.jraw.models.LiveUpdate))
`POST`|[`/api/live/{thread}/strike_update`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_strike_update)|[`LiveThreadManager.strikeUpdate(LiveThread, LiveUpdate)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/LiveThreadManager.html#strikeUpdate(net.dean.jraw.models.LiveThread, net.dean.jraw.models.LiveUpdate))
`POST`|[`/api/sendreplies`](https://www.reddit.com/dev/api/oauth#POST_api_sendreplies)|[`AccountManager.sendRepliesToInbox(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#sendRepliesToInbox(net.dean.jraw.models.Submission, boolean))

##flair
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/flairselector`](https://www.reddit.com/dev/api/oauth#POST_api_flairselector)|[`AccountManager.getFlairChoices(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#getFlairChoices(java.lang.String))
`POST`|[`/api/selectflair`](https://www.reddit.com/dev/api/oauth#POST_api_selectflair)|[`ModerationManager.setFlair(String, FlairTemplate, String, Submission, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/ModerationManager.html#setFlair(java.lang.String, net.dean.jraw.models.FlairTemplate, java.lang.String, net.dean.jraw.models.Submission, java.lang.String))
`POST`|[`/api/setflairenabled`](https://www.reddit.com/dev/api/oauth#POST_api_setflairenabled)|[`AccountManager.setFlairEnabled(String, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#setFlairEnabled(java.lang.String, boolean))

##history
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/user/{username}/comments`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_comments)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))
`GET`|[`/user/{username}/downvoted`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_downvoted)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))
`GET`|[`/user/{username}/gilded`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_gilded)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))
`GET`|[`/user/{username}/hidden`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_hidden)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))
`GET`|[`/user/{username}/overview`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_overview)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))
`GET`|[`/user/{username}/saved`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_saved)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))
`GET`|[`/user/{username}/submitted`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_submitted)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))
`GET`|[`/user/{username}/upvoted`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_upvoted)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))
`GET`|[`/user/{username}/{where}`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_%7Bwhere%7D)|[`UserContributionPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserContributionPaginator.html#next(boolean))

##identity
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/api/v1/me`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me)|[`RedditClient.me()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#me())
`GET`|[`/api/v1/me/prefs`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_prefs)|[`AccountManager.getPreferences(String[])`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#getPreferences([Ljava.lang.String;))
`GET`|[`/api/v1/me/trophies`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_trophies)|[`RedditClient.getTrophies(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getTrophies(java.lang.String))

##livemanage
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/live/{thread}/accept_contributor_invite`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_accept_contributor_invite)|No
`POST`|[`/api/live/{thread}/close_thread`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_close_thread)|No
`POST`|[`/api/live/{thread}/invite_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_invite_contributor)|No
`POST`|[`/api/live/{thread}/leave_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_leave_contributor)|No
`POST`|[`/api/live/{thread}/rm_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_rm_contributor)|No
`POST`|[`/api/live/{thread}/rm_contributor_invite`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_rm_contributor_invite)|No
`POST`|[`/api/live/{thread}/set_contributor_permissions`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_set_contributor_permissions)|No
`POST`|[`/api/live/{thread}/edit`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_edit)|[`LiveThreadManager.edit(LiveThread, String, boolean, String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/LiveThreadManager.html#edit(net.dean.jraw.models.LiveThread, java.lang.String, boolean, java.lang.String, java.lang.String))

##modconfig
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/delete_sr_header`](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_header)|No
`POST`|[`/api/delete_sr_img`](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_img)|No
`POST`|[`/api/site_admin`](https://www.reddit.com/dev/api/oauth#POST_api_site_admin)|No
`POST`|[`/api/subreddit_stylesheet`](https://www.reddit.com/dev/api/oauth#POST_api_subreddit_stylesheet)|No
`POST`|[`/api/upload_sr_img`](https://www.reddit.com/dev/api/oauth#POST_api_upload_sr_img)|No
`GET`|[`/r/subreddit/about/edit.json`](https://www.reddit.com/dev/api/oauth#GET_r_subreddit_about_edit.json)|No
`GET`|[`/stylesheet`](https://www.reddit.com/dev/api/oauth#GET_stylesheet)|[`RedditClient.getStylesheet(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getStylesheet(java.lang.String))

##modflair
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/clearflairtemplates`](https://www.reddit.com/dev/api/oauth#POST_api_clearflairtemplates)|No
`POST`|[`/api/deleteflair`](https://www.reddit.com/dev/api/oauth#POST_api_deleteflair)|No
`POST`|[`/api/deleteflairtemplate`](https://www.reddit.com/dev/api/oauth#POST_api_deleteflairtemplate)|No
`POST`|[`/api/flair`](https://www.reddit.com/dev/api/oauth#POST_api_flair)|No
`POST`|[`/api/flairconfig`](https://www.reddit.com/dev/api/oauth#POST_api_flairconfig)|No
`POST`|[`/api/flaircsv`](https://www.reddit.com/dev/api/oauth#POST_api_flaircsv)|No
`GET`|[`/api/flairlist`](https://www.reddit.com/dev/api/oauth#GET_api_flairlist)|No
`POST`|[`/api/flairtemplate`](https://www.reddit.com/dev/api/oauth#POST_api_flairtemplate)|No

##modlog
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/about/log`](https://www.reddit.com/dev/api/oauth#GET_about_log)|[`ModLogPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ModLogPaginator.html#next(boolean))

##modothers
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/setpermissions`](https://www.reddit.com/dev/api/oauth#POST_api_setpermissions)|No

##modposts
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/approve`](https://www.reddit.com/dev/api/oauth#POST_api_approve)|No
`POST`|[`/api/distinguish`](https://www.reddit.com/dev/api/oauth#POST_api_distinguish)|No
`POST`|[`/api/ignore_reports`](https://www.reddit.com/dev/api/oauth#POST_api_ignore_reports)|No
`POST`|[`/api/remove`](https://www.reddit.com/dev/api/oauth#POST_api_remove)|No
`POST`|[`/api/set_contest_mode`](https://www.reddit.com/dev/api/oauth#POST_api_set_contest_mode)|No
`POST`|[`/api/unignore_reports`](https://www.reddit.com/dev/api/oauth#POST_api_unignore_reports)|No
`POST`|[`/api/marknsfw`](https://www.reddit.com/dev/api/oauth#POST_api_marknsfw)|[`ModerationManager.setNsfw(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/ModerationManager.html#setNsfw(net.dean.jraw.models.Submission, boolean))
`POST`|[`/api/set_subreddit_sticky`](https://www.reddit.com/dev/api/oauth#POST_api_set_subreddit_sticky)|[`ModerationManager.setSticky(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/ModerationManager.html#setSticky(net.dean.jraw.models.Submission, boolean))
`POST`|[`/api/unmarknsfw`](https://www.reddit.com/dev/api/oauth#POST_api_unmarknsfw)|[`ModerationManager.setNsfw(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/ModerationManager.html#setNsfw(net.dean.jraw.models.Submission, boolean))

##modself
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/accept_moderator_invite`](https://www.reddit.com/dev/api/oauth#POST_api_accept_moderator_invite)|No
`POST`|[`/api/leavecontributor`](https://www.reddit.com/dev/api/oauth#POST_api_leavecontributor)|No
`POST`|[`/api/leavemoderator`](https://www.reddit.com/dev/api/oauth#POST_api_leavemoderator)|No

##modwiki
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/wiki/alloweditor/add`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_add)|No
`POST`|[`/api/wiki/alloweditor/del`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_del)|No
`POST`|[`/api/wiki/alloweditor/{act}`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_%7Bact%7D)|No
`POST`|[`/api/wiki/hide`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_hide)|No
`POST`|[`/api/wiki/revert`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_revert)|No
`POST`|[`/wiki/settings/{page}`](https://www.reddit.com/dev/api/oauth#POST_wiki_settings_%7Bpage%7D)|No
`GET`|[`/wiki/settings/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_settings_%7Bpage%7D)|[`WikiManager.getSettings(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/WikiManager.html#getSettings(java.lang.String, java.lang.String))

##mysubreddits
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends_%7Busername%7D)|[`AccountManager.getFriend(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#getFriend(java.lang.String))
`GET`|[`/api/v1/me/karma`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_karma)|[`AccountManager.getKarmaBreakdown()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#getKarmaBreakdown())
`GET`|[`/subreddits/mine/contributor`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_contributor)|[`UserSubredditsPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserSubredditsPaginator.html#next(boolean))
`GET`|[`/subreddits/mine/moderator`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_moderator)|[`UserSubredditsPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserSubredditsPaginator.html#next(boolean))
`GET`|[`/subreddits/mine/subscriber`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_subscriber)|[`UserSubredditsPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserSubredditsPaginator.html#next(boolean))
`GET`|[`/subreddits/mine/{where}`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_%7Bwhere%7D)|[`UserSubredditsPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserSubredditsPaginator.html#next(boolean))

##privatemessages
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/block`](https://www.reddit.com/dev/api/oauth#POST_api_block)|No
`POST`|[`/api/unblock_subreddit`](https://www.reddit.com/dev/api/oauth#POST_api_unblock_subreddit)|No
`POST`|[`/api/compose`](https://www.reddit.com/dev/api/oauth#POST_api_compose)|[`InboxManager.compose(String, String, String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/InboxManager.html#compose(java.lang.String, java.lang.String, java.lang.String, java.lang.String))
`POST`|[`/api/read_all_messages`](https://www.reddit.com/dev/api/oauth#POST_api_read_all_messages)|[`InboxManager.setAllRead()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/InboxManager.html#setAllRead())
`POST`|[`/api/read_message`](https://www.reddit.com/dev/api/oauth#POST_api_read_message)|[`InboxManager.setRead(Message, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/InboxManager.html#setRead(net.dean.jraw.models.Message, boolean))
`POST`|[`/api/unread_message`](https://www.reddit.com/dev/api/oauth#POST_api_unread_message)|[`InboxManager.setRead(Message, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/InboxManager.html#setRead(net.dean.jraw.models.Message, boolean))
`GET`|[`/message/inbox`](https://www.reddit.com/dev/api/oauth#GET_message_inbox)|[`InboxPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/InboxPaginator.html#next(boolean))
`GET`|[`/message/sent`](https://www.reddit.com/dev/api/oauth#GET_message_sent)|[`InboxPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/InboxPaginator.html#next(boolean))
`GET`|[`/message/unread`](https://www.reddit.com/dev/api/oauth#GET_message_unread)|[`InboxPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/InboxPaginator.html#next(boolean))
`GET`|[`/message/{where}`](https://www.reddit.com/dev/api/oauth#GET_message_%7Bwhere%7D)|[`InboxPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/InboxPaginator.html#next(boolean))

##read
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api/oauth#GET_api_filter_%7Bfilterpath%7D)|No
`GET`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#GET_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`GET`|[`/sidebar`](https://www.reddit.com/dev/api/oauth#GET_sidebar)|No
`GET`|[`/sticky`](https://www.reddit.com/dev/api/oauth#GET_sticky)|No
`GET`|[`/about/banned`](https://www.reddit.com/dev/api/oauth#GET_about_banned)|[`UserRecordPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserRecordPaginator.html#next(boolean))
`GET`|[`/about/contributors`](https://www.reddit.com/dev/api/oauth#GET_about_contributors)|[`UserRecordPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserRecordPaginator.html#next(boolean))
`GET`|[`/about/edited`](https://www.reddit.com/dev/api/oauth#GET_about_edited)|[`ModeratorPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ModeratorPaginator.html#next(boolean))
`GET`|[`/about/location`](https://www.reddit.com/dev/api/oauth#GET_about_location)|[`ModeratorPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ModeratorPaginator.html#next(boolean))
`GET`|[`/about/moderators`](https://www.reddit.com/dev/api/oauth#GET_about_moderators)|[`UserRecordPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserRecordPaginator.html#next(boolean))
`GET`|[`/about/modqueue`](https://www.reddit.com/dev/api/oauth#GET_about_modqueue)|[`ModeratorPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ModeratorPaginator.html#next(boolean))
`GET`|[`/about/reports`](https://www.reddit.com/dev/api/oauth#GET_about_reports)|[`ModeratorPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ModeratorPaginator.html#next(boolean))
`GET`|[`/about/spam`](https://www.reddit.com/dev/api/oauth#GET_about_spam)|[`ModeratorPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ModeratorPaginator.html#next(boolean))
`GET`|[`/about/unmoderated`](https://www.reddit.com/dev/api/oauth#GET_about_unmoderated)|[`ModeratorPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ModeratorPaginator.html#next(boolean))
`GET`|[`/about/wikibanned`](https://www.reddit.com/dev/api/oauth#GET_about_wikibanned)|[`UserRecordPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserRecordPaginator.html#next(boolean))
`GET`|[`/about/wikicontributors`](https://www.reddit.com/dev/api/oauth#GET_about_wikicontributors)|[`UserRecordPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserRecordPaginator.html#next(boolean))
`GET`|[`/about/{where}`](https://www.reddit.com/dev/api/oauth#GET_about_%7Bwhere%7D)|[`UserRecordPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/UserRecordPaginator.html#next(boolean))
`GET`|[`/api/info`](https://www.reddit.com/dev/api/oauth#GET_api_info)|[`RedditClient.get(String[])`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#get([Ljava.lang.String;))
`GET`|[`/api/morechildren`](https://www.reddit.com/dev/api/oauth#GET_api_morechildren)|[`CommentNode.getMoreComments(RedditClient)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/models/CommentNode.html#getMoreComments(net.dean.jraw.RedditClient))
`GET`|[`/api/multi/mine`](https://www.reddit.com/dev/api/oauth#GET_api_multi_mine)|[`MultiRedditManager.mine()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#mine())
`GET`|[`/api/multi/user/{username}`](https://www.reddit.com/dev/api/oauth#GET_api_multi_user_%7Busername%7D)|[`MultiRedditManager.getPublicMultis(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#getPublicMultis(java.lang.String))
`GET`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#get(java.lang.String, java.lang.String))
`GET`|[`/api/multi/{multipath}/description`](https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D_description)|[`MultiRedditManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#get(java.lang.String, java.lang.String))
`PUT`|[`/api/multi/{multipath}/description`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D_description)|[`MultiRedditManager.updateDescription(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#updateDescription(java.lang.String, java.lang.String))
`GET`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#get(java.lang.String, java.lang.String))
`GET`|[`/api/recommend/sr/{srnames}`](https://www.reddit.com/dev/api/oauth#GET_api_recommend_sr_%7Bsrnames%7D)|[`RedditClient.getRecommendations(List, List)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getRecommendations(java.util.List, java.util.List))
`POST`|[`/api/search_reddit_names.json`](https://www.reddit.com/dev/api/oauth#POST_api_search_reddit_names.json)|[`RedditClient.searchSubreddits(String, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#searchSubreddits(java.lang.String, boolean))
`GET`|[`/api/subreddits_by_topic.json`](https://www.reddit.com/dev/api/oauth#GET_api_subreddits_by_topic.json)|[`RedditClient.getSubredditsByTopic(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getSubredditsByTopic(java.lang.String))
`GET`|[`/api/v1/me/blocked`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_blocked)|[`ImportantUserPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ImportantUserPaginator.html#next(boolean))
`GET`|[`/api/v1/me/friends`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends)|[`ImportantUserPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ImportantUserPaginator.html#next(boolean))
`GET`|[`/api/v1/user/{username}/trophies`](https://www.reddit.com/dev/api/oauth#GET_api_v1_user_%7Busername%7D_trophies)|[`RedditClient.getTrophies(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getTrophies(java.lang.String))
`GET`|[`/by_id/{names}`](https://www.reddit.com/dev/api/oauth#GET_by_id_%7Bnames%7D)|[`SpecificPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SpecificPaginator.html#next(boolean))
`GET`|[`/comments/{article}`](https://www.reddit.com/dev/api/oauth#GET_comments_%7Barticle%7D)|[`RedditClient.getSubmission(SubmissionRequest)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getSubmission(net.dean.jraw.http.SubmissionRequest))
`GET`|[`/controversial`](https://www.reddit.com/dev/api/oauth#GET_controversial)|[`SubredditPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditPaginator.html#next(boolean))
`GET`|[`/duplicates/{article}`](https://www.reddit.com/dev/api/oauth#GET_duplicates_%7Barticle%7D)|[`DuplicatesPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/DuplicatesPaginator.html#next(boolean))
`GET`|[`/hot`](https://www.reddit.com/dev/api/oauth#GET_hot)|[`SubredditPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditPaginator.html#next(boolean))
`GET`|[`/live/{thread}`](https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D)|[`LiveThreadPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/LiveThreadPaginator.html#next(boolean))
`GET`|[`/live/{thread}/about.json`](https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_about.json)|[`LiveThreadManager.get(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/LiveThreadManager.html#get(java.lang.String))
`GET`|[`/live/{thread}/contributors`](https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_contributors)|[`LiveThreadManager.getContributors(LiveThread)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/LiveThreadManager.html#getContributors(net.dean.jraw.models.LiveThread))
`GET`|[`/live/{thread}/discussions`](https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_discussions)|[`LiveThreadManager.getDiscussions(LiveThread)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/LiveThreadManager.html#getDiscussions(net.dean.jraw.models.LiveThread))
`GET`|[`/new`](https://www.reddit.com/dev/api/oauth#GET_new)|[`SubredditPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditPaginator.html#next(boolean))
`GET`|[`/prefs/blocked`](https://www.reddit.com/dev/api/oauth#GET_prefs_blocked)|[`ImportantUserPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ImportantUserPaginator.html#next(boolean))
`GET`|[`/prefs/friends`](https://www.reddit.com/dev/api/oauth#GET_prefs_friends)|[`ImportantUserPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ImportantUserPaginator.html#next(boolean))
`GET`|[`/prefs/{where}`](https://www.reddit.com/dev/api/oauth#GET_prefs_%7Bwhere%7D)|[`ImportantUserPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/ImportantUserPaginator.html#next(boolean))
`GET`|[`/r/{subreddit}/about.json`](https://www.reddit.com/dev/api/oauth#GET_r_%7Bsubreddit%7D_about.json)|[`RedditClient.getSubreddit(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getSubreddit(java.lang.String))
`GET`|[`/random`](https://www.reddit.com/dev/api/oauth#GET_random)|[`RedditClient.getRandomSubmission(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getRandomSubmission(java.lang.String))
`GET`|[`/related/{article}`](https://www.reddit.com/dev/api/oauth#GET_related_%7Barticle%7D)|[`RelatedPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/RelatedPaginator.html#next(boolean))
`GET`|[`/search`](https://www.reddit.com/dev/api/oauth#GET_search)|[`SubmissionSearchPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubmissionSearchPaginator.html#next(boolean))
`GET`|[`/sort`](https://www.reddit.com/dev/api/oauth#GET_sort)|[`SubredditPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditPaginator.html#next(boolean))
`GET`|[`/subreddits/default`](https://www.reddit.com/dev/api/oauth#GET_subreddits_default)|[`SubredditStream.next()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditStream.html#next())
`GET`|[`/subreddits/employee`](https://www.reddit.com/dev/api/oauth#GET_subreddits_employee)|[`SubredditStream.next()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditStream.html#next())
`GET`|[`/subreddits/gold`](https://www.reddit.com/dev/api/oauth#GET_subreddits_gold)|[`SubredditStream.next()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditStream.html#next())
`GET`|[`/subreddits/new`](https://www.reddit.com/dev/api/oauth#GET_subreddits_new)|[`SubredditStream.next()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditStream.html#next())
`GET`|[`/subreddits/popular`](https://www.reddit.com/dev/api/oauth#GET_subreddits_popular)|[`SubredditStream.next()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditStream.html#next())
`GET`|[`/subreddits/search`](https://www.reddit.com/dev/api/oauth#GET_subreddits_search)|[`SubredditSearchPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditSearchPaginator.html#next(boolean))
`GET`|[`/subreddits/{where}`](https://www.reddit.com/dev/api/oauth#GET_subreddits_%7Bwhere%7D)|[`SubredditStream.next()`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditStream.html#next())
`GET`|[`/top`](https://www.reddit.com/dev/api/oauth#GET_top)|[`SubredditPaginator.next(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/paginators/SubredditPaginator.html#next(boolean))
`GET`|[`/user/{username}/about.json`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_about.json)|[`RedditClient.getUser(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getUser(java.lang.String))

##report
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/live/{thread}/report`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_report)|No
`POST`|[`/api/report`](https://www.reddit.com/dev/api/oauth#POST_api_report)|No
`POST`|[`/api/hide`](https://www.reddit.com/dev/api/oauth#POST_api_hide)|[`AccountManager.hide(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#hide(net.dean.jraw.models.Submission, boolean))
`POST`|[`/api/unhide`](https://www.reddit.com/dev/api/oauth#POST_api_unhide)|[`AccountManager.hide(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#hide(net.dean.jraw.models.Submission, boolean))

##save
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/saved_categories.json`](https://www.reddit.com/dev/api/oauth#POST_api_saved_categories.json)|No
`POST`|[`/api/store_visits`](https://www.reddit.com/dev/api/oauth#POST_api_store_visits)|No
`POST`|[`/api/save`](https://www.reddit.com/dev/api/oauth#POST_api_save)|[`AccountManager.setSaved(PublicContribution, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#setSaved(net.dean.jraw.models.PublicContribution, boolean))
`POST`|[`/api/unsave`](https://www.reddit.com/dev/api/oauth#POST_api_unsave)|[`AccountManager.setSaved(PublicContribution, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#setSaved(net.dean.jraw.models.PublicContribution, boolean))

##submit
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/comment`](https://www.reddit.com/dev/api/oauth#POST_api_comment)|[`AccountManager.reply(Contribution, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#reply(net.dean.jraw.models.Contribution, java.lang.String))
`POST`|[`/api/live/create`](https://www.reddit.com/dev/api/oauth#POST_api_live_create)|[`LiveThreadManager.create(String, boolean, String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/LiveThreadManager.html#create(java.lang.String, boolean, java.lang.String, java.lang.String))
`POST`|[`/api/live/{thread}/update`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_update)|[`LiveThreadManager.postUpdate(LiveThread, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/LiveThreadManager.html#postUpdate(net.dean.jraw.models.LiveThread, java.lang.String))
`POST`|[`/api/submit`](https://www.reddit.com/dev/api/oauth#POST_api_submit)|[`AccountManager.submit(SubmissionBuilder, Captcha, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#submit(net.dean.jraw.managers.AccountManager$SubmissionBuilder, net.dean.jraw.models.Captcha, java.lang.String))
`GET`|[`/api/submit_text.json`](https://www.reddit.com/dev/api/oauth#GET_api_submit_text.json)|[`RedditClient.getSubmitText(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/RedditClient.html#getSubmitText(java.lang.String))

##subscribe
Method|Endpoint|Implemented?
:----:|--------|------------
`DELETE`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api/oauth#DELETE_api_filter_%7Bfilterpath%7D)|No
`POST`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api/oauth#POST_api_filter_%7Bfilterpath%7D)|No
`PUT`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api/oauth#PUT_api_filter_%7Bfilterpath%7D)|No
`DELETE`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#DELETE_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`PUT`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#PUT_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`DELETE`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#DELETE_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.delete(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#delete(java.lang.String))
`POST`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.createOrUpdate(MultiRedditUpdateRequest)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#createOrUpdate(net.dean.jraw.http.MultiRedditUpdateRequest))
`PUT`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.createOrUpdate(MultiRedditUpdateRequest)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#createOrUpdate(net.dean.jraw.http.MultiRedditUpdateRequest))
`POST`|[`/api/multi/{multipath}/copy`](https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D_copy)|[`MultiRedditManager.copy(String, String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#copy(java.lang.String, java.lang.String, java.lang.String))
`DELETE`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#DELETE_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.removeSubreddit(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#removeSubreddit(java.lang.String, java.lang.String))
`PUT`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.addSubreddit(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#addSubreddit(java.lang.String, java.lang.String))
`POST`|[`/api/multi/{multipath}/rename`](https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D_rename)|[`MultiRedditManager.rename(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/MultiRedditManager.html#rename(java.lang.String, java.lang.String))
`POST`|[`/api/subscribe`](https://www.reddit.com/dev/api/oauth#POST_api_subscribe)|[`AccountManager.subscribe(Subreddit)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#subscribe(net.dean.jraw.models.Subreddit))
`DELETE`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#DELETE_api_v1_me_friends_%7Busername%7D)|[`AccountManager.deleteFriend(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#deleteFriend(java.lang.String))
`PUT`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#PUT_api_v1_me_friends_%7Busername%7D)|[`AccountManager.updateFriend(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#updateFriend(java.lang.String))

##vote
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/vote`](https://www.reddit.com/dev/api/oauth#POST_api_vote)|[`AccountManager.vote(Thing, VoteDirection)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/AccountManager.html#vote(net.dean.jraw.models.Thing, net.dean.jraw.models.VoteDirection))

##wikiedit
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/wiki/edit`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_edit)|No

##wikiread
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/wiki/discussions/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_discussions_%7Bpage%7D)|No
`GET`|[`/wiki/revisions`](https://www.reddit.com/dev/api/oauth#GET_wiki_revisions)|No
`GET`|[`/wiki/revisions/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_revisions_%7Bpage%7D)|No
`GET`|[`/wiki/pages`](https://www.reddit.com/dev/api/oauth#GET_wiki_pages)|[`WikiManager.getPages(String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/WikiManager.html#getPages(java.lang.String))
`GET`|[`/wiki/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_%7Bpage%7D)|[`WikiManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/net/dean/jraw/managers/WikiManager.html#get(java.lang.String, java.lang.String))
