package net.dean.jraw;

/* This class is updated by running ./gradlew endpoints:update. Do not modify directly */
/** This class is an automatically generated enumeration of Reddit's API endpoints */
@SuppressWarnings("unused")
public enum Endpoints {

    ///////// account /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_clear_sessions">{@code POST /api/clear_sessions}</a>" in the "account" category */
    CLEAR_SESSIONS("POST /api/clear_sessions"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_delete_user">{@code POST /api/delete_user}</a>" in the "account" category */
    DELETE_USER("POST /api/delete_user"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_register">{@code POST /api/register}</a>" in the "account" category */
    REGISTER("POST /api/register"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_set_force_https">{@code POST /api/set_force_https}</a>" in the "account" category */
    SET_FORCE_HTTPS("POST /api/set_force_https"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_update">{@code POST /api/update}</a>" in the "account" category */
    UPDATE("POST /api/update"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_update_email">{@code POST /api/update_email}</a>" in the "account" category */
    UPDATE_EMAIL("POST /api/update_email"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_update_password">{@code POST /api/update_password}</a>" in the "account" category */
    UPDATE_PASSWORD("POST /api/update_password"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_v1_me">{@code GET /api/v1/me}</a>" in the "account" category */
    OAUTH_ME("GET /api/v1/me"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_v1_me_blocked">{@code GET /api/v1/me/blocked}</a>" in the "account" category */
    OAUTH_ME_BLOCKED("GET /api/v1/me/blocked"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_v1_me_friends">{@code GET /api/v1/me/friends}</a>" in the "account" category */
    OAUTH_ME_FRIENDS("GET /api/v1/me/friends"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_v1_me_karma">{@code GET /api/v1/me/karma}</a>" in the "account" category */
    OAUTH_ME_KARMA("GET /api/v1/me/karma"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#PATCH_api_v1_me_prefs">{@code PATCH /api/v1/me/prefs}</a>" in the "account" category */
    OAUTH_ME_PREFS("PATCH /api/v1/me/prefs"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_v1_me_trophies">{@code GET /api/v1/me/trophies}</a>" in the "account" category */
    OAUTH_ME_TROPHIES("GET /api/v1/me/trophies"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_prefs_blocked">{@code GET /prefs/blocked}</a>" in the "account" category */
    PREFS_BLOCKED("GET /prefs/blocked"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_prefs_friends">{@code GET /prefs/friends}</a>" in the "account" category */
    PREFS_FRIENDS("GET /prefs/friends"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_prefs_%7Bwhere%7D">{@code GET /prefs/{where}}</a>" in the "account" category */
    PREFS_WHERE("GET /prefs/{where}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_login">{@code POST /api/login}</a>" in the "account" category */
    LOGIN("POST /api/login"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_me.json">{@code GET /api/me.json}</a>" in the "account" category */
    ME("GET /api/me.json"),

    ///////// apps /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_deleteapp">{@code POST /api/deleteapp}</a>" in the "apps" category */
    DELETEAPP("POST /api/deleteapp"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_revokeapp">{@code POST /api/revokeapp}</a>" in the "apps" category */
    REVOKEAPP("POST /api/revokeapp"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_setappicon">{@code POST /api/setappicon}</a>" in the "apps" category */
    SETAPPICON("POST /api/setappicon"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_updateapp">{@code POST /api/updateapp}</a>" in the "apps" category */
    UPDATEAPP("POST /api/updateapp"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_adddeveloper">{@code POST /api/adddeveloper}</a>" in the "apps" category */
    ADDDEVELOPER("POST /api/adddeveloper"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_removedeveloper">{@code POST /api/removedeveloper}</a>" in the "apps" category */
    REMOVEDEVELOPER("POST /api/removedeveloper"),

    ///////// captcha /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_needs_captcha.json">{@code GET /api/needs_captcha.json}</a>" in the "captcha" category */
    NEEDS_CAPTCHA("GET /api/needs_captcha.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_new_captcha">{@code POST /api/new_captcha}</a>" in the "captcha" category */
    NEW_CAPTCHA("POST /api/new_captcha"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_captcha_%7Biden%7D">{@code GET /captcha/{iden}}</a>" in the "captcha" category */
    CAPTCHA_IDEN("GET /captcha/{iden}"),

    ///////// flair /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_clearflairtemplates">{@code POST /api/clearflairtemplates}</a>" in the "flair" category */
    CLEARFLAIRTEMPLATES("POST /api/clearflairtemplates"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_deleteflair">{@code POST /api/deleteflair}</a>" in the "flair" category */
    DELETEFLAIR("POST /api/deleteflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_deleteflairtemplate">{@code POST /api/deleteflairtemplate}</a>" in the "flair" category */
    DELETEFLAIRTEMPLATE("POST /api/deleteflairtemplate"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_flair">{@code POST /api/flair}</a>" in the "flair" category */
    FLAIR("POST /api/flair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_flairconfig">{@code POST /api/flairconfig}</a>" in the "flair" category */
    FLAIRCONFIG("POST /api/flairconfig"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_flaircsv">{@code POST /api/flaircsv}</a>" in the "flair" category */
    FLAIRCSV("POST /api/flaircsv"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_flairlist">{@code GET /api/flairlist}</a>" in the "flair" category */
    FLAIRLIST("GET /api/flairlist"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_flairselector">{@code POST /api/flairselector}</a>" in the "flair" category */
    FLAIRSELECTOR("POST /api/flairselector"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_flairtemplate">{@code POST /api/flairtemplate}</a>" in the "flair" category */
    FLAIRTEMPLATE("POST /api/flairtemplate"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_selectflair">{@code POST /api/selectflair}</a>" in the "flair" category */
    SELECTFLAIR("POST /api/selectflair"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_setflairenabled">{@code POST /api/setflairenabled}</a>" in the "flair" category */
    SETFLAIRENABLED("POST /api/setflairenabled"),

    ///////// links & comments /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_info">{@code GET /api/info}</a>" in the "links &amp; comments" category */
    INFO("GET /api/info"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_morechildren">{@code POST /api/morechildren}</a>" in the "links &amp; comments" category */
    MORECHILDREN("POST /api/morechildren"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_report">{@code POST /api/report}</a>" in the "links &amp; comments" category */
    REPORT("POST /api/report"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_saved_categories.json">{@code GET /api/saved_categories.json}</a>" in the "links &amp; comments" category */
    SAVED_CATEGORIES("GET /api/saved_categories.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_set_contest_mode">{@code POST /api/set_contest_mode}</a>" in the "links &amp; comments" category */
    SET_CONTEST_MODE("POST /api/set_contest_mode"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_set_subreddit_sticky">{@code POST /api/set_subreddit_sticky}</a>" in the "links &amp; comments" category */
    SET_SUBREDDIT_STICKY("POST /api/set_subreddit_sticky"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_store_visits">{@code POST /api/store_visits}</a>" in the "links &amp; comments" category */
    STORE_VISITS("POST /api/store_visits"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_comment">{@code POST /api/comment}</a>" in the "links &amp; comments" category */
    COMMENT("POST /api/comment"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_del">{@code POST /api/del}</a>" in the "links &amp; comments" category */
    DEL("POST /api/del"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_editusertext">{@code POST /api/editusertext}</a>" in the "links &amp; comments" category */
    EDITUSERTEXT("POST /api/editusertext"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_hide">{@code POST /api/hide}</a>" in the "links &amp; comments" category */
    HIDE("POST /api/hide"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_marknsfw">{@code POST /api/marknsfw}</a>" in the "links &amp; comments" category */
    MARKNSFW("POST /api/marknsfw"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_save">{@code POST /api/save}</a>" in the "links &amp; comments" category */
    SAVE("POST /api/save"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_sendreplies">{@code POST /api/sendreplies}</a>" in the "links &amp; comments" category */
    SENDREPLIES("POST /api/sendreplies"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_submit">{@code POST /api/submit}</a>" in the "links &amp; comments" category */
    SUBMIT("POST /api/submit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_unhide">{@code POST /api/unhide}</a>" in the "links &amp; comments" category */
    UNHIDE("POST /api/unhide"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_unmarknsfw">{@code POST /api/unmarknsfw}</a>" in the "links &amp; comments" category */
    UNMARKNSFW("POST /api/unmarknsfw"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_unsave">{@code POST /api/unsave}</a>" in the "links &amp; comments" category */
    UNSAVE("POST /api/unsave"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_vote">{@code POST /api/vote}</a>" in the "links &amp; comments" category */
    VOTE("POST /api/vote"),

    ///////// listings /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_by_id_%7Bnames%7D">{@code GET /by_id/{names}}</a>" in the "listings" category */
    BY_ID_NAMES("GET /by_id/{names}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_comments_%7Barticle%7D">{@code GET /comments/{article}}</a>" in the "listings" category */
    COMMENTS_ARTICLE("GET /comments/{article}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_controversial">{@code GET /controversial}</a>" in the "listings" category */
    CONTROVERSIAL("GET /controversial"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_hot">{@code GET /hot}</a>" in the "listings" category */
    HOT("GET /hot"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_new">{@code GET /new}</a>" in the "listings" category */
    NEW("GET /new"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_random">{@code GET /random}</a>" in the "listings" category */
    RANDOM("GET /random"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_sort">{@code GET /sort}</a>" in the "listings" category */
    SORT("GET /sort"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_top">{@code GET /top}</a>" in the "listings" category */
    TOP("GET /top"),

    ///////// live threads /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_create">{@code POST /api/live/create}</a>" in the "live threads" category */
    LIVE_CREATE("POST /api/live/create"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_accept_contributor_invite">{@code POST /api/live/{thread}/accept_contributor_invite}</a>" in the "live threads" category */
    LIVE_THREAD_ACCEPT_CONTRIBUTOR_INVITE("POST /api/live/{thread}/accept_contributor_invite"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_close_thread">{@code POST /api/live/{thread}/close_thread}</a>" in the "live threads" category */
    LIVE_THREAD_CLOSE_THREAD("POST /api/live/{thread}/close_thread"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_delete_update">{@code POST /api/live/{thread}/delete_update}</a>" in the "live threads" category */
    LIVE_THREAD_DELETE_UPDATE("POST /api/live/{thread}/delete_update"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_edit">{@code POST /api/live/{thread}/edit}</a>" in the "live threads" category */
    LIVE_THREAD_EDIT("POST /api/live/{thread}/edit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_invite_contributor">{@code POST /api/live/{thread}/invite_contributor}</a>" in the "live threads" category */
    LIVE_THREAD_INVITE_CONTRIBUTOR("POST /api/live/{thread}/invite_contributor"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_leave_contributor">{@code POST /api/live/{thread}/leave_contributor}</a>" in the "live threads" category */
    LIVE_THREAD_LEAVE_CONTRIBUTOR("POST /api/live/{thread}/leave_contributor"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_report">{@code POST /api/live/{thread}/report}</a>" in the "live threads" category */
    LIVE_THREAD_REPORT("POST /api/live/{thread}/report"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_rm_contributor">{@code POST /api/live/{thread}/rm_contributor}</a>" in the "live threads" category */
    LIVE_THREAD_RM_CONTRIBUTOR("POST /api/live/{thread}/rm_contributor"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_rm_contributor_invite">{@code POST /api/live/{thread}/rm_contributor_invite}</a>" in the "live threads" category */
    LIVE_THREAD_RM_CONTRIBUTOR_INVITE("POST /api/live/{thread}/rm_contributor_invite"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_set_contributor_permissions">{@code POST /api/live/{thread}/set_contributor_permissions}</a>" in the "live threads" category */
    LIVE_THREAD_SET_CONTRIBUTOR_PERMISSIONS("POST /api/live/{thread}/set_contributor_permissions"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_strike_update">{@code POST /api/live/{thread}/strike_update}</a>" in the "live threads" category */
    LIVE_THREAD_STRIKE_UPDATE("POST /api/live/{thread}/strike_update"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_live_%7Bthread%7D_update">{@code POST /api/live/{thread}/update}</a>" in the "live threads" category */
    LIVE_THREAD_UPDATE("POST /api/live/{thread}/update"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_live_%7Bthread%7D_contributors.json">{@code GET /live/{thread}/contributors.json}</a>" in the "live threads" category */
    LIVE_THREAD_CONTRIBUTORS("GET /live/{thread}/contributors.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_live_%7Bthread%7D_discussions">{@code GET /live/{thread}/discussions}</a>" in the "live threads" category */
    LIVE_THREAD_DISCUSSIONS("GET /live/{thread}/discussions"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_live_%7Bthread%7D">{@code GET /live/{thread}}</a>" in the "live threads" category */
    LIVE_THREAD("GET /live/{thread}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_live_%7Bthread%7D_about.json">{@code GET /live/{thread}/about.json}</a>" in the "live threads" category */
    LIVE_THREAD_ABOUT("GET /live/{thread}/about.json"),

    ///////// moderation /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_edited">{@code GET /about/edited}</a>" in the "moderation" category */
    ABOUT_EDITED("GET /about/edited"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_log">{@code GET /about/log}</a>" in the "moderation" category */
    ABOUT_LOG("GET /about/log"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_modqueue">{@code GET /about/modqueue}</a>" in the "moderation" category */
    ABOUT_MODQUEUE("GET /about/modqueue"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_reports">{@code GET /about/reports}</a>" in the "moderation" category */
    ABOUT_REPORTS("GET /about/reports"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_spam">{@code GET /about/spam}</a>" in the "moderation" category */
    ABOUT_SPAM("GET /about/spam"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_unmoderated">{@code GET /about/unmoderated}</a>" in the "moderation" category */
    ABOUT_UNMODERATED("GET /about/unmoderated"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_%7Blocation%7D">{@code GET /about/{location}}</a>" in the "moderation" category */
    ABOUT_LOCATION("GET /about/{location}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_accept_moderator_invite">{@code POST /api/accept_moderator_invite}</a>" in the "moderation" category */
    ACCEPT_MODERATOR_INVITE("POST /api/accept_moderator_invite"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_approve">{@code POST /api/approve}</a>" in the "moderation" category */
    APPROVE("POST /api/approve"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_distinguish">{@code POST /api/distinguish}</a>" in the "moderation" category */
    DISTINGUISH("POST /api/distinguish"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_ignore_reports">{@code POST /api/ignore_reports}</a>" in the "moderation" category */
    IGNORE_REPORTS("POST /api/ignore_reports"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_leavecontributor">{@code POST /api/leavecontributor}</a>" in the "moderation" category */
    LEAVECONTRIBUTOR("POST /api/leavecontributor"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_leavemoderator">{@code POST /api/leavemoderator}</a>" in the "moderation" category */
    LEAVEMODERATOR("POST /api/leavemoderator"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_remove">{@code POST /api/remove}</a>" in the "moderation" category */
    REMOVE("POST /api/remove"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_unignore_reports">{@code POST /api/unignore_reports}</a>" in the "moderation" category */
    UNIGNORE_REPORTS("POST /api/unignore_reports"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_stylesheet">{@code GET /stylesheet}</a>" in the "moderation" category */
    STYLESHEET("GET /stylesheet"),

    ///////// multis /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#DELETE_api_filter_%7Bfilterpath%7D">{@code DELETE /api/filter/{filterpath}}</a>" in the "multis" category */
    FILTER_FILTERPATH_DELETE("DELETE /api/filter/{filterpath}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_filter_%7Bfilterpath%7D">{@code GET /api/filter/{filterpath}}</a>" in the "multis" category */
    FILTER_FILTERPATH_GET("GET /api/filter/{filterpath}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_filter_%7Bfilterpath%7D">{@code POST /api/filter/{filterpath}}</a>" in the "multis" category */
    FILTER_FILTERPATH_POST("POST /api/filter/{filterpath}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#PUT_api_filter_%7Bfilterpath%7D">{@code PUT /api/filter/{filterpath}}</a>" in the "multis" category */
    FILTER_FILTERPATH_PUT("PUT /api/filter/{filterpath}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#DELETE_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D">{@code DELETE /api/filter/{filterpath}/r/{srname}}</a>" in the "multis" category */
    FILTER_FILTERPATH_R_SRNAME_DELETE("DELETE /api/filter/{filterpath}/r/{srname}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D">{@code GET /api/filter/{filterpath}/r/{srname}}</a>" in the "multis" category */
    FILTER_FILTERPATH_R_SRNAME_GET("GET /api/filter/{filterpath}/r/{srname}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#PUT_api_filter_%7Bfilterpath%7D_r_%7Bsrname%7D">{@code PUT /api/filter/{filterpath}/r/{srname}}</a>" in the "multis" category */
    FILTER_FILTERPATH_R_SRNAME_PUT("PUT /api/filter/{filterpath}/r/{srname}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_multi_mine">{@code GET /api/multi/mine}</a>" in the "multis" category */
    MULTI_MINE("GET /api/multi/mine"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#DELETE_api_multi_%7Bmultipath%7D">{@code DELETE /api/multi/{multipath}}</a>" in the "multis" category */
    MULTI_MULTIPATH_DELETE("DELETE /api/multi/{multipath}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_multi_%7Bmultipath%7D">{@code GET /api/multi/{multipath}}</a>" in the "multis" category */
    MULTI_MULTIPATH_GET("GET /api/multi/{multipath}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_multi_%7Bmultipath%7D">{@code POST /api/multi/{multipath}}</a>" in the "multis" category */
    MULTI_MULTIPATH_POST("POST /api/multi/{multipath}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#PUT_api_multi_%7Bmultipath%7D">{@code PUT /api/multi/{multipath}}</a>" in the "multis" category */
    MULTI_MULTIPATH_PUT("PUT /api/multi/{multipath}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_multi_%7Bmultipath%7D_copy">{@code POST /api/multi/{multipath}/copy}</a>" in the "multis" category */
    MULTI_MULTIPATH_COPY("POST /api/multi/{multipath}/copy"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_multi_%7Bmultipath%7D_description">{@code GET /api/multi/{multipath}/description}</a>" in the "multis" category */
    MULTI_MULTIPATH_DESCRIPTION_GET("GET /api/multi/{multipath}/description"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#PUT_api_multi_%7Bmultipath%7D_description">{@code PUT /api/multi/{multipath}/description}</a>" in the "multis" category */
    MULTI_MULTIPATH_DESCRIPTION_PUT("PUT /api/multi/{multipath}/description"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#DELETE_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D">{@code DELETE /api/multi/{multipath}/r/{srname}}</a>" in the "multis" category */
    MULTI_MULTIPATH_R_SRNAME_DELETE("DELETE /api/multi/{multipath}/r/{srname}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D">{@code GET /api/multi/{multipath}/r/{srname}}</a>" in the "multis" category */
    MULTI_MULTIPATH_R_SRNAME_GET("GET /api/multi/{multipath}/r/{srname}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#PUT_api_multi_%7Bmultipath%7D_r_%7Bsrname%7D">{@code PUT /api/multi/{multipath}/r/{srname}}</a>" in the "multis" category */
    MULTI_MULTIPATH_R_SRNAME_PUT("PUT /api/multi/{multipath}/r/{srname}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_multi_%7Bmultipath%7D_rename">{@code POST /api/multi/{multipath}/rename}</a>" in the "multis" category */
    MULTI_MULTIPATH_RENAME("POST /api/multi/{multipath}/rename"),

    ///////// private messages /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_block">{@code POST /api/block}</a>" in the "private messages" category */
    BLOCK("POST /api/block"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_compose">{@code POST /api/compose}</a>" in the "private messages" category */
    COMPOSE("POST /api/compose"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_read_message">{@code POST /api/read_message}</a>" in the "private messages" category */
    READ_MESSAGE("POST /api/read_message"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_unread_message">{@code POST /api/unread_message}</a>" in the "private messages" category */
    UNREAD_MESSAGE("POST /api/unread_message"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_message_inbox">{@code POST /message/inbox}</a>" in the "private messages" category */
    MESSAGE_INBOX("POST /message/inbox"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_message_sent">{@code GET /message/sent}</a>" in the "private messages" category */
    MESSAGE_SENT("GET /message/sent"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_message_unread">{@code GET /message/unread}</a>" in the "private messages" category */
    MESSAGE_UNREAD("GET /message/unread"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_message_%7Bwhere%7D">{@code GET /message/{where}}</a>" in the "private messages" category */
    MESSAGE_WHERE("GET /message/{where}"),

    ///////// reddit gold /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_v1_gold_gild_%7Bfullname%7D">{@code POST /api/v1/gold/gild/{fullname}}</a>" in the "reddit gold" category */
    OAUTH_GOLD_GILD_FULLNAME("POST /api/v1/gold/gild/{fullname}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_v1_gold_give_%7Busername%7D">{@code POST /api/v1/gold/give/{username}}</a>" in the "reddit gold" category */
    OAUTH_GOLD_GIVE_USERNAME("POST /api/v1/gold/give/{username}"),

    ///////// search /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_search">{@code GET /search}</a>" in the "search" category */
    SEARCH("GET /search"),

    ///////// subreddits /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_banned">{@code GET /about/banned}</a>" in the "subreddits" category */
    ABOUT_BANNED("GET /about/banned"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_contributors">{@code GET /about/contributors}</a>" in the "subreddits" category */
    ABOUT_CONTRIBUTORS("GET /about/contributors"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_moderators">{@code GET /about/moderators}</a>" in the "subreddits" category */
    ABOUT_MODERATORS("GET /about/moderators"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_wikibanned">{@code GET /about/wikibanned}</a>" in the "subreddits" category */
    ABOUT_WIKIBANNED("GET /about/wikibanned"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_wikicontributors">{@code GET /about/wikicontributors}</a>" in the "subreddits" category */
    ABOUT_WIKICONTRIBUTORS("GET /about/wikicontributors"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_about_%7Bwhere%7D">{@code GET /about/{where}}</a>" in the "subreddits" category */
    ABOUT_WHERE("GET /about/{where}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_delete_sr_header">{@code POST /api/delete_sr_header}</a>" in the "subreddits" category */
    DELETE_SR_HEADER("POST /api/delete_sr_header"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_delete_sr_img">{@code POST /api/delete_sr_img}</a>" in the "subreddits" category */
    DELETE_SR_IMG("POST /api/delete_sr_img"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_recommend_sr_%7Bsrnames%7D">{@code GET /api/recommend/sr/{srnames}}</a>" in the "subreddits" category */
    RECOMMEND_SR_SRNAMES("GET /api/recommend/sr/{srnames}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_site_admin">{@code POST /api/site_admin}</a>" in the "subreddits" category */
    SITE_ADMIN("POST /api/site_admin"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_subreddit_stylesheet">{@code POST /api/subreddit_stylesheet}</a>" in the "subreddits" category */
    SUBREDDIT_STYLESHEET("POST /api/subreddit_stylesheet"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_upload_sr_img">{@code POST /api/upload_sr_img}</a>" in the "subreddits" category */
    UPLOAD_SR_IMG("POST /api/upload_sr_img"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_r_%7Bsubreddit%7D_about_edit.json">{@code GET /r/{subreddit}/about/edit.json}</a>" in the "subreddits" category */
    SUBREDDIT_ABOUT_EDIT("GET /r/{subreddit}/about/edit.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_subreddits_search">{@code GET /subreddits/search}</a>" in the "subreddits" category */
    SUBREDDITS_SEARCH("GET /subreddits/search"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_search_reddit_names.json">{@code POST /api/search_reddit_names.json}</a>" in the "subreddits" category */
    SEARCH_REDDIT_NAMES("POST /api/search_reddit_names.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_submit_text.json">{@code GET /api/submit_text.json}</a>" in the "subreddits" category */
    SUBMIT_TEXT("GET /api/submit_text.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_subreddits_by_topic.json">{@code GET /api/subreddits_by_topic.json}</a>" in the "subreddits" category */
    SUBREDDITS_BY_TOPIC("GET /api/subreddits_by_topic.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_subscribe">{@code POST /api/subscribe}</a>" in the "subreddits" category */
    SUBSCRIBE("POST /api/subscribe"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_r_%7Bsubreddit%7D_about.json">{@code GET /r/{subreddit}/about.json}</a>" in the "subreddits" category */
    SUBREDDIT_ABOUT("GET /r/{subreddit}/about.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_subreddits_mine_contributor">{@code GET /subreddits/mine/contributor}</a>" in the "subreddits" category */
    SUBREDDITS_MINE_CONTRIBUTOR("GET /subreddits/mine/contributor"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_subreddits_mine_moderator">{@code GET /subreddits/mine/moderator}</a>" in the "subreddits" category */
    SUBREDDITS_MINE_MODERATOR("GET /subreddits/mine/moderator"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_subreddits_mine_subscriber">{@code GET /subreddits/mine/subscriber}</a>" in the "subreddits" category */
    SUBREDDITS_MINE_SUBSCRIBER("GET /subreddits/mine/subscriber"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_subreddits_mine_%7Bwhere%7D">{@code GET /subreddits/mine/{where}}</a>" in the "subreddits" category */
    SUBREDDITS_MINE_WHERE("GET /subreddits/mine/{where}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_subreddits_new">{@code GET /subreddits/new}</a>" in the "subreddits" category */
    SUBREDDITS_NEW("GET /subreddits/new"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_subreddits_popular">{@code GET /subreddits/popular}</a>" in the "subreddits" category */
    SUBREDDITS_POPULAR("GET /subreddits/popular"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_subreddits_%7Bwhere%7D">{@code GET /subreddits/{where}}</a>" in the "subreddits" category */
    SUBREDDITS_WHERE("GET /subreddits/{where}"),

    ///////// users /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_friend">{@code POST /api/friend}</a>" in the "users" category */
    FRIEND("POST /api/friend"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_setpermissions">{@code POST /api/setpermissions}</a>" in the "users" category */
    SETPERMISSIONS("POST /api/setpermissions"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_unfriend">{@code POST /api/unfriend}</a>" in the "users" category */
    UNFRIEND("POST /api/unfriend"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#DELETE_api_v1_me_friends_%7Busername%7D">{@code DELETE /api/v1/me/friends/{username}}</a>" in the "users" category */
    OAUTH_ME_FRIENDS_USERNAME_DELETE("DELETE /api/v1/me/friends/{username}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_v1_me_friends_%7Busername%7D">{@code GET /api/v1/me/friends/{username}}</a>" in the "users" category */
    OAUTH_ME_FRIENDS_USERNAME_GET("GET /api/v1/me/friends/{username}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#PUT_api_v1_me_friends_%7Busername%7D">{@code PUT /api/v1/me/friends/{username}}</a>" in the "users" category */
    OAUTH_ME_FRIENDS_USERNAME_PUT("PUT /api/v1/me/friends/{username}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_v1_user_%7Busername%7D_trophies">{@code GET /api/v1/user/{username}/trophies}</a>" in the "users" category */
    OAUTH_USER_USERNAME_TROPHIES("GET /api/v1/user/{username}/trophies"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_api_username_available.json">{@code GET /api/username_available.json}</a>" in the "users" category */
    USERNAME_AVAILABLE("GET /api/username_available.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_about.json">{@code GET /user/{username}/about.json}</a>" in the "users" category */
    USER_USERNAME_ABOUT("GET /user/{username}/about.json"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_comments">{@code GET /user/{username}/comments}</a>" in the "users" category */
    USER_USERNAME_COMMENTS("GET /user/{username}/comments"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_disliked">{@code GET /user/{username}/disliked}</a>" in the "users" category */
    USER_USERNAME_DISLIKED("GET /user/{username}/disliked"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_gilded">{@code GET /user/{username}/gilded}</a>" in the "users" category */
    USER_USERNAME_GILDED("GET /user/{username}/gilded"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_hidden">{@code GET /user/{username}/hidden}</a>" in the "users" category */
    USER_USERNAME_HIDDEN("GET /user/{username}/hidden"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_liked">{@code GET /user/{username}/liked}</a>" in the "users" category */
    USER_USERNAME_LIKED("GET /user/{username}/liked"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_overview">{@code GET /user/{username}/overview}</a>" in the "users" category */
    USER_USERNAME_OVERVIEW("GET /user/{username}/overview"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_saved">{@code GET /user/{username}/saved}</a>" in the "users" category */
    USER_USERNAME_SAVED("GET /user/{username}/saved"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_submitted">{@code GET /user/{username}/submitted}</a>" in the "users" category */
    USER_USERNAME_SUBMITTED("GET /user/{username}/submitted"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_user_%7Busername%7D_%7Bwhere%7D">{@code GET /user/{username}/{where}}</a>" in the "users" category */
    USER_USERNAME_WHERE("GET /user/{username}/{where}"),

    ///////// wiki /////////
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_wiki_alloweditor_act">{@code POST /api/wiki/alloweditor/act}</a>" in the "wiki" category */
    WIKI_ALLOWEDITOR_ACT("POST /api/wiki/alloweditor/act"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_wiki_alloweditor_add">{@code POST /api/wiki/alloweditor/add}</a>" in the "wiki" category */
    WIKI_ALLOWEDITOR_ADD("POST /api/wiki/alloweditor/add"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_wiki_alloweditor_del">{@code POST /api/wiki/alloweditor/del}</a>" in the "wiki" category */
    WIKI_ALLOWEDITOR_DEL("POST /api/wiki/alloweditor/del"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_wiki_edit">{@code POST /api/wiki/edit}</a>" in the "wiki" category */
    WIKI_EDIT("POST /api/wiki/edit"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_wiki_hide">{@code POST /api/wiki/hide}</a>" in the "wiki" category */
    WIKI_HIDE("POST /api/wiki/hide"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_api_wiki_revert">{@code POST /api/wiki/revert}</a>" in the "wiki" category */
    WIKI_REVERT("POST /api/wiki/revert"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_wiki_discussions_%7Bpage%7D">{@code GET /wiki/discussions/{page}}</a>" in the "wiki" category */
    WIKI_DISCUSSIONS_PAGE("GET /wiki/discussions/{page}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_wiki_revisions">{@code GET /wiki/revisions}</a>" in the "wiki" category */
    WIKI_REVISIONS("GET /wiki/revisions"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#POST_wiki_revisions_%7Bpage%7D">{@code POST /wiki/revisions/{page}}</a>" in the "wiki" category */
    WIKI_REVISIONS_PAGE("POST /wiki/revisions/{page}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_wiki_settings_%7Bpage%7D">{@code GET /wiki/settings/{page}}</a>" in the "wiki" category */
    WIKI_SETTINGS_PAGE("GET /wiki/settings/{page}"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_wiki_pages">{@code GET /wiki/pages}</a>" in the "wiki" category */
    WIKI_PAGES("GET /wiki/pages"),
    /** Represents the endpoint "<a href="https://www.reddit.com/dev/api#GET_wiki_%7Bpage%7D">{@code GET /wiki/{page}}</a>" in the "wiki" category */
    WIKI_PAGE("GET /wiki/{page}");

    private final net.dean.jraw.Endpoint endpoint;

    private Endpoints(String requestDescriptor) {
        this.endpoint = new Endpoint(requestDescriptor);
    }

    /**
      * Gets the Endpoint object associated with this enumeration
      * @return The Endpoint object
      */
    public final net.dean.jraw.Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public java.lang.String toString() {
        return endpoint.toString();
    }
}
