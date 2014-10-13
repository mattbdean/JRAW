<!--- Generated 2014-10-13 at 10:18:44 EDT. Use ./gradlew endpoints:update to update. DO NOT MODIFY DIRECTLY -->
#Endpoints

This file contains a list of all the endpoints (regardless of if they have been implemented) that can be found at the [official Reddit API docs](https://www.reddit.com/dev/api). To update this file, run `./gradlew endpoints:update`.

So far **69** endpoints (out of 184 total) have been implemented.

##account
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/clear_sessions`](https://www.reddit.com/dev/api#POST_api_clear_sessions)|No
`POST`|[`/api/delete_user`](https://www.reddit.com/dev/api#POST_api_delete_user)|No
`POST`|[`/api/register`](https://www.reddit.com/dev/api#POST_api_register)|No
`POST`|[`/api/set_force_https`](https://www.reddit.com/dev/api#POST_api_set_force_https)|No
`POST`|[`/api/update`](https://www.reddit.com/dev/api#POST_api_update)|No
`POST`|[`/api/update_email`](https://www.reddit.com/dev/api#POST_api_update_email)|No
`POST`|[`/api/update_password`](https://www.reddit.com/dev/api#POST_api_update_password)|No
`GET`|[`/api/v1/me`](https://www.reddit.com/dev/api#GET_api_v1_me)|No
`GET`|[`/api/v1/me/blocked`](https://www.reddit.com/dev/api#GET_api_v1_me_blocked)|No
`GET`|[`/api/v1/me/friends`](https://www.reddit.com/dev/api#GET_api_v1_me_friends)|No
`GET`|[`/api/v1/me/karma`](https://www.reddit.com/dev/api#GET_api_v1_me_karma)|No
`PATCH`|[`/api/v1/me/prefs`](https://www.reddit.com/dev/api#PATCH_api_v1_me_prefs)|No
`GET`|[`/api/v1/me/trophies`](https://www.reddit.com/dev/api#GET_api_v1_me_trophies)|No
`GET`|[`/prefs/blocked`](https://www.reddit.com/dev/api#GET_prefs_blocked)|No
`GET`|[`/prefs/friends`](https://www.reddit.com/dev/api#GET_prefs_friends)|No
`GET`|[`/prefs/{where}`](https://www.reddit.com/dev/api#GET_prefs_%7Bwhere%7D)|No
`POST`|[`/api/login`](https://www.reddit.com/dev/api#POST_api_login)|[`RedditClient.login(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#login-java.lang.String-java.lang.String-)
`GET`|[`/api/me.json`](https://www.reddit.com/dev/api#GET_api_me.json)|[`RedditClient.me()`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#me--)

##apps
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/deleteapp`](https://www.reddit.com/dev/api#POST_api_deleteapp)|No
`POST`|[`/api/revokeapp`](https://www.reddit.com/dev/api#POST_api_revokeapp)|No
`POST`|[`/api/setappicon`](https://www.reddit.com/dev/api#POST_api_setappicon)|No
`POST`|[`/api/updateapp`](https://www.reddit.com/dev/api#POST_api_updateapp)|No
`POST`|[`/api/adddeveloper`](https://www.reddit.com/dev/api#POST_api_adddeveloper)|[`LoggedInAccount.addDeveloper(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#addDeveloper-java.lang.String-java.lang.String-)
`POST`|[`/api/removedeveloper`](https://www.reddit.com/dev/api#POST_api_removedeveloper)|[`LoggedInAccount.removeDeveloper(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#removeDeveloper-java.lang.String-java.lang.String-)

##captcha
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/api/needs_captcha.json`](https://www.reddit.com/dev/api#GET_api_needs_captcha.json)|[`RedditClient.needsCaptcha()`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#needsCaptcha--)
`POST`|[`/api/new_captcha`](https://www.reddit.com/dev/api#POST_api_new_captcha)|[`RedditClient.getNewCaptcha()`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getNewCaptcha--)
`GET`|[`/captcha/{iden}`](https://www.reddit.com/dev/api#GET_captcha_%7Biden%7D)|[`RedditClient.getCaptcha(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getCaptcha-java.lang.String-)

##flair
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/clearflairtemplates`](https://www.reddit.com/dev/api#POST_api_clearflairtemplates)|No
`POST`|[`/api/deleteflair`](https://www.reddit.com/dev/api#POST_api_deleteflair)|No
`POST`|[`/api/deleteflairtemplate`](https://www.reddit.com/dev/api#POST_api_deleteflairtemplate)|No
`POST`|[`/api/flair`](https://www.reddit.com/dev/api#POST_api_flair)|No
`POST`|[`/api/flairconfig`](https://www.reddit.com/dev/api#POST_api_flairconfig)|No
`POST`|[`/api/flaircsv`](https://www.reddit.com/dev/api#POST_api_flaircsv)|No
`GET`|[`/api/flairlist`](https://www.reddit.com/dev/api#GET_api_flairlist)|No
`POST`|[`/api/flairselector`](https://www.reddit.com/dev/api#POST_api_flairselector)|No
`POST`|[`/api/flairtemplate`](https://www.reddit.com/dev/api#POST_api_flairtemplate)|No
`POST`|[`/api/selectflair`](https://www.reddit.com/dev/api#POST_api_selectflair)|No
`POST`|[`/api/setflairenabled`](https://www.reddit.com/dev/api#POST_api_setflairenabled)|No

##links & comments
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/editusertext`](https://www.reddit.com/dev/api#POST_api_editusertext)|No
`GET`|[`/api/info`](https://www.reddit.com/dev/api#GET_api_info)|No
`POST`|[`/api/morechildren`](https://www.reddit.com/dev/api#POST_api_morechildren)|No
`POST`|[`/api/report`](https://www.reddit.com/dev/api#POST_api_report)|No
`GET`|[`/api/saved_categories.json`](https://www.reddit.com/dev/api#GET_api_saved_categories.json)|No
`POST`|[`/api/set_contest_mode`](https://www.reddit.com/dev/api#POST_api_set_contest_mode)|No
`POST`|[`/api/set_subreddit_sticky`](https://www.reddit.com/dev/api#POST_api_set_subreddit_sticky)|No
`POST`|[`/api/store_visits`](https://www.reddit.com/dev/api#POST_api_store_visits)|No
`POST`|[`/api/comment`](https://www.reddit.com/dev/api#POST_api_comment)|[`LoggedInAccount.reply(Contribution, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#reply-net.dean.jraw.models.Contribution-java.lang.String-)
`POST`|[`/api/del`](https://www.reddit.com/dev/api#POST_api_del)|[`LoggedInAccount.delete(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#delete-java.lang.String-)
`POST`|[`/api/hide`](https://www.reddit.com/dev/api#POST_api_hide)|[`LoggedInAccount.hide(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#hide-net.dean.jraw.models.Submission-boolean-)
`POST`|[`/api/marknsfw`](https://www.reddit.com/dev/api#POST_api_marknsfw)|[`LoggedInAccount.setNsfw(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#setNsfw-net.dean.jraw.models.Submission-boolean-)
`POST`|[`/api/save`](https://www.reddit.com/dev/api#POST_api_save)|[`LoggedInAccount.setSaved(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#setSaved-net.dean.jraw.models.Submission-boolean-)
`POST`|[`/api/sendreplies`](https://www.reddit.com/dev/api#POST_api_sendreplies)|[`LoggedInAccount.setSendRepliesToInbox(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#setSendRepliesToInbox-net.dean.jraw.models.Submission-boolean-)
`POST`|[`/api/submit`](https://www.reddit.com/dev/api#POST_api_submit)|[`LoggedInAccount.submitContent(SubmissionBuilder, Captcha, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#submitContent-net.dean.jraw.models.LoggedInAccount$SubmissionBuilder-net.dean.jraw.models.Captcha-java.lang.String-)
`POST`|[`/api/unhide`](https://www.reddit.com/dev/api#POST_api_unhide)|[`LoggedInAccount.hide(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#hide-net.dean.jraw.models.Submission-boolean-)
`POST`|[`/api/unmarknsfw`](https://www.reddit.com/dev/api#POST_api_unmarknsfw)|[`LoggedInAccount.setNsfw(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#setNsfw-net.dean.jraw.models.Submission-boolean-)
`POST`|[`/api/unsave`](https://www.reddit.com/dev/api#POST_api_unsave)|[`LoggedInAccount.setSaved(Submission, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#setSaved-net.dean.jraw.models.Submission-boolean-)
`POST`|[`/api/vote`](https://www.reddit.com/dev/api#POST_api_vote)|[`LoggedInAccount.vote(Thing, VoteDirection)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#vote-net.dean.jraw.models.Thing-net.dean.jraw.models.VoteDirection-)

##listings
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/by_id/{names}`](https://www.reddit.com/dev/api#GET_by_id_%7Bnames%7D)|[`SpecificPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/SpecificPaginator#getListing-boolean-)
`GET`|[`/comments/{article}`](https://www.reddit.com/dev/api#GET_comments_%7Barticle%7D)|[`RedditClient.getSubmission(SubmissionRequest)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getSubmission-net.dean.jraw.RedditClient$SubmissionRequest-)
`GET`|[`/controversial`](https://www.reddit.com/dev/api#GET_controversial)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/SubredditPaginator#getListing-boolean-)
`GET`|[`/hot`](https://www.reddit.com/dev/api#GET_hot)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/SubredditPaginator#getListing-boolean-)
`GET`|[`/new`](https://www.reddit.com/dev/api#GET_new)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/SubredditPaginator#getListing-boolean-)
`GET`|[`/random`](https://www.reddit.com/dev/api#GET_random)|[`RedditClient.getRandom(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getRandom-java.lang.String-)
`GET`|[`/sort`](https://www.reddit.com/dev/api#GET_sort)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/SubredditPaginator#getListing-boolean-)
`GET`|[`/top`](https://www.reddit.com/dev/api#GET_top)|[`SubredditPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/SubredditPaginator#getListing-boolean-)

##live threads
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/live/create`](https://www.reddit.com/dev/api#POST_api_live_create)|No
`POST`|[`/api/live/{thread}/accept_contributor_invite`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_accept_contributor_invite)|No
`POST`|[`/api/live/{thread}/close_thread`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_close_thread)|No
`POST`|[`/api/live/{thread}/delete_update`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_delete_update)|No
`POST`|[`/api/live/{thread}/edit`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_edit)|No
`POST`|[`/api/live/{thread}/invite_contributor`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_invite_contributor)|No
`POST`|[`/api/live/{thread}/leave_contributor`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_leave_contributor)|No
`POST`|[`/api/live/{thread}/report`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_report)|No
`POST`|[`/api/live/{thread}/rm_contributor`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_rm_contributor)|No
`POST`|[`/api/live/{thread}/rm_contributor_invite`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_rm_contributor_invite)|No
`POST`|[`/api/live/{thread}/set_contributor_permissions`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_set_contributor_permissions)|No
`POST`|[`/api/live/{thread}/strike_update`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_strike_update)|No
`POST`|[`/api/live/{thread}/update`](https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_update)|No
`GET`|[`/live/{thread}`](https://www.reddit.com/dev/api#GET_live_%7Bthread%7D)|No
`GET`|[`/live/{thread}/about.json`](https://www.reddit.com/dev/api#GET_live_%7Bthread%7D_about.json)|No
`GET`|[`/live/{thread}/contributors.json`](https://www.reddit.com/dev/api#GET_live_%7Bthread%7D_contributors.json)|No
`GET`|[`/live/{thread}/discussions`](https://www.reddit.com/dev/api#GET_live_%7Bthread%7D_discussions)|No

##moderation
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/about/edited`](https://www.reddit.com/dev/api#GET_about_edited)|No
`GET`|[`/about/log`](https://www.reddit.com/dev/api#GET_about_log)|No
`GET`|[`/about/modqueue`](https://www.reddit.com/dev/api#GET_about_modqueue)|No
`GET`|[`/about/reports`](https://www.reddit.com/dev/api#GET_about_reports)|No
`GET`|[`/about/spam`](https://www.reddit.com/dev/api#GET_about_spam)|No
`GET`|[`/about/unmoderated`](https://www.reddit.com/dev/api#GET_about_unmoderated)|No
`GET`|[`/about/{location}`](https://www.reddit.com/dev/api#GET_about_%7Blocation%7D)|No
`POST`|[`/api/accept_moderator_invite`](https://www.reddit.com/dev/api#POST_api_accept_moderator_invite)|No
`POST`|[`/api/approve`](https://www.reddit.com/dev/api#POST_api_approve)|No
`POST`|[`/api/distinguish`](https://www.reddit.com/dev/api#POST_api_distinguish)|No
`POST`|[`/api/ignore_reports`](https://www.reddit.com/dev/api#POST_api_ignore_reports)|No
`POST`|[`/api/leavecontributor`](https://www.reddit.com/dev/api#POST_api_leavecontributor)|No
`POST`|[`/api/leavemoderator`](https://www.reddit.com/dev/api#POST_api_leavemoderator)|No
`POST`|[`/api/remove`](https://www.reddit.com/dev/api#POST_api_remove)|No
`POST`|[`/api/unignore_reports`](https://www.reddit.com/dev/api#POST_api_unignore_reports)|No
`GET`|[`/stylesheet`](https://www.reddit.com/dev/api#GET_stylesheet)|[`RedditClient.getStylesheet(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getStylesheet-java.lang.String-)

##multis
Method|Endpoint|Implemented?
:----:|--------|------------
`DELETE`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api#DELETE_api_filter_%7Bfilterpath%7D)|No
`GET`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api#GET_api_filter_%7Bfilterpath%7D)|No
`POST`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api#POST_api_filter_%7Bfilterpath%7D)|No
`PUT`|[`/api/filter/{filterpath}`](https://www.reddit.com/dev/api#PUT_api_filter_%7Bfilterpath%7D)|No
`DELETE`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api#DELETE_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`GET`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api#GET_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`PUT`|[`/api/filter/{filterpath}/r/{srname}`](https://www.reddit.com/dev/api#PUT_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D)|No
`GET`|[`/api/multi/mine`](https://www.reddit.com/dev/api#GET_api_multi_mine)|[`MultiRedditManager.mine()`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#mine--)
`DELETE`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api#DELETE_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.delete(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#delete-java.lang.String-)
`GET`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api#GET_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#get-java.lang.String-java.lang.String-)
`POST`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api#POST_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.create(String, List, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#create-java.lang.String-java.util.List-boolean-)
`PUT`|[`/api/multi/{multipath}`](https://www.reddit.com/dev/api#PUT_api_multi_%7Bmultipath%7D)|[`MultiRedditManager.update(String, List, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#update-java.lang.String-java.util.List-boolean-)
`POST`|[`/api/multi/{multipath}/copy`](https://www.reddit.com/dev/api#POST_api_multi_%7Bmultipath%7D_copy)|[`MultiRedditManager.copy(String, String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#copy-java.lang.String-java.lang.String-java.lang.String-)
`GET`|[`/api/multi/{multipath}/description`](https://www.reddit.com/dev/api#GET_api_multi_%7Bmultipath%7D_description)|[`MultiRedditManager.getDescription(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#getDescription-java.lang.String-)
`PUT`|[`/api/multi/{multipath}/description`](https://www.reddit.com/dev/api#PUT_api_multi_%7Bmultipath%7D_description)|[`MultiRedditManager.updateDescription(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#updateDescription-java.lang.String-java.lang.String-)
`DELETE`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api#DELETE_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.removeSubreddit(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#removeSubreddit-java.lang.String-java.lang.String-)
`GET`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api#GET_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.get(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#get-java.lang.String-java.lang.String-)
`PUT`|[`/api/multi/{multipath}/r/{srname}`](https://www.reddit.com/dev/api#PUT_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D)|[`MultiRedditManager.addSubreddit(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#addSubreddit-java.lang.String-java.lang.String-)
`POST`|[`/api/multi/{multipath}/rename`](https://www.reddit.com/dev/api#POST_api_multi_%7Bmultipath%7D_rename)|[`MultiRedditManager.rename(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/MultiRedditManager#rename-java.lang.String-java.lang.String-)

##private messages
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/block`](https://www.reddit.com/dev/api#POST_api_block)|No
`POST`|[`/api/compose`](https://www.reddit.com/dev/api#POST_api_compose)|No
`POST`|[`/api/read_message`](https://www.reddit.com/dev/api#POST_api_read_message)|No
`POST`|[`/api/unread_message`](https://www.reddit.com/dev/api#POST_api_unread_message)|No
`POST`|[`/message/inbox`](https://www.reddit.com/dev/api#POST_message_inbox)|[`InboxPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/InboxPaginator#getListing-boolean-)
`GET`|[`/message/sent`](https://www.reddit.com/dev/api#GET_message_sent)|[`InboxPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/InboxPaginator#getListing-boolean-)
`GET`|[`/message/unread`](https://www.reddit.com/dev/api#GET_message_unread)|[`InboxPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/InboxPaginator#getListing-boolean-)
`GET`|[`/message/{where}`](https://www.reddit.com/dev/api#GET_message_%7Bwhere%7D)|[`InboxPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/InboxPaginator#getListing-boolean-)

##reddit gold
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/v1/gold/gild/{fullname}`](https://www.reddit.com/dev/api#POST_api_v1_gold_gild_%7Bfullname%7D)|No
`POST`|[`/api/v1/gold/give/{username}`](https://www.reddit.com/dev/api#POST_api_v1_gold_give_%7Busername%7D)|No

##search
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/search`](https://www.reddit.com/dev/api#GET_search)|[`SearchPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/SearchPaginator#getListing-boolean-)

##subreddits
Method|Endpoint|Implemented?
:----:|--------|------------
`GET`|[`/about/banned`](https://www.reddit.com/dev/api#GET_about_banned)|No
`GET`|[`/about/contributors`](https://www.reddit.com/dev/api#GET_about_contributors)|No
`GET`|[`/about/moderators`](https://www.reddit.com/dev/api#GET_about_moderators)|No
`GET`|[`/about/wikibanned`](https://www.reddit.com/dev/api#GET_about_wikibanned)|No
`GET`|[`/about/wikicontributors`](https://www.reddit.com/dev/api#GET_about_wikicontributors)|No
`GET`|[`/about/{where}`](https://www.reddit.com/dev/api#GET_about_%7Bwhere%7D)|No
`POST`|[`/api/delete_sr_header`](https://www.reddit.com/dev/api#POST_api_delete_sr_header)|No
`POST`|[`/api/delete_sr_img`](https://www.reddit.com/dev/api#POST_api_delete_sr_img)|No
`GET`|[`/api/recommend/sr/{srnames}`](https://www.reddit.com/dev/api#GET_api_recommend_sr_%7Bsrnames%7D)|No
`POST`|[`/api/site_admin`](https://www.reddit.com/dev/api#POST_api_site_admin)|No
`POST`|[`/api/subreddit_stylesheet`](https://www.reddit.com/dev/api#POST_api_subreddit_stylesheet)|No
`POST`|[`/api/upload_sr_img`](https://www.reddit.com/dev/api#POST_api_upload_sr_img)|No
`GET`|[`/r/{subreddit}/about/edit.json`](https://www.reddit.com/dev/api#GET_r_%7Bsubreddit%7D_about_edit.json)|No
`GET`|[`/subreddits/search`](https://www.reddit.com/dev/api#GET_subreddits_search)|No
`POST`|[`/api/search_reddit_names.json`](https://www.reddit.com/dev/api#POST_api_search_reddit_names.json)|[`RedditClient.searchSubreddits(String, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#searchSubreddits-java.lang.String-boolean-)
`GET`|[`/api/submit_text.json`](https://www.reddit.com/dev/api#GET_api_submit_text.json)|[`RedditClient.getSubmitText(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getSubmitText-java.lang.String-)
`GET`|[`/api/subreddits_by_topic.json`](https://www.reddit.com/dev/api#GET_api_subreddits_by_topic.json)|[`RedditClient.getSubredditsByTopic(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getSubredditsByTopic-java.lang.String-)
`POST`|[`/api/subscribe`](https://www.reddit.com/dev/api#POST_api_subscribe)|[`LoggedInAccount.setSubscribed(Subreddit, boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/models/LoggedInAccount#setSubscribed-net.dean.jraw.models.Subreddit-boolean-)
`GET`|[`/r/{subreddit}/about.json`](https://www.reddit.com/dev/api#GET_r_%7Bsubreddit%7D_about.json)|[`RedditClient.getSubreddit(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getSubreddit-java.lang.String-)
`GET`|[`/subreddits/mine/contributor`](https://www.reddit.com/dev/api#GET_subreddits_mine_contributor)|[`UserSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserSubredditsPaginator#getListing-boolean-)
`GET`|[`/subreddits/mine/moderator`](https://www.reddit.com/dev/api#GET_subreddits_mine_moderator)|[`UserSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserSubredditsPaginator#getListing-boolean-)
`GET`|[`/subreddits/mine/subscriber`](https://www.reddit.com/dev/api#GET_subreddits_mine_subscriber)|[`UserSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserSubredditsPaginator#getListing-boolean-)
`GET`|[`/subreddits/mine/{where}`](https://www.reddit.com/dev/api#GET_subreddits_mine_%7Bwhere%7D)|[`UserSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserSubredditsPaginator#getListing-boolean-)
`GET`|[`/subreddits/new`](https://www.reddit.com/dev/api#GET_subreddits_new)|[`AllSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/AllSubredditsPaginator#getListing-boolean-)
`GET`|[`/subreddits/popular`](https://www.reddit.com/dev/api#GET_subreddits_popular)|[`AllSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/AllSubredditsPaginator#getListing-boolean-)
`GET`|[`/subreddits/{where}`](https://www.reddit.com/dev/api#GET_subreddits_%7Bwhere%7D)|[`AllSubredditsPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/AllSubredditsPaginator#getListing-boolean-)

##users
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/friend`](https://www.reddit.com/dev/api#POST_api_friend)|No
`POST`|[`/api/setpermissions`](https://www.reddit.com/dev/api#POST_api_setpermissions)|No
`POST`|[`/api/unfriend`](https://www.reddit.com/dev/api#POST_api_unfriend)|No
`DELETE`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api#DELETE_api_v1_me_friends_%7Busername%7D)|No
`GET`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api#GET_api_v1_me_friends_%7Busername%7D)|No
`PUT`|[`/api/v1/me/friends/{username}`](https://www.reddit.com/dev/api#PUT_api_v1_me_friends_%7Busername%7D)|No
`GET`|[`/api/v1/user/{username}/trophies`](https://www.reddit.com/dev/api#GET_api_v1_user_%7Busername%7D_trophies)|No
`GET`|[`/api/username_available.json`](https://www.reddit.com/dev/api#GET_api_username_available.json)|[`RedditClient.isUsernameAvailable(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#isUsernameAvailable-java.lang.String-)
`GET`|[`/user/{username}/about.json`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_about.json)|[`RedditClient.getUser(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getUser-java.lang.String-)
`GET`|[`/user/{username}/comments`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_comments)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)
`GET`|[`/user/{username}/disliked`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_disliked)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)
`GET`|[`/user/{username}/gilded`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_gilded)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)
`GET`|[`/user/{username}/hidden`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_hidden)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)
`GET`|[`/user/{username}/liked`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_liked)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)
`GET`|[`/user/{username}/overview`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_overview)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)
`GET`|[`/user/{username}/saved`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_saved)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)
`GET`|[`/user/{username}/submitted`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_submitted)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)
`GET`|[`/user/{username}/{where}`](https://www.reddit.com/dev/api#GET_user_%7Busername%7D_%7Bwhere%7D)|[`UserContributionPaginator.getListing(boolean)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/pagination/UserContributionPaginator#getListing-boolean-)

##wiki
Method|Endpoint|Implemented?
:----:|--------|------------
`POST`|[`/api/wiki/alloweditor/act`](https://www.reddit.com/dev/api#POST_api_wiki_alloweditor_act)|No
`POST`|[`/api/wiki/alloweditor/add`](https://www.reddit.com/dev/api#POST_api_wiki_alloweditor_add)|No
`POST`|[`/api/wiki/alloweditor/del`](https://www.reddit.com/dev/api#POST_api_wiki_alloweditor_del)|No
`POST`|[`/api/wiki/edit`](https://www.reddit.com/dev/api#POST_api_wiki_edit)|No
`POST`|[`/api/wiki/hide`](https://www.reddit.com/dev/api#POST_api_wiki_hide)|No
`POST`|[`/api/wiki/revert`](https://www.reddit.com/dev/api#POST_api_wiki_revert)|No
`GET`|[`/wiki/discussions/{page}`](https://www.reddit.com/dev/api#GET_wiki_discussions_%7Bpage%7D)|No
`GET`|[`/wiki/revisions`](https://www.reddit.com/dev/api#GET_wiki_revisions)|No
`POST`|[`/wiki/revisions/{page}`](https://www.reddit.com/dev/api#POST_wiki_revisions_%7Bpage%7D)|No
`GET`|[`/wiki/settings/{page}`](https://www.reddit.com/dev/api#GET_wiki_settings_%7Bpage%7D)|No
`GET`|[`/wiki/pages`](https://www.reddit.com/dev/api#GET_wiki_pages)|[`RedditClient.getWikiPages(String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getWikiPages-java.lang.String-)
`GET`|[`/wiki/{page}`](https://www.reddit.com/dev/api#GET_wiki_%7Bpage%7D)|[`RedditClient.getWikiPage(String, String)`](https://thatjavanerd.github.io/JRAW/docs/0.4.0/net/dean/jraw/RedditClient#getWikiPage-java.lang.String-java.lang.String-)
