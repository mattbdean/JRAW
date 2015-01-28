<!--- Generated 2015-01-27 at 22:59:27 EST. Use ./gradlew endpoints:update to update. DO NOT MODIFY DIRECTLY -->
#Endpoints

This file contains a list of all the endpoints (regardless of if they have been implemented) that can be found at the [official Reddit API docs](https://www.reddit.com/dev/api). To update this file, run `./gradlew endpoints:update`.

So far **112** endpoints (out of 187 total) have been implemented.

##(any scope)
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/friend`](https://www.reddit.com/dev/api/oauth#POST_api_friend)|No
`POST`|[`/api/unfriend`](https://www.reddit.com/dev/api/oauth#POST_api_unfriend)|No
`GET`|[`/api/needs_captcha.json`](https://www.reddit.com/dev/api/oauth#GET_api_needs_captcha.json)|[`RedditClient.needsCaptcha()`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#needsCaptcha())
`POST`|[`/api/new_captcha`](https://www.reddit.com/dev/api/oauth#POST_api_new_captcha)|[`RedditClient.getNewCaptcha()`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getNewCaptcha())
`POST`|[`/captcha/{iden}`](https://www.reddit.com/dev/api/oauth#POST_captcha_%7Biden%7D)|[`RedditClient.getCaptcha(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getCaptcha(java.lang.String))

##(not available through oauth)
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/clear_sessions`](https://www.reddit.com/dev/api#POST_api_clear_sessions)|No
`POST`|[`/api/revokeapp`](https://www.reddit.com/dev/api#POST_api_revokeapp)|No
`POST`|[`/api/set_force_https`](https://www.reddit.com/dev/api#POST_api_set_force_https)|No
`POST`|[`/api/setappicon`](https://www.reddit.com/dev/api#POST_api_setappicon)|No
`POST`|[`/api/update`](https://www.reddit.com/dev/api#POST_api_update)|No
`POST`|[`/api/update_email`](https://www.reddit.com/dev/api#POST_api_update_email)|No
`POST`|[`/api/update_password`](https://www.reddit.com/dev/api#POST_api_update_password)|No
`POST`|[`/api/adddeveloper`](https://www.reddit.com/dev/api#POST_api_adddeveloper)|[`AccountManager.addDeveloper(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#addDeveloper(java.lang.String, java.lang.String))
`POST`|[`/api/deleteapp`](https://www.reddit.com/dev/api#POST_api_deleteapp)|[`AccountManager.deleteApp(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#deleteApp(java.lang.String))
`POST`|[`/api/login`](https://www.reddit.com/dev/api#POST_api_login)|[`RedditClient.login(Credentials)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#login(net.dean.jraw.http.Credentials))
`GET`|[`/api/me.json`](https://www.reddit.com/dev/api#GET_api_me.json)|[`RedditClient.me()`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#me())
`POST`|[`/api/register`](https://www.reddit.com/dev/api#POST_api_register)|[`RedditClient.register(String, String, String, Captcha, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#register(java.lang.String, java.lang.String, java.lang.String, net.dean.jraw.models.Captcha, java.lang.String))
`POST`|[`/api/removedeveloper`](https://www.reddit.com/dev/api#POST_api_removedeveloper)|[`AccountManager.removeDeveloper(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#removeDeveloper(java.lang.String, java.lang.String))
`POST`|[`/api/updateapp`](https://www.reddit.com/dev/api#POST_api_updateapp)|[`AccountManager.createOrUpdateApp(String, String, AppType, String, String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#createOrUpdateApp(java.lang.String, java.lang.String, net.dean.jraw.http.oauth.AppType, java.lang.String, java.lang.String, java.lang.String))
`GET`|[`/api/username_available.json`](https://www.reddit.com/dev/api#GET_api_username_available.json)|[`RedditClient.isUsernameAvailable(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#isUsernameAvailable(java.lang.String))

##account
Method|Endpoint|Implemented?
:----:|--------|------------
`PATCH`|[`/api/v1/me/prefs`](https://www.reddit.com/dev/api/oauth#PATCH_api_v1_me_prefs)|[`RedditOAuth2Client.updatePreferences(AccountPreferencesEditor)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#updatePreferences(net.dean.jraw.AccountPreferencesEditor))

##creddits
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/v1/gold/gild/{fullname}`](https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_gild_%7Bfullname%7D)|No
`POST`|[`/api/v1/gold/give/{username}`](https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_give_%7Busername%7D)|No

##edit
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/live/{thread}/delete_update`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_delete_update)|No
`POST`|[`/api/live/{thread}/strike_update`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_strike_update)|No
`POST`|[`/api/del`](https://www.reddit.com/dev/api/oauth#POST_api_del)|[`ModerationManager.delete(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/ModerationManager#delete(java.lang.String))
`POST`|[`/api/editusertext`](https://www.reddit.com/dev/api/oauth#POST_api_editusertext)|[`AccountManager.updateSelfpost(Submission, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#updateSelfpost(net.dean.jraw.models.Submission, java.lang.String))
`POST`|[`/api/sendreplies`](https://www.reddit.com/dev/api/oauth#POST_api_sendreplies)|[`AccountManager.sendRepliesToInbox(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#sendRepliesToInbox(net.dean.jraw.models.Submission, boolean))

##flair
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/flairselector`](https://www.reddit.com/dev/api/oauth#POST_api_flairselector)|[`AccountManager.getFlairChoices(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#getFlairChoices(java.lang.String))
`POST`|[`/api/selectflair`](https://www.reddit.com/dev/api/oauth#POST_api_selectflair)|[`ModerationManager.setFlair(String, FlairTemplate, String, Submission, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/ModerationManager#setFlair(java.lang.String, net.dean.jraw.models.FlairTemplate, java.lang.String, net.dean.jraw.models.Submission, java.lang.String))
`POST`|[`/api/setflairenabled`](https://www.reddit.com/dev/api/oauth#POST_api_setflairenabled)|[`AccountManager.setFlairEnabled(String, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#setFlairEnabled(java.lang.String, boolean))

##history
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/user/{username}/comments`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_comments)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))
`GET`|[`/user/{username}/disliked`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_disliked)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))
`GET`|[`/user/{username}/gilded`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_gilded)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))
`GET`|[`/user/{username}/hidden`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_hidden)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))
`GET`|[`/user/{username}/liked`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_liked)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))
`GET`|[`/user/{username}/overview`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_overview)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))
`GET`|[`/user/{username}/saved`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_saved)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))
`GET`|[`/user/{username}/submitted`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_submitted)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))
`GET`|[`/user/{username}/where`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_where)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserContributionPaginator#getListing(boolean))

##identity
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/api/v1/me`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me)|[`RedditOAuth2Client.me()`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#me())
`GET`|[`/api/v1/me/prefs`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_prefs)|[`RedditOAuth2Client.getPreferences(String[])`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#getPreferences([Ljava.lang.String;))
`GET`|[`/api/v1/me/trophies`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_trophies)|[`RedditOAuth2Client.getTrophies(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#getTrophies(java.lang.String))

##livemanage
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/live/{thread}/accept_contributor_invite`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_accept_contributor_invite)|No
`POST`|[`/api/live/{thread}/close_thread`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_close_thread)|No
`POST`|[`/api/live/{thread}/edit`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_edit)|No
`POST`|[`/api/live/{thread}/invite_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_invite_contributor)|No
`POST`|[`/api/live/{thread}/leave_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_leave_contributor)|No
`POST`|[`/api/live/{thread}/rm_contributor`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_rm_contributor)|No
`POST`|[`/api/live/{thread}/rm_contributor_invite`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_rm_contributor_invite)|No
`POST`|[`/api/live/{thread}/set_contributor_permissions`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_set_contributor_permissions)|No

##modconfig
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/delete_sr_header`](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_header)|No
`POST`|[`/api/delete_sr_img`](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_img)|No
`POST`|[`/api/site_admin`](https://www.reddit.com/dev/api/oauth#POST_api_site_admin)|No
`POST`|[`/api/subreddit_stylesheet`](https://www.reddit.com/dev/api/oauth#POST_api_subreddit_stylesheet)|No
`POST`|[`/api/upload_sr_img`](https://www.reddit.com/dev/api/oauth#POST_api_upload_sr_img)|No
`GET`|[`/r/{subreddit}/about/edit.json`](https://www.reddit.com/dev/api/oauth#GET_r_%7Bsubreddit%7D_about_edit.json)|No
`GET`|[`/stylesheet`](https://www.reddit.com/dev/api/oauth#GET_stylesheet)|[`RedditClient.getStylesheet(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getStylesheet(java.lang.String))

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
`GET`|[`/about/log`](https://www.reddit.com/dev/api/oauth#GET_about_log)|[`ModLogPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ModLogPaginator#getListing(boolean))

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
`POST`|[`/api/marknsfw`](https://www.reddit.com/dev/api/oauth#POST_api_marknsfw)|[`ModerationManager.setNsfw(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/ModerationManager#setNsfw(net.dean.jraw.models.Submission, boolean))
`POST`|[`/api/set_subreddit_sticky`](https://www.reddit.com/dev/api/oauth#POST_api_set_subreddit_sticky)|[`ModerationManager.setSticky(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/ModerationManager#setSticky(net.dean.jraw.models.Submission, boolean))
`POST`|[`/api/unmarknsfw`](https://www.reddit.com/dev/api/oauth#POST_api_unmarknsfw)|[`ModerationManager.setNsfw(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/ModerationManager#setNsfw(net.dean.jraw.models.Submission, boolean))

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
`GET`|[`/wiki/settings/{page}`](https://www.reddit.com/dev/api/oauth#GET_wiki_settings_%7Bpage%7D)|[`WikiManager.getSettings(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/WikiManager#getSettings(java.lang.String, java.lang.String))

##mysubreddits
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends_%7Busername%7D)|[`RedditOAuth2Client.getFriend(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#getFriend(java.lang.String))
`GET`|[`/api/v1/me/karma`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_karma)|[`RedditOAuth2Client.getKarmaBreakdown()`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#getKarmaBreakdown())
`GET`|[`/subreddits/mine/contributor`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_contributor)|[`UserSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserSubredditsPaginator#getListing(boolean))
`GET`|[`/subreddits/mine/moderator`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_moderator)|[`UserSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserSubredditsPaginator#getListing(boolean))
`GET`|[`/subreddits/mine/subscriber`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_subscriber)|[`UserSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserSubredditsPaginator#getListing(boolean))
`GET`|[`/subreddits/mine/{where}`](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_%7Bwhere%7D)|[`UserSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserSubredditsPaginator#getListing(boolean))

##privatemessages
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/block`](https://www.reddit.com/dev/api/oauth#POST_api_block)|No
`POST`|[`/api/read_all_messages`](https://www.reddit.com/dev/api/oauth#POST_api_read_all_messages)|No
`POST`|[`/api/unblock_subreddit`](https://www.reddit.com/dev/api/oauth#POST_api_unblock_subreddit)|No
`POST`|[`/api/compose`](https://www.reddit.com/dev/api/oauth#POST_api_compose)|[`InboxManager.compose(String, String, String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/InboxManager#compose(java.lang.String, java.lang.String, java.lang.String, java.lang.String))
`POST`|[`/api/read_message`](https://www.reddit.com/dev/api/oauth#POST_api_read_message)|[`InboxManager.setRead(PrivateMessage, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/InboxManager#setRead(net.dean.jraw.models.PrivateMessage, boolean))
`POST`|[`/api/unread_message`](https://www.reddit.com/dev/api/oauth#POST_api_unread_message)|[`InboxManager.setRead(PrivateMessage, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/InboxManager#setRead(net.dean.jraw.models.PrivateMessage, boolean))
`GET`|[`/message/inbox`](https://www.reddit.com/dev/api/oauth#GET_message_inbox)|[`InboxPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/InboxPaginator#getListing(boolean))
`GET`|[`/message/sent`](https://www.reddit.com/dev/api/oauth#GET_message_sent)|[`InboxPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/InboxPaginator#getListing(boolean))
`GET`|[`/message/unread`](https://www.reddit.com/dev/api/oauth#GET_message_unread)|[`InboxPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/InboxPaginator#getListing(boolean))
`GET`|[`/message/{where}`](https://www.reddit.com/dev/api/oauth#GET_message_%7Bwhere%7D)|[`InboxPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/InboxPaginator#getListing(boolean))

##read
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api/oauth#GET_api_filter_%7Bfilterpath%7D)|No
`GET`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#GET_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`GET`|[`/api/recommend/sr/{srnames}`](https://www.reddit.com/dev/api/oauth#GET_api_recommend_sr_%7Bsrnames%7D)|No
`GET`|[`/live/{thread}/contributors.json`](https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_contributors.json)|No
`GET`|[`/live/{thread}/discussions`](https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_discussions)|No
`GET`|[`/r/{subreddit}/about.json`](https://www.reddit.com/dev/api/oauth#GET_r_%7Bsubreddit%7D_about.json)|No
`GET`|[`/about/banned`](https://www.reddit.com/dev/api/oauth#GET_about_banned)|[`UserRecordPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserRecordPaginator#getListing(boolean))
`GET`|[`/about/contributors`](https://www.reddit.com/dev/api/oauth#GET_about_contributors)|[`UserRecordPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserRecordPaginator#getListing(boolean))
`GET`|[`/about/edited`](https://www.reddit.com/dev/api/oauth#GET_about_edited)|[`ModeratorPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ModeratorPaginator#getListing(boolean))
`GET`|[`/about/moderators`](https://www.reddit.com/dev/api/oauth#GET_about_moderators)|[`UserRecordPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserRecordPaginator#getListing(boolean))
`GET`|[`/about/modqueue`](https://www.reddit.com/dev/api/oauth#GET_about_modqueue)|[`ModeratorPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ModeratorPaginator#getListing(boolean))
`GET`|[`/about/reports`](https://www.reddit.com/dev/api/oauth#GET_about_reports)|[`ModeratorPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ModeratorPaginator#getListing(boolean))
`GET`|[`/about/spam`](https://www.reddit.com/dev/api/oauth#GET_about_spam)|[`ModeratorPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ModeratorPaginator#getListing(boolean))
`GET`|[`/about/unmoderated`](https://www.reddit.com/dev/api/oauth#GET_about_unmoderated)|[`ModeratorPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ModeratorPaginator#getListing(boolean))
`GET`|[`/about/wikibanned`](https://www.reddit.com/dev/api/oauth#GET_about_wikibanned)|[`UserRecordPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserRecordPaginator#getListing(boolean))
`GET`|[`/about/wikicontributors`](https://www.reddit.com/dev/api/oauth#GET_about_wikicontributors)|[`UserRecordPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserRecordPaginator#getListing(boolean))
`GET`|[`/about/{location}`](https://www.reddit.com/dev/api/oauth#GET_about_%7Blocation%7D)|[`ModeratorPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ModeratorPaginator#getListing(boolean))
`GET`|[`/about/{where}`](https://www.reddit.com/dev/api/oauth#GET_about_%7Bwhere%7D)|[`UserRecordPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/UserRecordPaginator#getListing(boolean))
`GET`|[`/api/info`](https://www.reddit.com/dev/api/oauth#GET_api_info)|[`RedditClient.get(String[])`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#get([Ljava.lang.String;))
`POST`|[`/api/morechildren`](https://www.reddit.com/dev/api/oauth#POST_api_morechildren)|[`RedditClient.getMoreComments(Submission, CommentSort, More)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getMoreComments(net.dean.jraw.models.Submission, net.dean.jraw.models.CommentSort, net.dean.jraw.models.More))
`GET`|[`/api/multi/mine`](https://www.reddit.com/dev/api/oauth#GET_api_multi_mine)|[`MultiRedditManager.mine()`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#mine())
`GET`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#get(java.lang.String, java.lang.String))
`GET`|[`/api/multi/{multipath}/description`](https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D_description)|[`MultiRedditManager.getDescription(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#getDescription(java.lang.String, java.lang.String))
`PUT`|[`/api/multi/{multipath}/description`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D_description)|[`MultiRedditManager.updateDescription(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#updateDescription(java.lang.String, java.lang.String))
`GET`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#get(java.lang.String, java.lang.String))
`POST`|[`/api/search_reddit_names.json`](https://www.reddit.com/dev/api/oauth#POST_api_search_reddit_names.json)|[`RedditClient.searchSubreddits(String, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#searchSubreddits(java.lang.String, boolean))
`GET`|[`/api/subreddits_by_topic.json`](https://www.reddit.com/dev/api/oauth#GET_api_subreddits_by_topic.json)|[`RedditClient.getSubredditsByTopic(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getSubredditsByTopic(java.lang.String))
`GET`|[`/api/v1/me/blocked`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_blocked)|[`ImportantUserPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ImportantUserPaginator#getListing(boolean))
`GET`|[`/api/v1/me/friends`](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends)|[`ImportantUserPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ImportantUserPaginator#getListing(boolean))
`GET`|[`/api/v1/user/{username}/trophies`](https://www.reddit.com/dev/api/oauth#GET_api_v1_user_%7Busername%7D_trophies)|[`RedditOAuth2Client.getTrophies(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#getTrophies(java.lang.String))
`GET`|[`/by_id/names`](https://www.reddit.com/dev/api/oauth#GET_by_id_names)|[`SpecificPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/SpecificPaginator#getListing(boolean))
`GET`|[`/comments/article`](https://www.reddit.com/dev/api/oauth#GET_comments_article)|[`RedditClient.getSubmission(SubmissionRequest)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getSubmission(net.dean.jraw.RedditClient$SubmissionRequest))
`GET`|[`/controversial`](https://www.reddit.com/dev/api/oauth#GET_controversial)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/SubredditPaginator#getListing(boolean))
`GET`|[`/hot`](https://www.reddit.com/dev/api/oauth#GET_hot)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/SubredditPaginator#getListing(boolean))
`GET`|[`/live/{thread}`](https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D)|[`LiveThreadPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/LiveThreadPaginator#getListing(boolean))
`GET`|[`/live/{thread}/about.json`](https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_about.json)|[`RedditClient.getLiveThread(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getLiveThread(java.lang.String))
`GET`|[`/new`](https://www.reddit.com/dev/api/oauth#GET_new)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/SubredditPaginator#getListing(boolean))
`GET`|[`/prefs/blocked`](https://www.reddit.com/dev/api/oauth#GET_prefs_blocked)|[`ImportantUserPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ImportantUserPaginator#getListing(boolean))
`GET`|[`/prefs/friends`](https://www.reddit.com/dev/api/oauth#GET_prefs_friends)|[`ImportantUserPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ImportantUserPaginator#getListing(boolean))
`GET`|[`/prefs/where`](https://www.reddit.com/dev/api/oauth#GET_prefs_where)|[`ImportantUserPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/ImportantUserPaginator#getListing(boolean))
`GET`|[`/random`](https://www.reddit.com/dev/api/oauth#GET_random)|[`RedditClient.getRandomSubmission(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getRandomSubmission(java.lang.String))
`GET`|[`/search`](https://www.reddit.com/dev/api/oauth#GET_search)|[`SubmissionSearchPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/SubmissionSearchPaginator#getListing(boolean))
`GET`|[`/sort`](https://www.reddit.com/dev/api/oauth#GET_sort)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/SubredditPaginator#getListing(boolean))
`GET`|[`/subreddits/new`](https://www.reddit.com/dev/api/oauth#GET_subreddits_new)|[`AllSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/AllSubredditsPaginator#getListing(boolean))
`GET`|[`/subreddits/popular`](https://www.reddit.com/dev/api/oauth#GET_subreddits_popular)|[`AllSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/AllSubredditsPaginator#getListing(boolean))
`GET`|[`/subreddits/search`](https://www.reddit.com/dev/api/oauth#GET_subreddits_search)|[`SubredditSearchPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/SubredditSearchPaginator#getListing(boolean))
`GET`|[`/subreddits/where`](https://www.reddit.com/dev/api/oauth#GET_subreddits_where)|[`AllSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/AllSubredditsPaginator#getListing(boolean))
`GET`|[`/top`](https://www.reddit.com/dev/api/oauth#GET_top)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/paginators/SubredditPaginator#getListing(boolean))
`GET`|[`/user/{username}/about.json`](https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_about.json)|[`RedditClient.getUser(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getUser(java.lang.String))

##report
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/live/{thread}/report`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_report)|No
`POST`|[`/api/report`](https://www.reddit.com/dev/api/oauth#POST_api_report)|No
`POST`|[`/api/hide`](https://www.reddit.com/dev/api/oauth#POST_api_hide)|[`AccountManager.hide(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#hide(net.dean.jraw.models.Submission, boolean))
`POST`|[`/api/unhide`](https://www.reddit.com/dev/api/oauth#POST_api_unhide)|[`AccountManager.hide(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#hide(net.dean.jraw.models.Submission, boolean))

##save
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/api/saved_categories.json`](https://www.reddit.com/dev/api/oauth#GET_api_saved_categories.json)|No
`POST`|[`/api/store_visits`](https://www.reddit.com/dev/api/oauth#POST_api_store_visits)|No
`POST`|[`/api/save`](https://www.reddit.com/dev/api/oauth#POST_api_save)|[`AccountManager.setSaved(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#setSaved(net.dean.jraw.models.Submission, boolean))
`POST`|[`/api/unsave`](https://www.reddit.com/dev/api/oauth#POST_api_unsave)|[`AccountManager.setSaved(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#setSaved(net.dean.jraw.models.Submission, boolean))

##submit
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/live/create`](https://www.reddit.com/dev/api/oauth#POST_api_live_create)|No
`POST`|[`/api/live/{thread}/update`](https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_update)|No
`POST`|[`/api/comment`](https://www.reddit.com/dev/api/oauth#POST_api_comment)|[`AccountManager.reply(Contribution, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#reply(net.dean.jraw.models.Contribution, java.lang.String))
`POST`|[`/api/submit`](https://www.reddit.com/dev/api/oauth#POST_api_submit)|[`AccountManager.submit(SubmissionBuilder, Captcha, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#submit(net.dean.jraw.managers.AccountManager$SubmissionBuilder, net.dean.jraw.models.Captcha, java.lang.String))
`GET`|[`/api/submit_text.json`](https://www.reddit.com/dev/api/oauth#GET_api_submit_text.json)|[`RedditClient.getSubmitText(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditClient#getSubmitText(java.lang.String))

##subscribe
Method|Endpoint|Implemented?
:----:|--------|------------
`DELETE`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api/oauth#DELETE_api_filter_%7Bfilterpath%7D)|No
`PUT`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api/oauth#PUT_api_filter_%7Bfilterpath%7D)|No
`DELETE`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#DELETE_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`PUT`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#PUT_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`POST`|[`/api/multi/{filterpath}`](https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bfilterpath%7D)|No
`DELETE`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#DELETE_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.delete(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#delete(java.lang.String))
`POST`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.createOrUpdate(String, List, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#createOrUpdate(java.lang.String, java.util.List, boolean))
`PUT`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.createOrUpdate(String, List, boolean)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#createOrUpdate(java.lang.String, java.util.List, boolean))
`POST`|[`/api/multi/{multipath}/copy`](https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D_copy)|[`MultiRedditManager.copy(String, String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#copy(java.lang.String, java.lang.String, java.lang.String))
`DELETE`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#DELETE_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.removeSubreddit(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#removeSubreddit(java.lang.String, java.lang.String))
`PUT`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.addSubreddit(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#addSubreddit(java.lang.String, java.lang.String))
`POST`|[`/api/multi/{multipath}/rename`](https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D_rename)|[`MultiRedditManager.rename(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/MultiRedditManager#rename(java.lang.String, java.lang.String))
`POST`|[`/api/subscribe`](https://www.reddit.com/dev/api/oauth#POST_api_subscribe)|[`AccountManager.subscribe(Subreddit)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#subscribe(net.dean.jraw.models.Subreddit))
`DELETE`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#DELETE_api_v1_me_friends_%7Busername%7D)|[`RedditOAuth2Client.deleteFriend(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#deleteFriend(java.lang.String))
`PUT`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api/oauth#PUT_api_v1_me_friends_%7Busername%7D)|[`RedditOAuth2Client.updateFriend(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/RedditOAuth2Client#updateFriend(java.lang.String))

##vote
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/vote`](https://www.reddit.com/dev/api/oauth#POST_api_vote)|[`AccountManager.vote(Thing, VoteDirection)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/AccountManager#vote(net.dean.jraw.models.Thing, net.dean.jraw.models.VoteDirection))

##wikiedit
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/wiki/edit`](https://www.reddit.com/dev/api/oauth#POST_api_wiki_edit)|No

##wikiread
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/wiki/discussions/page`](https://www.reddit.com/dev/api/oauth#GET_wiki_discussions_page)|No
`GET`|[`/wiki/revisions`](https://www.reddit.com/dev/api/oauth#GET_wiki_revisions)|No
`GET`|[`/wiki/revisions/page`](https://www.reddit.com/dev/api/oauth#GET_wiki_revisions_page)|No
`GET`|[`/wiki/page`](https://www.reddit.com/dev/api/oauth#GET_wiki_page)|[`WikiManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/WikiManager#get(java.lang.String, java.lang.String))
`GET`|[`/wiki/pages`](https://www.reddit.com/dev/api/oauth#GET_wiki_pages)|[`WikiManager.getPages(String)`](https://thatjavanerd.github.io/JRAW/docs/git/latest/net/dean/jraw/managers/WikiManager#getPages(java.lang.String))
