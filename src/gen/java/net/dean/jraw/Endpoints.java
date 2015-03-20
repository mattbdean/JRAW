package net.dean.jraw;

/* This class is updated by running ./gradlew endpoints:update. Do not modify directly */
/** This class is an automatically generated enumeration of Reddit's API endpoints */
@SuppressWarnings("unused")
public enum Endpoints {

    ///////// (any scope) /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_friend">{@code POST /api/friend}</a>" included in the "(any scope)" scope */
    FRIEND("POST /api/friend", "(any scope)"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_unfriend">{@code POST /api/unfriend}</a>" included in the "(any scope)" scope */
    UNFRIEND("POST /api/unfriend", "(any scope)"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_needs_captcha.json">{@code GET /api/needs_captcha.json}</a>" included in the "(any scope)" scope */
    NEEDS_CAPTCHA("GET /api/needs_captcha.json", "(any scope)"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_new_captcha">{@code POST /api/new_captcha}</a>" included in the "(any scope)" scope */
    NEW_CAPTCHA("POST /api/new_captcha", "(any scope)"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_captcha_%7Biden%7D">{@code GET /captcha/{iden}}</a>" included in the "(any scope)" scope */
    CAPTCHA_IDEN("GET /captcha/{iden}", "(any scope)"),

    ///////// account /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#PATCH_api_v1_me_prefs">{@code PATCH /api/v1/me/prefs}</a>" included in the "account" scope */
    OAUTH_ME_PREFS_PATCH("PATCH /api/v1/me/prefs", "account"),

    ///////// creddits /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_gild_%7Bfullname%7D">{@code POST /api/v1/gold/gild/{fullname}}</a>" included in the "creddits" scope */
    OAUTH_GOLD_GILD_FULLNAME("POST /api/v1/gold/gild/{fullname}", "creddits"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_give_%7Busername%7D">{@code POST /api/v1/gold/give/{username}}</a>" included in the "creddits" scope */
    OAUTH_GOLD_GIVE_USERNAME("POST /api/v1/gold/give/{username}", "creddits"),

    ///////// edit /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_delete_update">{@code POST /api/live/{thread}/delete_update}</a>" included in the "edit" scope */
    LIVE_THREAD_DELETE_UPDATE("POST /api/live/{thread}/delete_update", "edit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_strike_update">{@code POST /api/live/{thread}/strike_update}</a>" included in the "edit" scope */
    LIVE_THREAD_STRIKE_UPDATE("POST /api/live/{thread}/strike_update", "edit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_del">{@code POST /api/del}</a>" included in the "edit" scope */
    DEL("POST /api/del", "edit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_editusertext">{@code POST /api/editusertext}</a>" included in the "edit" scope */
    EDITUSERTEXT("POST /api/editusertext", "edit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_sendreplies">{@code POST /api/sendreplies}</a>" included in the "edit" scope */
    SENDREPLIES("POST /api/sendreplies", "edit"),

    ///////// flair /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_flairselector">{@code POST /api/flairselector}</a>" included in the "flair" scope */
    FLAIRSELECTOR("POST /api/flairselector", "flair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_selectflair">{@code POST /api/selectflair}</a>" included in the "flair" scope */
    SELECTFLAIR("POST /api/selectflair", "flair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_setflairenabled">{@code POST /api/setflairenabled}</a>" included in the "flair" scope */
    SETFLAIRENABLED("POST /api/setflairenabled", "flair"),

    ///////// history /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_comments">{@code GET /user/{username}/comments}</a>" included in the "history" scope */
    USER_USERNAME_COMMENTS("GET /user/{username}/comments", "history"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_disliked">{@code GET /user/{username}/disliked}</a>" included in the "history" scope */
    USER_USERNAME_DISLIKED("GET /user/{username}/disliked", "history"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_gilded">{@code GET /user/{username}/gilded}</a>" included in the "history" scope */
    USER_USERNAME_GILDED("GET /user/{username}/gilded", "history"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_hidden">{@code GET /user/{username}/hidden}</a>" included in the "history" scope */
    USER_USERNAME_HIDDEN("GET /user/{username}/hidden", "history"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_liked">{@code GET /user/{username}/liked}</a>" included in the "history" scope */
    USER_USERNAME_LIKED("GET /user/{username}/liked", "history"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_overview">{@code GET /user/{username}/overview}</a>" included in the "history" scope */
    USER_USERNAME_OVERVIEW("GET /user/{username}/overview", "history"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_saved">{@code GET /user/{username}/saved}</a>" included in the "history" scope */
    USER_USERNAME_SAVED("GET /user/{username}/saved", "history"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_submitted">{@code GET /user/{username}/submitted}</a>" included in the "history" scope */
    USER_USERNAME_SUBMITTED("GET /user/{username}/submitted", "history"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_%7Bwhere%7D">{@code GET /user/{username}/{where}}</a>" included in the "history" scope */
    USER_USERNAME_WHERE("GET /user/{username}/{where}", "history"),

    ///////// identity /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me">{@code GET /api/v1/me}</a>" included in the "identity" scope */
    OAUTH_ME("GET /api/v1/me", "identity"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_prefs">{@code GET /api/v1/me/prefs}</a>" included in the "identity" scope */
    OAUTH_ME_PREFS_GET("GET /api/v1/me/prefs", "identity"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_trophies">{@code GET /api/v1/me/trophies}</a>" included in the "identity" scope */
    OAUTH_ME_TROPHIES("GET /api/v1/me/trophies", "identity"),

    ///////// livemanage /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_accept_contributor_invite">{@code POST /api/live/{thread}/accept_contributor_invite}</a>" included in the "livemanage" scope */
    LIVE_THREAD_ACCEPT_CONTRIBUTOR_INVITE("POST /api/live/{thread}/accept_contributor_invite", "livemanage"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_close_thread">{@code POST /api/live/{thread}/close_thread}</a>" included in the "livemanage" scope */
    LIVE_THREAD_CLOSE_THREAD("POST /api/live/{thread}/close_thread", "livemanage"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_edit">{@code POST /api/live/{thread}/edit}</a>" included in the "livemanage" scope */
    LIVE_THREAD_EDIT("POST /api/live/{thread}/edit", "livemanage"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_invite_contributor">{@code POST /api/live/{thread}/invite_contributor}</a>" included in the "livemanage" scope */
    LIVE_THREAD_INVITE_CONTRIBUTOR("POST /api/live/{thread}/invite_contributor", "livemanage"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_leave_contributor">{@code POST /api/live/{thread}/leave_contributor}</a>" included in the "livemanage" scope */
    LIVE_THREAD_LEAVE_CONTRIBUTOR("POST /api/live/{thread}/leave_contributor", "livemanage"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_rm_contributor">{@code POST /api/live/{thread}/rm_contributor}</a>" included in the "livemanage" scope */
    LIVE_THREAD_RM_CONTRIBUTOR("POST /api/live/{thread}/rm_contributor", "livemanage"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_rm_contributor_invite">{@code POST /api/live/{thread}/rm_contributor_invite}</a>" included in the "livemanage" scope */
    LIVE_THREAD_RM_CONTRIBUTOR_INVITE("POST /api/live/{thread}/rm_contributor_invite", "livemanage"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_set_contributor_permissions">{@code POST /api/live/{thread}/set_contributor_permissions}</a>" included in the "livemanage" scope */
    LIVE_THREAD_SET_CONTRIBUTOR_PERMISSIONS("POST /api/live/{thread}/set_contributor_permissions", "livemanage"),

    ///////// modconfig /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_header">{@code POST /api/delete_sr_header}</a>" included in the "modconfig" scope */
    DELETE_SR_HEADER("POST /api/delete_sr_header", "modconfig"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_img">{@code POST /api/delete_sr_img}</a>" included in the "modconfig" scope */
    DELETE_SR_IMG("POST /api/delete_sr_img", "modconfig"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_site_admin">{@code POST /api/site_admin}</a>" included in the "modconfig" scope */
    SITE_ADMIN("POST /api/site_admin", "modconfig"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_subreddit_stylesheet">{@code POST /api/subreddit_stylesheet}</a>" included in the "modconfig" scope */
    SUBREDDIT_STYLESHEET("POST /api/subreddit_stylesheet", "modconfig"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_upload_sr_img">{@code POST /api/upload_sr_img}</a>" included in the "modconfig" scope */
    UPLOAD_SR_IMG("POST /api/upload_sr_img", "modconfig"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_r_subreddit_about_edit.json">{@code GET /r/subreddit/about/edit.json}</a>" included in the "modconfig" scope */
    SUBREDDIT_ABOUT_EDIT("GET /r/subreddit/about/edit.json", "modconfig"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_stylesheet">{@code GET /stylesheet}</a>" included in the "modconfig" scope */
    STYLESHEET("GET /stylesheet", "modconfig"),

    ///////// modflair /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_clearflairtemplates">{@code POST /api/clearflairtemplates}</a>" included in the "modflair" scope */
    CLEARFLAIRTEMPLATES("POST /api/clearflairtemplates", "modflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_deleteflair">{@code POST /api/deleteflair}</a>" included in the "modflair" scope */
    DELETEFLAIR("POST /api/deleteflair", "modflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_deleteflairtemplate">{@code POST /api/deleteflairtemplate}</a>" included in the "modflair" scope */
    DELETEFLAIRTEMPLATE("POST /api/deleteflairtemplate", "modflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_flair">{@code POST /api/flair}</a>" included in the "modflair" scope */
    FLAIR("POST /api/flair", "modflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_flairconfig">{@code POST /api/flairconfig}</a>" included in the "modflair" scope */
    FLAIRCONFIG("POST /api/flairconfig", "modflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_flaircsv">{@code POST /api/flaircsv}</a>" included in the "modflair" scope */
    FLAIRCSV("POST /api/flaircsv", "modflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_flairlist">{@code GET /api/flairlist}</a>" included in the "modflair" scope */
    FLAIRLIST("GET /api/flairlist", "modflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_flairtemplate">{@code POST /api/flairtemplate}</a>" included in the "modflair" scope */
    FLAIRTEMPLATE("POST /api/flairtemplate", "modflair"),

    ///////// modlog /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_log">{@code GET /about/log}</a>" included in the "modlog" scope */
    ABOUT_LOG("GET /about/log", "modlog"),

    ///////// modothers /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_setpermissions">{@code POST /api/setpermissions}</a>" included in the "modothers" scope */
    SETPERMISSIONS("POST /api/setpermissions", "modothers"),

    ///////// modposts /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_approve">{@code POST /api/approve}</a>" included in the "modposts" scope */
    APPROVE("POST /api/approve", "modposts"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_distinguish">{@code POST /api/distinguish}</a>" included in the "modposts" scope */
    DISTINGUISH("POST /api/distinguish", "modposts"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_ignore_reports">{@code POST /api/ignore_reports}</a>" included in the "modposts" scope */
    IGNORE_REPORTS("POST /api/ignore_reports", "modposts"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_remove">{@code POST /api/remove}</a>" included in the "modposts" scope */
    REMOVE("POST /api/remove", "modposts"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_set_contest_mode">{@code POST /api/set_contest_mode}</a>" included in the "modposts" scope */
    SET_CONTEST_MODE("POST /api/set_contest_mode", "modposts"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_unignore_reports">{@code POST /api/unignore_reports}</a>" included in the "modposts" scope */
    UNIGNORE_REPORTS("POST /api/unignore_reports", "modposts"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_marknsfw">{@code POST /api/marknsfw}</a>" included in the "modposts" scope */
    MARKNSFW("POST /api/marknsfw", "modposts"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_set_subreddit_sticky">{@code POST /api/set_subreddit_sticky}</a>" included in the "modposts" scope */
    SET_SUBREDDIT_STICKY("POST /api/set_subreddit_sticky", "modposts"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_unmarknsfw">{@code POST /api/unmarknsfw}</a>" included in the "modposts" scope */
    UNMARKNSFW("POST /api/unmarknsfw", "modposts"),

    ///////// modself /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_accept_moderator_invite">{@code POST /api/accept_moderator_invite}</a>" included in the "modself" scope */
    ACCEPT_MODERATOR_INVITE("POST /api/accept_moderator_invite", "modself"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_leavecontributor">{@code POST /api/leavecontributor}</a>" included in the "modself" scope */
    LEAVECONTRIBUTOR("POST /api/leavecontributor", "modself"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_leavemoderator">{@code POST /api/leavemoderator}</a>" included in the "modself" scope */
    LEAVEMODERATOR("POST /api/leavemoderator", "modself"),

    ///////// modwiki /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_add">{@code POST /api/wiki/alloweditor/add}</a>" included in the "modwiki" scope */
    WIKI_ALLOWEDITOR_ADD("POST /api/wiki/alloweditor/add", "modwiki"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_del">{@code POST /api/wiki/alloweditor/del}</a>" included in the "modwiki" scope */
    WIKI_ALLOWEDITOR_DEL("POST /api/wiki/alloweditor/del", "modwiki"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_%7Bact%7D">{@code POST /api/wiki/alloweditor/{act}}</a>" included in the "modwiki" scope */
    WIKI_ALLOWEDITOR_ACT("POST /api/wiki/alloweditor/{act}", "modwiki"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_hide">{@code POST /api/wiki/hide}</a>" included in the "modwiki" scope */
    WIKI_HIDE("POST /api/wiki/hide", "modwiki"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_revert">{@code POST /api/wiki/revert}</a>" included in the "modwiki" scope */
    WIKI_REVERT("POST /api/wiki/revert", "modwiki"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_wiki_settings_%7Bpage%7D">{@code POST /wiki/settings/{page}}</a>" included in the "modwiki" scope */
    WIKI_SETTINGS_PAGE_POST("POST /wiki/settings/{page}", "modwiki"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_wiki_settings_%7Bpage%7D">{@code GET /wiki/settings/{page}}</a>" included in the "modwiki" scope */
    WIKI_SETTINGS_PAGE_GET("GET /wiki/settings/{page}", "modwiki"),

    ///////// mysubreddits /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends_%7Busername%7D">{@code GET /api/v1/me/friends/{username}}</a>" included in the "mysubreddits" scope */
    OAUTH_ME_FRIENDS_USERNAME_GET("GET /api/v1/me/friends/{username}", "mysubreddits"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_karma">{@code GET /api/v1/me/karma}</a>" included in the "mysubreddits" scope */
    OAUTH_ME_KARMA("GET /api/v1/me/karma", "mysubreddits"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_contributor">{@code GET /subreddits/mine/contributor}</a>" included in the "mysubreddits" scope */
    SUBREDDITS_MINE_CONTRIBUTOR("GET /subreddits/mine/contributor", "mysubreddits"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_moderator">{@code GET /subreddits/mine/moderator}</a>" included in the "mysubreddits" scope */
    SUBREDDITS_MINE_MODERATOR("GET /subreddits/mine/moderator", "mysubreddits"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_subscriber">{@code GET /subreddits/mine/subscriber}</a>" included in the "mysubreddits" scope */
    SUBREDDITS_MINE_SUBSCRIBER("GET /subreddits/mine/subscriber", "mysubreddits"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_%7Bwhere%7D">{@code GET /subreddits/mine/{where}}</a>" included in the "mysubreddits" scope */
    SUBREDDITS_MINE_WHERE("GET /subreddits/mine/{where}", "mysubreddits"),

    ///////// privatemessages /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_block">{@code POST /api/block}</a>" included in the "privatemessages" scope */
    BLOCK("POST /api/block", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_unblock_subreddit">{@code POST /api/unblock_subreddit}</a>" included in the "privatemessages" scope */
    UNBLOCK_SUBREDDIT("POST /api/unblock_subreddit", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_compose">{@code POST /api/compose}</a>" included in the "privatemessages" scope */
    COMPOSE("POST /api/compose", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_read_all_messages">{@code POST /api/read_all_messages}</a>" included in the "privatemessages" scope */
    READ_ALL_MESSAGES("POST /api/read_all_messages", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_read_message">{@code POST /api/read_message}</a>" included in the "privatemessages" scope */
    READ_MESSAGE("POST /api/read_message", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_unread_message">{@code POST /api/unread_message}</a>" included in the "privatemessages" scope */
    UNREAD_MESSAGE("POST /api/unread_message", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_message_inbox">{@code GET /message/inbox}</a>" included in the "privatemessages" scope */
    MESSAGE_INBOX("GET /message/inbox", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_message_sent">{@code GET /message/sent}</a>" included in the "privatemessages" scope */
    MESSAGE_SENT("GET /message/sent", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_message_unread">{@code GET /message/unread}</a>" included in the "privatemessages" scope */
    MESSAGE_UNREAD("GET /message/unread", "privatemessages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_message_%7Bwhere%7D">{@code GET /message/{where}}</a>" included in the "privatemessages" scope */
    MESSAGE_WHERE("GET /message/{where}", "privatemessages"),

    ///////// read /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_filter_%7Bfilterpath%7D">{@code GET /api/filter/{filterpath}}</a>" included in the "read" scope */
    FILTER_FILTERPATH_GET("GET /api/filter/{filterpath}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D">{@code GET /api/filter/{filterpath}/r/{srname}}</a>" included in the "read" scope */
    FILTER_FILTERPATH_R_SRNAME_GET("GET /api/filter/{filterpath}/r/{srname}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_contributors.json">{@code GET /live/{thread}/contributors.json}</a>" included in the "read" scope */
    LIVE_THREAD_CONTRIBUTORS("GET /live/{thread}/contributors.json", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_discussions">{@code GET /live/{thread}/discussions}</a>" included in the "read" scope */
    LIVE_THREAD_DISCUSSIONS("GET /live/{thread}/discussions", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_sidebar">{@code GET /sidebar}</a>" included in the "read" scope */
    SIDEBAR("GET /sidebar", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_sticky">{@code GET /sticky}</a>" included in the "read" scope */
    STICKY("GET /sticky", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_banned">{@code GET /about/banned}</a>" included in the "read" scope */
    ABOUT_BANNED("GET /about/banned", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_contributors">{@code GET /about/contributors}</a>" included in the "read" scope */
    ABOUT_CONTRIBUTORS("GET /about/contributors", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_edited">{@code GET /about/edited}</a>" included in the "read" scope */
    ABOUT_EDITED("GET /about/edited", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_location">{@code GET /about/location}</a>" included in the "read" scope */
    ABOUT_LOCATION("GET /about/location", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_moderators">{@code GET /about/moderators}</a>" included in the "read" scope */
    ABOUT_MODERATORS("GET /about/moderators", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_modqueue">{@code GET /about/modqueue}</a>" included in the "read" scope */
    ABOUT_MODQUEUE("GET /about/modqueue", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_reports">{@code GET /about/reports}</a>" included in the "read" scope */
    ABOUT_REPORTS("GET /about/reports", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_spam">{@code GET /about/spam}</a>" included in the "read" scope */
    ABOUT_SPAM("GET /about/spam", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_unmoderated">{@code GET /about/unmoderated}</a>" included in the "read" scope */
    ABOUT_UNMODERATED("GET /about/unmoderated", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_wikibanned">{@code GET /about/wikibanned}</a>" included in the "read" scope */
    ABOUT_WIKIBANNED("GET /about/wikibanned", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_wikicontributors">{@code GET /about/wikicontributors}</a>" included in the "read" scope */
    ABOUT_WIKICONTRIBUTORS("GET /about/wikicontributors", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_about_%7Bwhere%7D">{@code GET /about/{where}}</a>" included in the "read" scope */
    ABOUT_WHERE("GET /about/{where}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_info">{@code GET /api/info}</a>" included in the "read" scope */
    INFO("GET /api/info", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_morechildren">{@code GET /api/morechildren}</a>" included in the "read" scope */
    MORECHILDREN("GET /api/morechildren", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_mine">{@code GET /api/multi/mine}</a>" included in the "read" scope */
    MULTI_MINE("GET /api/multi/mine", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_user_%7Busername%7D">{@code GET /api/multi/user/{username}}</a>" included in the "read" scope */
    MULTI_USER_USERNAME("GET /api/multi/user/{username}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D">{@code GET /api/multi/{multipath}}</a>" included in the "read" scope */
    MULTI_MULTIPATH_GET("GET /api/multi/{multipath}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D_description">{@code GET /api/multi/{multipath}/description}</a>" included in the "read" scope */
    MULTI_MULTIPATH_DESCRIPTION_GET("GET /api/multi/{multipath}/description", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D_description">{@code PUT /api/multi/{multipath}/description}</a>" included in the "read" scope */
    MULTI_MULTIPATH_DESCRIPTION_PUT("PUT /api/multi/{multipath}/description", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D">{@code GET /api/multi/{multipath}/r/{srname}}</a>" included in the "read" scope */
    MULTI_MULTIPATH_R_SRNAME_GET("GET /api/multi/{multipath}/r/{srname}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_recommend_sr_%7Bsrnames%7D">{@code GET /api/recommend/sr/{srnames}}</a>" included in the "read" scope */
    RECOMMEND_SR_SRNAMES("GET /api/recommend/sr/{srnames}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_search_reddit_names.json">{@code POST /api/search_reddit_names.json}</a>" included in the "read" scope */
    SEARCH_REDDIT_NAMES("POST /api/search_reddit_names.json", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_subreddits_by_topic.json">{@code GET /api/subreddits_by_topic.json}</a>" included in the "read" scope */
    SUBREDDITS_BY_TOPIC("GET /api/subreddits_by_topic.json", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_blocked">{@code GET /api/v1/me/blocked}</a>" included in the "read" scope */
    OAUTH_ME_BLOCKED("GET /api/v1/me/blocked", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends">{@code GET /api/v1/me/friends}</a>" included in the "read" scope */
    OAUTH_ME_FRIENDS("GET /api/v1/me/friends", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_user_%7Busername%7D_trophies">{@code GET /api/v1/user/{username}/trophies}</a>" included in the "read" scope */
    OAUTH_USER_USERNAME_TROPHIES("GET /api/v1/user/{username}/trophies", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_by_id_%7Bnames%7D">{@code GET /by_id/{names}}</a>" included in the "read" scope */
    BY_ID_NAMES("GET /by_id/{names}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_comments_%7Barticle%7D">{@code GET /comments/{article}}</a>" included in the "read" scope */
    COMMENTS_ARTICLE("GET /comments/{article}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_controversial">{@code GET /controversial}</a>" included in the "read" scope */
    CONTROVERSIAL("GET /controversial", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_duplicates_%7Barticle%7D">{@code GET /duplicates/{article}}</a>" included in the "read" scope */
    DUPLICATES_ARTICLE("GET /duplicates/{article}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_hot">{@code GET /hot}</a>" included in the "read" scope */
    HOT("GET /hot", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D">{@code GET /live/{thread}}</a>" included in the "read" scope */
    LIVE_THREAD("GET /live/{thread}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_live_%7Bthread%7D_about.json">{@code GET /live/{thread}/about.json}</a>" included in the "read" scope */
    LIVE_THREAD_ABOUT("GET /live/{thread}/about.json", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_new">{@code GET /new}</a>" included in the "read" scope */
    NEW("GET /new", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_prefs_blocked">{@code GET /prefs/blocked}</a>" included in the "read" scope */
    PREFS_BLOCKED("GET /prefs/blocked", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_prefs_friends">{@code GET /prefs/friends}</a>" included in the "read" scope */
    PREFS_FRIENDS("GET /prefs/friends", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_prefs_%7Bwhere%7D">{@code GET /prefs/{where}}</a>" included in the "read" scope */
    PREFS_WHERE("GET /prefs/{where}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_r_%7Bsubreddit%7D_about.json">{@code GET /r/{subreddit}/about.json}</a>" included in the "read" scope */
    SUBREDDIT_ABOUT("GET /r/{subreddit}/about.json", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_random">{@code GET /random}</a>" included in the "read" scope */
    RANDOM("GET /random", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_related_%7Barticle%7D">{@code GET /related/{article}}</a>" included in the "read" scope */
    RELATED_ARTICLE("GET /related/{article}", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_search">{@code GET /search}</a>" included in the "read" scope */
    SEARCH("GET /search", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_sort">{@code GET /sort}</a>" included in the "read" scope */
    SORT("GET /sort", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_employee">{@code GET /subreddits/employee}</a>" included in the "read" scope */
    SUBREDDITS_EMPLOYEE("GET /subreddits/employee", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_gold">{@code GET /subreddits/gold}</a>" included in the "read" scope */
    SUBREDDITS_GOLD("GET /subreddits/gold", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_new">{@code GET /subreddits/new}</a>" included in the "read" scope */
    SUBREDDITS_NEW("GET /subreddits/new", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_popular">{@code GET /subreddits/popular}</a>" included in the "read" scope */
    SUBREDDITS_POPULAR("GET /subreddits/popular", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_search">{@code GET /subreddits/search}</a>" included in the "read" scope */
    SUBREDDITS_SEARCH("GET /subreddits/search", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_where">{@code GET /subreddits/where}</a>" included in the "read" scope */
    SUBREDDITS_WHERE("GET /subreddits/where", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_top">{@code GET /top}</a>" included in the "read" scope */
    TOP("GET /top", "read"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_user_%7Busername%7D_about.json">{@code GET /user/{username}/about.json}</a>" included in the "read" scope */
    USER_USERNAME_ABOUT("GET /user/{username}/about.json", "read"),

    ///////// report /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_report">{@code POST /api/live/{thread}/report}</a>" included in the "report" scope */
    LIVE_THREAD_REPORT("POST /api/live/{thread}/report", "report"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_report">{@code POST /api/report}</a>" included in the "report" scope */
    REPORT("POST /api/report", "report"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_hide">{@code POST /api/hide}</a>" included in the "report" scope */
    HIDE("POST /api/hide", "report"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_unhide">{@code POST /api/unhide}</a>" included in the "report" scope */
    UNHIDE("POST /api/unhide", "report"),

    ///////// save /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_saved_categories.json">{@code POST /api/saved_categories.json}</a>" included in the "save" scope */
    SAVED_CATEGORIES("POST /api/saved_categories.json", "save"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_store_visits">{@code POST /api/store_visits}</a>" included in the "save" scope */
    STORE_VISITS("POST /api/store_visits", "save"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_save">{@code POST /api/save}</a>" included in the "save" scope */
    SAVE("POST /api/save", "save"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_unsave">{@code POST /api/unsave}</a>" included in the "save" scope */
    UNSAVE("POST /api/unsave", "save"),

    ///////// submit /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_create">{@code POST /api/live/create}</a>" included in the "submit" scope */
    LIVE_CREATE("POST /api/live/create", "submit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_live_%7Bthread%7D_update">{@code POST /api/live/{thread}/update}</a>" included in the "submit" scope */
    LIVE_THREAD_UPDATE("POST /api/live/{thread}/update", "submit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_comment">{@code POST /api/comment}</a>" included in the "submit" scope */
    COMMENT("POST /api/comment", "submit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_submit">{@code POST /api/submit}</a>" included in the "submit" scope */
    SUBMIT("POST /api/submit", "submit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_api_submit_text.json">{@code GET /api/submit_text.json}</a>" included in the "submit" scope */
    SUBMIT_TEXT("GET /api/submit_text.json", "submit"),

    ///////// subscribe /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#DELETE_api_filter_%7Bfilterpath%7D">{@code DELETE /api/filter/{filterpath}}</a>" included in the "subscribe" scope */
    FILTER_FILTERPATH_DELETE("DELETE /api/filter/{filterpath}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_filter_%7Bfilterpath%7D">{@code POST /api/filter/{filterpath}}</a>" included in the "subscribe" scope */
    FILTER_FILTERPATH_POST("POST /api/filter/{filterpath}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#PUT_api_filter_%7Bfilterpath%7D">{@code PUT /api/filter/{filterpath}}</a>" included in the "subscribe" scope */
    FILTER_FILTERPATH_PUT("PUT /api/filter/{filterpath}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#DELETE_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D">{@code DELETE /api/filter/{filterpath}/r/{srname}}</a>" included in the "subscribe" scope */
    FILTER_FILTERPATH_R_SRNAME_DELETE("DELETE /api/filter/{filterpath}/r/{srname}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#PUT_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D">{@code PUT /api/filter/{filterpath}/r/{srname}}</a>" included in the "subscribe" scope */
    FILTER_FILTERPATH_R_SRNAME_PUT("PUT /api/filter/{filterpath}/r/{srname}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#DELETE_api_multi_%7Bmultipath%7D">{@code DELETE /api/multi/{multipath}}</a>" included in the "subscribe" scope */
    MULTI_MULTIPATH_DELETE("DELETE /api/multi/{multipath}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D">{@code POST /api/multi/{multipath}}</a>" included in the "subscribe" scope */
    MULTI_MULTIPATH_POST("POST /api/multi/{multipath}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D">{@code PUT /api/multi/{multipath}}</a>" included in the "subscribe" scope */
    MULTI_MULTIPATH_PUT("PUT /api/multi/{multipath}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D_copy">{@code POST /api/multi/{multipath}/copy}</a>" included in the "subscribe" scope */
    MULTI_MULTIPATH_COPY("POST /api/multi/{multipath}/copy", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#DELETE_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D">{@code DELETE /api/multi/{multipath}/r/{srname}}</a>" included in the "subscribe" scope */
    MULTI_MULTIPATH_R_SRNAME_DELETE("DELETE /api/multi/{multipath}/r/{srname}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#PUT_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D">{@code PUT /api/multi/{multipath}/r/{srname}}</a>" included in the "subscribe" scope */
    MULTI_MULTIPATH_R_SRNAME_PUT("PUT /api/multi/{multipath}/r/{srname}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_multi_%7Bmultipath%7D_rename">{@code POST /api/multi/{multipath}/rename}</a>" included in the "subscribe" scope */
    MULTI_MULTIPATH_RENAME("POST /api/multi/{multipath}/rename", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_subscribe">{@code POST /api/subscribe}</a>" included in the "subscribe" scope */
    SUBSCRIBE("POST /api/subscribe", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#DELETE_api_v1_me_friends_%7Busername%7D">{@code DELETE /api/v1/me/friends/{username}}</a>" included in the "subscribe" scope */
    OAUTH_ME_FRIENDS_USERNAME_DELETE("DELETE /api/v1/me/friends/{username}", "subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#PUT_api_v1_me_friends_%7Busername%7D">{@code PUT /api/v1/me/friends/{username}}</a>" included in the "subscribe" scope */
    OAUTH_ME_FRIENDS_USERNAME_PUT("PUT /api/v1/me/friends/{username}", "subscribe"),

    ///////// vote /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_vote">{@code POST /api/vote}</a>" included in the "vote" scope */
    VOTE("POST /api/vote", "vote"),

    ///////// wikiedit /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_edit">{@code POST /api/wiki/edit}</a>" included in the "wikiedit" scope */
    WIKI_EDIT("POST /api/wiki/edit", "wikiedit"),

    ///////// wikiread /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_wiki_discussions_%7Bpage%7D">{@code GET /wiki/discussions/{page}}</a>" included in the "wikiread" scope */
    WIKI_DISCUSSIONS_PAGE("GET /wiki/discussions/{page}", "wikiread"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_wiki_revisions">{@code GET /wiki/revisions}</a>" included in the "wikiread" scope */
    WIKI_REVISIONS("GET /wiki/revisions", "wikiread"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_wiki_revisions_%7Bpage%7D">{@code GET /wiki/revisions/{page}}</a>" included in the "wikiread" scope */
    WIKI_REVISIONS_PAGE("GET /wiki/revisions/{page}", "wikiread"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_wiki_pages">{@code GET /wiki/pages}</a>" included in the "wikiread" scope */
    WIKI_PAGES("GET /wiki/pages", "wikiread"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api/oauth#GET_wiki_%7Bpage%7D">{@code GET /wiki/{page}}</a>" included in the "wikiread" scope */
    WIKI_PAGE("GET /wiki/{page}", "wikiread");

    private final net.dean.jraw.Endpoint endpoint;
    private final String scope;

    private Endpoints(String requestDescriptor, String scope) {
        this.endpoint = new net.dean.jraw.Endpoint(requestDescriptor);
        this.scope = scope;
    }

    /**
      * Gets the Endpoint object associated with this enumeration
      * @return The Endpoint object
      */
    public final net.dean.jraw.Endpoint getEndpoint() {
        return endpoint;
    }

    /**
      * Gets this Endpoint's OAuth scope
      */
    public final String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return endpoint.toString();
    }
}
