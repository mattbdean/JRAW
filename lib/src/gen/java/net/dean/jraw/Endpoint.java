package net.dean.jraw;

import static net.dean.jraw.Endpoint.Constant.OPTIONAL_SUBREDDIT;

/**
 * This is a dynamically generated enumeration of all reddit API endpoints.
 *
 * For JRAW developers: this class should not be edited by hand. This class can be regenerated through the {@code :meta:update} Gradle task. */
public enum Endpoint {
    /**
     * Represents the endpoint {@code POST /api/comment}. Requires OAuth scope 'any'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_comment">here</a> for more information */
    POST_COMMENT("POST", "/api/comment", "any"),

    /**
     * Represents the endpoint {@code POST /api/friend}. Requires OAuth scope 'any'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_friend">here</a> for more information */
    POST_FRIEND("POST", OPTIONAL_SUBREDDIT + "/api/friend", "any"),

    /**
     * Represents the endpoint {@code GET /api/needs_captcha}. Requires OAuth scope 'any'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_needs_captcha">here</a> for more information */
    GET_NEEDS_CAPTCHA("GET", "/api/needs_captcha", "any"),

    /**
     * Represents the endpoint {@code POST /api/unfriend}. Requires OAuth scope 'any'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unfriend">here</a> for more information */
    POST_UNFRIEND("POST", OPTIONAL_SUBREDDIT + "/api/unfriend", "any"),

    /**
     * Represents the endpoint {@code GET /api/username_available}. Requires OAuth scope 'any'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_username_available">here</a> for more information */
    GET_USERNAME_AVAILABLE("GET", "/api/username_available", "any"),

    /**
     * Represents the endpoint {@code GET /api/v1/scopes}. Requires OAuth scope 'any'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_scopes">here</a> for more information */
    GET_SCOPES("GET", "/api/v1/scopes", "any"),

    /**
     * Represents the endpoint {@code POST /api/block_user}. Requires OAuth scope 'account'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_block_user">here</a> for more information */
    POST_BLOCK_USER("POST", "/api/block_user", "account"),

    /**
     * Represents the endpoint {@code PATCH /api/v1/me/prefs}. Requires OAuth scope 'account'. See <a href="https://www.reddit.com/dev/api/oauth#PATCH_api_v1_me_prefs">here</a> for more information */
    PATCH_ME_PREFS("PATCH", "/api/v1/me/prefs", "account"),

    /**
     * Represents the endpoint {@code POST /api/v1/gold/gild/{fullname}}. Requires OAuth scope 'creddits'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_gild_{fullname}">here</a> for more information */
    POST_GOLD_GILD_FULLNAME("POST", "/api/v1/gold/gild/{fullname}", "creddits"),

    /**
     * Represents the endpoint {@code POST /api/v1/gold/give/{username}}. Requires OAuth scope 'creddits'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_give_{username}">here</a> for more information */
    POST_GOLD_GIVE_USERNAME("POST", "/api/v1/gold/give/{username}", "creddits"),

    /**
     * Represents the endpoint {@code POST /api/del}. Requires OAuth scope 'edit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_del">here</a> for more information */
    POST_DEL("POST", "/api/del", "edit"),

    /**
     * Represents the endpoint {@code POST /api/editusertext}. Requires OAuth scope 'edit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_editusertext">here</a> for more information */
    POST_EDITUSERTEXT("POST", "/api/editusertext", "edit"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/delete_update}. Requires OAuth scope 'edit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_delete_update">here</a> for more information */
    POST_LIVE_THREAD_DELETE_UPDATE("POST", "/api/live/{thread}/delete_update", "edit"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/strike_update}. Requires OAuth scope 'edit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_strike_update">here</a> for more information */
    POST_LIVE_THREAD_STRIKE_UPDATE("POST", "/api/live/{thread}/strike_update", "edit"),

    /**
     * Represents the endpoint {@code POST /api/sendreplies}. Requires OAuth scope 'edit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_sendreplies">here</a> for more information */
    POST_SENDREPLIES("POST", "/api/sendreplies", "edit"),

    /**
     * Represents the endpoint {@code POST /api/flairselector}. Requires OAuth scope 'flair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_flairselector">here</a> for more information */
    POST_FLAIRSELECTOR("POST", OPTIONAL_SUBREDDIT + "/api/flairselector", "flair"),

    /**
     * Represents the endpoint {@code GET /api/link_flair}. Requires OAuth scope 'flair'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_link_flair">here</a> for more information */
    GET_LINK_FLAIR("GET", OPTIONAL_SUBREDDIT + "/api/link_flair", "flair"),

    /**
     * Represents the endpoint {@code GET /api/link_flair_v2}. Requires OAuth scope 'flair'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_link_flair_v2">here</a> for more information */
    GET_LINK_FLAIR_V2("GET", OPTIONAL_SUBREDDIT + "/api/link_flair_v2", "flair"),

    /**
     * Represents the endpoint {@code POST /api/selectflair}. Requires OAuth scope 'flair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_selectflair">here</a> for more information */
    POST_SELECTFLAIR("POST", OPTIONAL_SUBREDDIT + "/api/selectflair", "flair"),

    /**
     * Represents the endpoint {@code POST /api/setflairenabled}. Requires OAuth scope 'flair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_setflairenabled">here</a> for more information */
    POST_SETFLAIRENABLED("POST", OPTIONAL_SUBREDDIT + "/api/setflairenabled", "flair"),

    /**
     * Represents the endpoint {@code GET /api/user_flair}. Requires OAuth scope 'flair'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_user_flair">here</a> for more information */
    GET_USER_FLAIR("GET", OPTIONAL_SUBREDDIT + "/api/user_flair", "flair"),

    /**
     * Represents the endpoint {@code GET /api/user_flair_v2}. Requires OAuth scope 'flair'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_user_flair_v2">here</a> for more information */
    GET_USER_FLAIR_V2("GET", OPTIONAL_SUBREDDIT + "/api/user_flair_v2", "flair"),

    /**
     * Represents the endpoint {@code GET /user/{username}/{where}}. Requires OAuth scope 'history'. See <a href="https://www.reddit.com/dev/api/oauth#GET_user_{username}_{where}">here</a> for more information */
    GET_USER_USERNAME_WHERE("GET", "/user/{username}/{where}", "history"),

    /**
     * Represents the endpoint {@code GET /api/v1/me}. Requires OAuth scope 'identity'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me">here</a> for more information */
    GET_ME("GET", "/api/v1/me", "identity"),

    /**
     * Represents the endpoint {@code GET /api/v1/me/prefs}. Requires OAuth scope 'identity'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_prefs">here</a> for more information */
    GET_ME_PREFS("GET", "/api/v1/me/prefs", "identity"),

    /**
     * Represents the endpoint {@code GET /api/v1/me/trophies}. Requires OAuth scope 'identity'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_trophies">here</a> for more information */
    GET_ME_TROPHIES("GET", "/api/v1/me/trophies", "identity"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/accept_contributor_invite}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_accept_contributor_invite">here</a> for more information */
    POST_LIVE_THREAD_ACCEPT_CONTRIBUTOR_INVITE("POST", "/api/live/{thread}/accept_contributor_invite", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/close_thread}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_close_thread">here</a> for more information */
    POST_LIVE_THREAD_CLOSE_THREAD("POST", "/api/live/{thread}/close_thread", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/edit}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_edit">here</a> for more information */
    POST_LIVE_THREAD_EDIT("POST", "/api/live/{thread}/edit", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/hide_discussion}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_hide_discussion">here</a> for more information */
    POST_LIVE_THREAD_HIDE_DISCUSSION("POST", "/api/live/{thread}/hide_discussion", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/invite_contributor}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_invite_contributor">here</a> for more information */
    POST_LIVE_THREAD_INVITE_CONTRIBUTOR("POST", "/api/live/{thread}/invite_contributor", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/leave_contributor}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_leave_contributor">here</a> for more information */
    POST_LIVE_THREAD_LEAVE_CONTRIBUTOR("POST", "/api/live/{thread}/leave_contributor", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/rm_contributor}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_rm_contributor">here</a> for more information */
    POST_LIVE_THREAD_RM_CONTRIBUTOR("POST", "/api/live/{thread}/rm_contributor", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/rm_contributor_invite}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_rm_contributor_invite">here</a> for more information */
    POST_LIVE_THREAD_RM_CONTRIBUTOR_INVITE("POST", "/api/live/{thread}/rm_contributor_invite", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/set_contributor_permissions}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_set_contributor_permissions">here</a> for more information */
    POST_LIVE_THREAD_SET_CONTRIBUTOR_PERMISSIONS("POST", "/api/live/{thread}/set_contributor_permissions", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/unhide_discussion}. Requires OAuth scope 'livemanage'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_unhide_discussion">here</a> for more information */
    POST_LIVE_THREAD_UNHIDE_DISCUSSION("POST", "/api/live/{thread}/unhide_discussion", "livemanage"),

    /**
     * Represents the endpoint {@code POST /api/delete_sr_banner}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_banner">here</a> for more information */
    POST_DELETE_SR_BANNER("POST", OPTIONAL_SUBREDDIT + "/api/delete_sr_banner", "modconfig"),

    /**
     * Represents the endpoint {@code POST /api/delete_sr_header}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_header">here</a> for more information */
    POST_DELETE_SR_HEADER("POST", OPTIONAL_SUBREDDIT + "/api/delete_sr_header", "modconfig"),

    /**
     * Represents the endpoint {@code POST /api/delete_sr_icon}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_icon">here</a> for more information */
    POST_DELETE_SR_ICON("POST", OPTIONAL_SUBREDDIT + "/api/delete_sr_icon", "modconfig"),

    /**
     * Represents the endpoint {@code POST /api/delete_sr_img}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_img">here</a> for more information */
    POST_DELETE_SR_IMG("POST", OPTIONAL_SUBREDDIT + "/api/delete_sr_img", "modconfig"),

    /**
     * Represents the endpoint {@code POST /api/site_admin}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_site_admin">here</a> for more information */
    POST_SITE_ADMIN("POST", "/api/site_admin", "modconfig"),

    /**
     * Represents the endpoint {@code POST /api/subreddit_stylesheet}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_subreddit_stylesheet">here</a> for more information */
    POST_SUBREDDIT_STYLESHEET("POST", OPTIONAL_SUBREDDIT + "/api/subreddit_stylesheet", "modconfig"),

    /**
     * Represents the endpoint {@code POST /api/upload_sr_img}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_upload_sr_img">here</a> for more information */
    POST_UPLOAD_SR_IMG("POST", OPTIONAL_SUBREDDIT + "/api/upload_sr_img", "modconfig"),

    /**
     * Represents the endpoint {@code GET /r/{subreddit}/about/edit}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about_edit">here</a> for more information */
    GET_SUBREDDIT_ABOUT_EDIT("GET", "/r/{subreddit}/about/edit", "modconfig"),

    /**
     * Represents the endpoint {@code GET /r/{subreddit}/about/traffic}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about_traffic">here</a> for more information */
    GET_SUBREDDIT_ABOUT_TRAFFIC("GET", "/r/{subreddit}/about/traffic", "modconfig"),

    /**
     * Represents the endpoint {@code GET /stylesheet}. Requires OAuth scope 'modconfig'. See <a href="https://www.reddit.com/dev/api/oauth#GET_stylesheet">here</a> for more information */
    GET_STYLESHEET("GET", OPTIONAL_SUBREDDIT + "/stylesheet", "modconfig"),

    /**
     * Represents the endpoint {@code POST /api/mute_message_author}. Requires OAuth scope 'modcontributors'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mute_message_author">here</a> for more information */
    POST_MUTE_MESSAGE_AUTHOR("POST", "/api/mute_message_author", "modcontributors"),

    /**
     * Represents the endpoint {@code POST /api/unmute_message_author}. Requires OAuth scope 'modcontributors'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unmute_message_author">here</a> for more information */
    POST_UNMUTE_MESSAGE_AUTHOR("POST", "/api/unmute_message_author", "modcontributors"),

    /**
     * Represents the endpoint {@code POST /api/clearflairtemplates}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_clearflairtemplates">here</a> for more information */
    POST_CLEARFLAIRTEMPLATES("POST", OPTIONAL_SUBREDDIT + "/api/clearflairtemplates", "modflair"),

    /**
     * Represents the endpoint {@code POST /api/deleteflair}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_deleteflair">here</a> for more information */
    POST_DELETEFLAIR("POST", OPTIONAL_SUBREDDIT + "/api/deleteflair", "modflair"),

    /**
     * Represents the endpoint {@code POST /api/deleteflairtemplate}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_deleteflairtemplate">here</a> for more information */
    POST_DELETEFLAIRTEMPLATE("POST", OPTIONAL_SUBREDDIT + "/api/deleteflairtemplate", "modflair"),

    /**
     * Represents the endpoint {@code POST /api/flair}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_flair">here</a> for more information */
    POST_FLAIR("POST", OPTIONAL_SUBREDDIT + "/api/flair", "modflair"),

    /**
     * Represents the endpoint {@code PATCH /api/flair_template_order}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#PATCH_api_flair_template_order">here</a> for more information */
    PATCH_FLAIR_TEMPLATE_ORDER("PATCH", OPTIONAL_SUBREDDIT + "/api/flair_template_order", "modflair"),

    /**
     * Represents the endpoint {@code POST /api/flairconfig}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_flairconfig">here</a> for more information */
    POST_FLAIRCONFIG("POST", OPTIONAL_SUBREDDIT + "/api/flairconfig", "modflair"),

    /**
     * Represents the endpoint {@code POST /api/flaircsv}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_flaircsv">here</a> for more information */
    POST_FLAIRCSV("POST", OPTIONAL_SUBREDDIT + "/api/flaircsv", "modflair"),

    /**
     * Represents the endpoint {@code GET /api/flairlist}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_flairlist">here</a> for more information */
    GET_FLAIRLIST("GET", OPTIONAL_SUBREDDIT + "/api/flairlist", "modflair"),

    /**
     * Represents the endpoint {@code POST /api/flairtemplate}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_flairtemplate">here</a> for more information */
    POST_FLAIRTEMPLATE("POST", OPTIONAL_SUBREDDIT + "/api/flairtemplate", "modflair"),

    /**
     * Represents the endpoint {@code POST /api/flairtemplate_v2}. Requires OAuth scope 'modflair'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_flairtemplate_v2">here</a> for more information */
    POST_FLAIRTEMPLATE_V2("POST", OPTIONAL_SUBREDDIT + "/api/flairtemplate_v2", "modflair"),

    /**
     * Represents the endpoint {@code GET /about/log}. Requires OAuth scope 'modlog'. See <a href="https://www.reddit.com/dev/api/oauth#GET_about_log">here</a> for more information */
    GET_ABOUT_LOG("GET", OPTIONAL_SUBREDDIT + "/about/log", "modlog"),

    /**
     * Represents the endpoint {@code POST /api/mod/bulk_read}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_bulk_read">here</a> for more information */
    POST_MOD_BULK_READ("POST", "/api/mod/bulk_read", "modmail"),

    /**
     * Represents the endpoint {@code GET /api/mod/conversations}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations">here</a> for more information */
    GET_MOD_CONVERSATIONS("GET", "/api/mod/conversations", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations">here</a> for more information */
    POST_MOD_CONVERSATIONS("POST", "/api/mod/conversations", "modmail"),

    /**
     * Represents the endpoint {@code GET /api/mod/conversations/{conversation_id}}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_:conversation_id">here</a> for more information */
    GET_MOD_CONVERSATIONS_CONVERSATION_ID("GET", "/api/mod/conversations/{conversation_id}", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations/{conversation_id}}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id">here</a> for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID("POST", "/api/mod/conversations/{conversation_id}", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations/{conversation_id}/archive}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_archive">here</a> for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_ARCHIVE("POST", "/api/mod/conversations/{conversation_id}/archive", "modmail"),

    /**
     * Represents the endpoint {@code DELETE /api/mod/conversations/{conversation_id}/highlight}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#DELETE_api_mod_conversations_:conversation_id_highlight">here</a> for more information */
    DELETE_MOD_CONVERSATIONS_CONVERSATION_ID_HIGHLIGHT("DELETE", "/api/mod/conversations/{conversation_id}/highlight", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations/{conversation_id}/highlight}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_highlight">here</a> for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_HIGHLIGHT("POST", "/api/mod/conversations/{conversation_id}/highlight", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations/{conversation_id}/mute}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_mute">here</a> for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_MUTE("POST", "/api/mod/conversations/{conversation_id}/mute", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations/{conversation_id}/unarchive}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_unarchive">here</a> for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_UNARCHIVE("POST", "/api/mod/conversations/{conversation_id}/unarchive", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations/{conversation_id}/unmute}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_unmute">here</a> for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_UNMUTE("POST", "/api/mod/conversations/{conversation_id}/unmute", "modmail"),

    /**
     * Represents the endpoint {@code GET /api/mod/conversations/{conversation_id}/user}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_:conversation_id_user">here</a> for more information */
    GET_MOD_CONVERSATIONS_CONVERSATION_ID_USER("GET", "/api/mod/conversations/{conversation_id}/user", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations/read}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_read">here</a> for more information */
    POST_MOD_CONVERSATIONS_READ("POST", "/api/mod/conversations/read", "modmail"),

    /**
     * Represents the endpoint {@code GET /api/mod/conversations/subreddits}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_subreddits">here</a> for more information */
    GET_MOD_CONVERSATIONS_SUBREDDITS("GET", "/api/mod/conversations/subreddits", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/mod/conversations/unread}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_unread">here</a> for more information */
    POST_MOD_CONVERSATIONS_UNREAD("POST", "/api/mod/conversations/unread", "modmail"),

    /**
     * Represents the endpoint {@code GET /api/mod/conversations/unread/count}. Requires OAuth scope 'modmail'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_unread_count">here</a> for more information */
    GET_MOD_CONVERSATIONS_UNREAD_COUNT("GET", "/api/mod/conversations/unread/count", "modmail"),

    /**
     * Represents the endpoint {@code POST /api/setpermissions}. Requires OAuth scope 'modothers'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_setpermissions">here</a> for more information */
    POST_SETPERMISSIONS("POST", OPTIONAL_SUBREDDIT + "/api/setpermissions", "modothers"),

    /**
     * Represents the endpoint {@code POST /api/approve}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_approve">here</a> for more information */
    POST_APPROVE("POST", "/api/approve", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/distinguish}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_distinguish">here</a> for more information */
    POST_DISTINGUISH("POST", "/api/distinguish", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/ignore_reports}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_ignore_reports">here</a> for more information */
    POST_IGNORE_REPORTS("POST", "/api/ignore_reports", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/lock}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_lock">here</a> for more information */
    POST_LOCK("POST", "/api/lock", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/marknsfw}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_marknsfw">here</a> for more information */
    POST_MARKNSFW("POST", "/api/marknsfw", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/remove}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_remove">here</a> for more information */
    POST_REMOVE("POST", "/api/remove", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/set_contest_mode}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_set_contest_mode">here</a> for more information */
    POST_SET_CONTEST_MODE("POST", "/api/set_contest_mode", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/set_subreddit_sticky}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_set_subreddit_sticky">here</a> for more information */
    POST_SET_SUBREDDIT_STICKY("POST", "/api/set_subreddit_sticky", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/set_suggested_sort}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_set_suggested_sort">here</a> for more information */
    POST_SET_SUGGESTED_SORT("POST", "/api/set_suggested_sort", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/spoiler}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_spoiler">here</a> for more information */
    POST_SPOILER("POST", "/api/spoiler", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/unignore_reports}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unignore_reports">here</a> for more information */
    POST_UNIGNORE_REPORTS("POST", "/api/unignore_reports", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/unlock}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unlock">here</a> for more information */
    POST_UNLOCK("POST", "/api/unlock", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/unmarknsfw}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unmarknsfw">here</a> for more information */
    POST_UNMARKNSFW("POST", "/api/unmarknsfw", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/unspoiler}. Requires OAuth scope 'modposts'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unspoiler">here</a> for more information */
    POST_UNSPOILER("POST", "/api/unspoiler", "modposts"),

    /**
     * Represents the endpoint {@code POST /api/accept_moderator_invite}. Requires OAuth scope 'modself'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_accept_moderator_invite">here</a> for more information */
    POST_ACCEPT_MODERATOR_INVITE("POST", OPTIONAL_SUBREDDIT + "/api/accept_moderator_invite", "modself"),

    /**
     * Represents the endpoint {@code POST /api/leavecontributor}. Requires OAuth scope 'modself'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_leavecontributor">here</a> for more information */
    POST_LEAVECONTRIBUTOR("POST", "/api/leavecontributor", "modself"),

    /**
     * Represents the endpoint {@code POST /api/leavemoderator}. Requires OAuth scope 'modself'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_leavemoderator">here</a> for more information */
    POST_LEAVEMODERATOR("POST", "/api/leavemoderator", "modself"),

    /**
     * Represents the endpoint {@code POST /api/wiki/alloweditor/{act}}. Requires OAuth scope 'modwiki'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_{act}">here</a> for more information */
    POST_WIKI_ALLOWEDITOR_ACT("POST", OPTIONAL_SUBREDDIT + "/api/wiki/alloweditor/{act}", "modwiki"),

    /**
     * Represents the endpoint {@code POST /api/wiki/hide}. Requires OAuth scope 'modwiki'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_hide">here</a> for more information */
    POST_WIKI_HIDE("POST", OPTIONAL_SUBREDDIT + "/api/wiki/hide", "modwiki"),

    /**
     * Represents the endpoint {@code POST /api/wiki/revert}. Requires OAuth scope 'modwiki'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_revert">here</a> for more information */
    POST_WIKI_REVERT("POST", OPTIONAL_SUBREDDIT + "/api/wiki/revert", "modwiki"),

    /**
     * Represents the endpoint {@code GET /wiki/settings/{page}}. Requires OAuth scope 'modwiki'. See <a href="https://www.reddit.com/dev/api/oauth#GET_wiki_settings_{page}">here</a> for more information */
    GET_WIKI_SETTINGS_PAGE("GET", OPTIONAL_SUBREDDIT + "/wiki/settings/{page}", "modwiki"),

    /**
     * Represents the endpoint {@code POST /wiki/settings/{page}}. Requires OAuth scope 'modwiki'. See <a href="https://www.reddit.com/dev/api/oauth#POST_wiki_settings_{page}">here</a> for more information */
    POST_WIKI_SETTINGS_PAGE("POST", OPTIONAL_SUBREDDIT + "/wiki/settings/{page}", "modwiki"),

    /**
     * Represents the endpoint {@code GET /api/v1/me/friends/{username}}. Requires OAuth scope 'mysubreddits'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends_{username}">here</a> for more information */
    GET_ME_FRIENDS_USERNAME("GET", "/api/v1/me/friends/{username}", "mysubreddits"),

    /**
     * Represents the endpoint {@code GET /api/v1/me/karma}. Requires OAuth scope 'mysubreddits'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_me_karma">here</a> for more information */
    GET_ME_KARMA("GET", "/api/v1/me/karma", "mysubreddits"),

    /**
     * Represents the endpoint {@code GET /subreddits/mine/{where}}. Requires OAuth scope 'mysubreddits'. See <a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_{where}">here</a> for more information */
    GET_SUBREDDITS_MINE_WHERE("GET", "/subreddits/mine/{where}", "mysubreddits"),

    /**
     * Represents the endpoint {@code POST /api/block}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_block">here</a> for more information */
    POST_BLOCK("POST", "/api/block", "privatemessages"),

    /**
     * Represents the endpoint {@code POST /api/collapse_message}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_collapse_message">here</a> for more information */
    POST_COLLAPSE_MESSAGE("POST", "/api/collapse_message", "privatemessages"),

    /**
     * Represents the endpoint {@code POST /api/compose}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_compose">here</a> for more information */
    POST_COMPOSE("POST", "/api/compose", "privatemessages"),

    /**
     * Represents the endpoint {@code POST /api/del_msg}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_del_msg">here</a> for more information */
    POST_DEL_MSG("POST", "/api/del_msg", "privatemessages"),

    /**
     * Represents the endpoint {@code POST /api/read_all_messages}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_read_all_messages">here</a> for more information */
    POST_READ_ALL_MESSAGES("POST", "/api/read_all_messages", "privatemessages"),

    /**
     * Represents the endpoint {@code POST /api/read_message}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_read_message">here</a> for more information */
    POST_READ_MESSAGE("POST", "/api/read_message", "privatemessages"),

    /**
     * Represents the endpoint {@code POST /api/unblock_subreddit}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unblock_subreddit">here</a> for more information */
    POST_UNBLOCK_SUBREDDIT("POST", "/api/unblock_subreddit", "privatemessages"),

    /**
     * Represents the endpoint {@code POST /api/uncollapse_message}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_uncollapse_message">here</a> for more information */
    POST_UNCOLLAPSE_MESSAGE("POST", "/api/uncollapse_message", "privatemessages"),

    /**
     * Represents the endpoint {@code POST /api/unread_message}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unread_message">here</a> for more information */
    POST_UNREAD_MESSAGE("POST", "/api/unread_message", "privatemessages"),

    /**
     * Represents the endpoint {@code GET /api/user_data_by_account_ids}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_user_data_by_account_ids">here</a> for more information */
    GET_USER_DATA_BY_ACCOUNT_IDS("GET", "/api/user_data_by_account_ids", "privatemessages"),

    /**
     * Represents the endpoint {@code GET /message/{where}}. Requires OAuth scope 'privatemessages'. See <a href="https://www.reddit.com/dev/api/oauth#GET_message_{where}">here</a> for more information */
    GET_MESSAGE_WHERE("GET", "/message/{where}", "privatemessages"),

    /**
     * Represents the endpoint {@code GET /about/{location}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_about_{location}">here</a> for more information */
    GET_ABOUT_LOCATION("GET", OPTIONAL_SUBREDDIT + "/about/{location}", "read"),

    /**
     * Represents the endpoint {@code GET /about/{where}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_about_{where}">here</a> for more information */
    GET_ABOUT_WHERE("GET", OPTIONAL_SUBREDDIT + "/about/{where}", "read"),

    /**
     * Represents the endpoint {@code GET /api/info}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_info">here</a> for more information */
    GET_INFO("GET", OPTIONAL_SUBREDDIT + "/api/info", "read"),

    /**
     * Represents the endpoint {@code GET /api/live/by_id/{names}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_live_by_id_{names}">here</a> for more information */
    GET_LIVE_BY_ID_NAMES("GET", "/api/live/by_id/{names}", "read"),

    /**
     * Represents the endpoint {@code GET /api/live/happening_now}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_live_happening_now">here</a> for more information */
    GET_LIVE_HAPPENING_NOW("GET", "/api/live/happening_now", "read"),

    /**
     * Represents the endpoint {@code GET /api/morechildren}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_morechildren">here</a> for more information */
    GET_MORECHILDREN("GET", "/api/morechildren", "read"),

    /**
     * Represents the endpoint {@code GET /api/multi/mine}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_mine">here</a> for more information */
    GET_MULTI_MINE("GET", "/api/multi/mine", "read"),

    /**
     * Represents the endpoint {@code GET /api/multi/user/{username}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_user_{username}">here</a> for more information */
    GET_MULTI_USER_USERNAME("GET", "/api/multi/user/{username}", "read"),

    /**
     * Represents the endpoint {@code GET /api/multi/{multipath}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath}">here</a> for more information */
    GET_MULTI_MULTIPATH("GET", "/api/multi/{multipath}", "read"),

    /**
     * Represents the endpoint {@code GET /api/multi/{multipath}/description}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath}_description">here</a> for more information */
    GET_MULTI_MULTIPATH_DESCRIPTION("GET", "/api/multi/{multipath}/description", "read"),

    /**
     * Represents the endpoint {@code PUT /api/multi/{multipath}/description}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath}_description">here</a> for more information */
    PUT_MULTI_MULTIPATH_DESCRIPTION("PUT", "/api/multi/{multipath}/description", "read"),

    /**
     * Represents the endpoint {@code GET /api/multi/{multipath}/r/{srname}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath}_r_{srname}">here</a> for more information */
    GET_MULTI_MULTIPATH_R_SRNAME("GET", "/api/multi/{multipath}/r/{srname}", "read"),

    /**
     * Represents the endpoint {@code GET /api/recommend/sr/{srnames}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_recommend_sr_{srnames}">here</a> for more information */
    GET_RECOMMEND_SR_SRNAMES("GET", "/api/recommend/sr/{srnames}", "read"),

    /**
     * Represents the endpoint {@code GET /api/search_reddit_names}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_search_reddit_names">here</a> for more information */
    GET_SEARCH_REDDIT_NAMES("GET", "/api/search_reddit_names", "read"),

    /**
     * Represents the endpoint {@code POST /api/search_reddit_names}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_search_reddit_names">here</a> for more information */
    POST_SEARCH_REDDIT_NAMES("POST", "/api/search_reddit_names", "read"),

    /**
     * Represents the endpoint {@code POST /api/search_subreddits}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_search_subreddits">here</a> for more information */
    POST_SEARCH_SUBREDDITS("POST", "/api/search_subreddits", "read"),

    /**
     * Represents the endpoint {@code GET /api/subreddit_autocomplete}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_subreddit_autocomplete">here</a> for more information */
    GET_SUBREDDIT_AUTOCOMPLETE("GET", "/api/subreddit_autocomplete", "read"),

    /**
     * Represents the endpoint {@code GET /api/subreddit_autocomplete_v2}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_subreddit_autocomplete_v2">here</a> for more information */
    GET_SUBREDDIT_AUTOCOMPLETE_V2("GET", "/api/subreddit_autocomplete_v2", "read"),

    /**
     * Represents the endpoint {@code GET /api/v1/user/{username}/trophies}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_user_{username}_trophies">here</a> for more information */
    GET_USER_USERNAME_TROPHIES("GET", "/api/v1/user/{username}/trophies", "read"),

    /**
     * Represents the endpoint {@code GET /api/v1/{subreddit}/emojis/all}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_v1_{subreddit}_emojis_all">here</a> for more information */
    GET_SUBREDDIT_EMOJIS_ALL("GET", "/api/v1/{subreddit}/emojis/all", "read"),

    /**
     * Represents the endpoint {@code GET /best}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_best">here</a> for more information */
    GET_BEST("GET", "/best", "read"),

    /**
     * Represents the endpoint {@code GET /by_id/{names}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_by_id_{names}">here</a> for more information */
    GET_BY_ID_NAMES("GET", "/by_id/{names}", "read"),

    /**
     * Represents the endpoint {@code GET /comments/{article}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_comments_{article}">here</a> for more information */
    GET_COMMENTS_ARTICLE("GET", OPTIONAL_SUBREDDIT + "/comments/{article}", "read"),

    /**
     * Represents the endpoint {@code GET /duplicates/{article}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_duplicates_{article}">here</a> for more information */
    GET_DUPLICATES_ARTICLE("GET", "/duplicates/{article}", "read"),

    /**
     * Represents the endpoint {@code GET /hot}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_hot">here</a> for more information */
    GET_HOT("GET", OPTIONAL_SUBREDDIT + "/hot", "read"),

    /**
     * Represents the endpoint {@code GET /live/{thread}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_live_{thread}">here</a> for more information */
    GET_LIVE_THREAD("GET", "/live/{thread}", "read"),

    /**
     * Represents the endpoint {@code GET /live/{thread}/about}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_live_{thread}_about">here</a> for more information */
    GET_LIVE_THREAD_ABOUT("GET", "/live/{thread}/about", "read"),

    /**
     * Represents the endpoint {@code GET /live/{thread}/contributors}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_live_{thread}_contributors">here</a> for more information */
    GET_LIVE_THREAD_CONTRIBUTORS("GET", "/live/{thread}/contributors", "read"),

    /**
     * Represents the endpoint {@code GET /live/{thread}/discussions}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_live_{thread}_discussions">here</a> for more information */
    GET_LIVE_THREAD_DISCUSSIONS("GET", "/live/{thread}/discussions", "read"),

    /**
     * Represents the endpoint {@code GET /live/{thread}/updates/{update_id}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_live_{thread}_updates_{update_id}">here</a> for more information */
    GET_LIVE_THREAD_UPDATES_UPDATE_ID("GET", "/live/{thread}/updates/{update_id}", "read"),

    /**
     * Represents the endpoint {@code GET /new}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_new">here</a> for more information */
    GET_NEW("GET", OPTIONAL_SUBREDDIT + "/new", "read"),

    /**
     * Represents the endpoint {@code GET /prefs/{where}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_prefs_{where}">here</a> for more information */
    GET_PREFS_WHERE("GET", "/prefs/{where}", "read"),

    /**
     * Represents the endpoint {@code GET /profiles/search}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_profiles_search">here</a> for more information */
    GET_PROFILES_SEARCH("GET", "/profiles/search", "read"),

    /**
     * Represents the endpoint {@code GET /r/{subreddit}/about}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about">here</a> for more information */
    GET_SUBREDDIT_ABOUT("GET", "/r/{subreddit}/about", "read"),

    /**
     * Represents the endpoint {@code GET /r/{subreddit}/about/rules}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about_rules">here</a> for more information */
    GET_SUBREDDIT_ABOUT_RULES("GET", "/r/{subreddit}/about/rules", "read"),

    /**
     * Represents the endpoint {@code GET /random}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_random">here</a> for more information */
    GET_RANDOM("GET", OPTIONAL_SUBREDDIT + "/random", "read"),

    /**
     * Represents the endpoint {@code GET /rising}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_rising">here</a> for more information */
    GET_RISING("GET", OPTIONAL_SUBREDDIT + "/rising", "read"),

    /**
     * Represents the endpoint {@code GET /search}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_search">here</a> for more information */
    GET_SEARCH("GET", OPTIONAL_SUBREDDIT + "/search", "read"),

    /**
     * Represents the endpoint {@code GET /sidebar}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_sidebar">here</a> for more information */
    GET_SIDEBAR("GET", OPTIONAL_SUBREDDIT + "/sidebar", "read"),

    /**
     * Represents the endpoint {@code GET /sticky}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_sticky">here</a> for more information */
    GET_STICKY("GET", OPTIONAL_SUBREDDIT + "/sticky", "read"),

    /**
     * Represents the endpoint {@code GET /subreddits/search}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_search">here</a> for more information */
    GET_SUBREDDITS_SEARCH("GET", "/subreddits/search", "read"),

    /**
     * Represents the endpoint {@code GET /subreddits/{where}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_subreddits_{where}">here</a> for more information */
    GET_SUBREDDITS_WHERE("GET", "/subreddits/{where}", "read"),

    /**
     * Represents the endpoint {@code GET /user/{username}/about}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_user_{username}_about">here</a> for more information */
    GET_USER_USERNAME_ABOUT("GET", "/user/{username}/about", "read"),

    /**
     * Represents the endpoint {@code GET /users/{where}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_users_{where}">here</a> for more information */
    GET_USERS_WHERE("GET", "/users/{where}", "read"),

    /**
     * Represents the endpoint {@code GET /{sort}}. Requires OAuth scope 'read'. See <a href="https://www.reddit.com/dev/api/oauth#GET_{sort}">here</a> for more information */
    GET_SORT("GET", OPTIONAL_SUBREDDIT + "/{sort}", "read"),

    /**
     * Represents the endpoint {@code POST /api/hide}. Requires OAuth scope 'report'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_hide">here</a> for more information */
    POST_HIDE("POST", "/api/hide", "report"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/report}. Requires OAuth scope 'report'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_report">here</a> for more information */
    POST_LIVE_THREAD_REPORT("POST", "/api/live/{thread}/report", "report"),

    /**
     * Represents the endpoint {@code POST /api/report}. Requires OAuth scope 'report'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_report">here</a> for more information */
    POST_REPORT("POST", "/api/report", "report"),

    /**
     * Represents the endpoint {@code POST /api/report_user}. Requires OAuth scope 'report'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_report_user">here</a> for more information */
    POST_REPORT_USER("POST", "/api/report_user", "report"),

    /**
     * Represents the endpoint {@code POST /api/unhide}. Requires OAuth scope 'report'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unhide">here</a> for more information */
    POST_UNHIDE("POST", "/api/unhide", "report"),

    /**
     * Represents the endpoint {@code POST /api/save}. Requires OAuth scope 'save'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_save">here</a> for more information */
    POST_SAVE("POST", "/api/save", "save"),

    /**
     * Represents the endpoint {@code GET /api/saved_categories}. Requires OAuth scope 'save'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_saved_categories">here</a> for more information */
    GET_SAVED_CATEGORIES("GET", "/api/saved_categories", "save"),

    /**
     * Represents the endpoint {@code POST /api/store_visits}. Requires OAuth scope 'save'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_store_visits">here</a> for more information */
    POST_STORE_VISITS("POST", "/api/store_visits", "save"),

    /**
     * Represents the endpoint {@code POST /api/unsave}. Requires OAuth scope 'save'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_unsave">here</a> for more information */
    POST_UNSAVE("POST", "/api/unsave", "save"),

    /**
     * Represents the endpoint {@code POST /api/v1/{subreddit}/emoji.json}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_v1_{subreddit}_emoji.json">here</a> for more information */
    POST_SUBREDDIT_EMOJI("POST", "/api/v1/{subreddit}/emoji.json", "structuredstyles"),

    /**
     * Represents the endpoint {@code DELETE /api/v1/{subreddit}/emoji/{emoji_name}}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#DELETE_api_v1_{subreddit}_emoji_{emoji_name}">here</a> for more information */
    DELETE_SUBREDDIT_EMOJI_EMOJI_NAME("DELETE", "/api/v1/{subreddit}/emoji/{emoji_name}", "structuredstyles"),

    /**
     * Represents the endpoint {@code POST /api/v1/{subreddit}/emoji_asset_upload_s3.json}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_v1_{subreddit}_emoji_asset_upload_s3.json">here</a> for more information */
    POST_SUBREDDIT_EMOJI_ASSET_UPLOAD_S3("POST", "/api/v1/{subreddit}/emoji_asset_upload_s3.json", "structuredstyles"),

    /**
     * Represents the endpoint {@code POST /api/widget}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_widget">here</a> for more information */
    POST_WIDGET("POST", "/api/widget", "structuredstyles"),

    /**
     * Represents the endpoint {@code DELETE /api/widget/{widget_id}}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#DELETE_api_widget_{widget_id}">here</a> for more information */
    DELETE_WIDGET_WIDGET_ID("DELETE", "/api/widget/{widget_id}", "structuredstyles"),

    /**
     * Represents the endpoint {@code PUT /api/widget/{widget_id}}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#PUT_api_widget_{widget_id}">here</a> for more information */
    PUT_WIDGET_WIDGET_ID("PUT", "/api/widget/{widget_id}", "structuredstyles"),

    /**
     * Represents the endpoint {@code POST /api/widget_image_upload_s3}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_widget_image_upload_s3">here</a> for more information */
    POST_WIDGET_IMAGE_UPLOAD_S3("POST", "/api/widget_image_upload_s3", "structuredstyles"),

    /**
     * Represents the endpoint {@code PATCH /api/widget_order/{section}}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#PATCH_api_widget_order_{section}">here</a> for more information */
    PATCH_WIDGET_ORDER_SECTION("PATCH", "/api/widget_order/{section}", "structuredstyles"),

    /**
     * Represents the endpoint {@code GET /api/widgets}. Requires OAuth scope 'structuredstyles'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_widgets">here</a> for more information */
    GET_WIDGETS("GET", "/api/widgets", "structuredstyles"),

    /**
     * Represents the endpoint {@code POST /api/live/create}. Requires OAuth scope 'submit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_create">here</a> for more information */
    POST_LIVE_CREATE("POST", "/api/live/create", "submit"),

    /**
     * Represents the endpoint {@code POST /api/live/{thread}/update}. Requires OAuth scope 'submit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_update">here</a> for more information */
    POST_LIVE_THREAD_UPDATE("POST", "/api/live/{thread}/update", "submit"),

    /**
     * Represents the endpoint {@code POST /api/submit}. Requires OAuth scope 'submit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_submit">here</a> for more information */
    POST_SUBMIT("POST", "/api/submit", "submit"),

    /**
     * Represents the endpoint {@code GET /api/submit_text}. Requires OAuth scope 'submit'. See <a href="https://www.reddit.com/dev/api/oauth#GET_api_submit_text">here</a> for more information */
    GET_SUBMIT_TEXT("GET", OPTIONAL_SUBREDDIT + "/api/submit_text", "submit"),

    /**
     * Represents the endpoint {@code POST /api/multi/copy}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_multi_copy">here</a> for more information */
    POST_MULTI_COPY("POST", "/api/multi/copy", "subscribe"),

    /**
     * Represents the endpoint {@code POST /api/multi/rename}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_multi_rename">here</a> for more information */
    POST_MULTI_RENAME("POST", "/api/multi/rename", "subscribe"),

    /**
     * Represents the endpoint {@code DELETE /api/multi/{multipath}}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#DELETE_api_multi_{multipath}">here</a> for more information */
    DELETE_MULTI_MULTIPATH("DELETE", "/api/multi/{multipath}", "subscribe"),

    /**
     * Represents the endpoint {@code POST /api/multi/{multipath}}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_multi_{multipath}">here</a> for more information */
    POST_MULTI_MULTIPATH("POST", "/api/multi/{multipath}", "subscribe"),

    /**
     * Represents the endpoint {@code PUT /api/multi/{multipath}}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath}">here</a> for more information */
    PUT_MULTI_MULTIPATH("PUT", "/api/multi/{multipath}", "subscribe"),

    /**
     * Represents the endpoint {@code DELETE /api/multi/{multipath}/r/{srname}}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#DELETE_api_multi_{multipath}_r_{srname}">here</a> for more information */
    DELETE_MULTI_MULTIPATH_R_SRNAME("DELETE", "/api/multi/{multipath}/r/{srname}", "subscribe"),

    /**
     * Represents the endpoint {@code PUT /api/multi/{multipath}/r/{srname}}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath}_r_{srname}">here</a> for more information */
    PUT_MULTI_MULTIPATH_R_SRNAME("PUT", "/api/multi/{multipath}/r/{srname}", "subscribe"),

    /**
     * Represents the endpoint {@code POST /api/subscribe}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_subscribe">here</a> for more information */
    POST_SUBSCRIBE("POST", "/api/subscribe", "subscribe"),

    /**
     * Represents the endpoint {@code DELETE /api/v1/me/friends/{username}}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#DELETE_api_v1_me_friends_{username}">here</a> for more information */
    DELETE_ME_FRIENDS_USERNAME("DELETE", "/api/v1/me/friends/{username}", "subscribe"),

    /**
     * Represents the endpoint {@code PUT /api/v1/me/friends/{username}}. Requires OAuth scope 'subscribe'. See <a href="https://www.reddit.com/dev/api/oauth#PUT_api_v1_me_friends_{username}">here</a> for more information */
    PUT_ME_FRIENDS_USERNAME("PUT", "/api/v1/me/friends/{username}", "subscribe"),

    /**
     * Represents the endpoint {@code POST /api/vote}. Requires OAuth scope 'vote'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_vote">here</a> for more information */
    POST_VOTE("POST", "/api/vote", "vote"),

    /**
     * Represents the endpoint {@code POST /api/wiki/edit}. Requires OAuth scope 'wikiedit'. See <a href="https://www.reddit.com/dev/api/oauth#POST_api_wiki_edit">here</a> for more information */
    POST_WIKI_EDIT("POST", OPTIONAL_SUBREDDIT + "/api/wiki/edit", "wikiedit"),

    /**
     * Represents the endpoint {@code GET /wiki/discussions/{page}}. Requires OAuth scope 'wikiread'. See <a href="https://www.reddit.com/dev/api/oauth#GET_wiki_discussions_{page}">here</a> for more information */
    GET_WIKI_DISCUSSIONS_PAGE("GET", OPTIONAL_SUBREDDIT + "/wiki/discussions/{page}", "wikiread"),

    /**
     * Represents the endpoint {@code GET /wiki/pages}. Requires OAuth scope 'wikiread'. See <a href="https://www.reddit.com/dev/api/oauth#GET_wiki_pages">here</a> for more information */
    GET_WIKI_PAGES("GET", OPTIONAL_SUBREDDIT + "/wiki/pages", "wikiread"),

    /**
     * Represents the endpoint {@code GET /wiki/revisions}. Requires OAuth scope 'wikiread'. See <a href="https://www.reddit.com/dev/api/oauth#GET_wiki_revisions">here</a> for more information */
    GET_WIKI_REVISIONS("GET", OPTIONAL_SUBREDDIT + "/wiki/revisions", "wikiread"),

    /**
     * Represents the endpoint {@code GET /wiki/revisions/{page}}. Requires OAuth scope 'wikiread'. See <a href="https://www.reddit.com/dev/api/oauth#GET_wiki_revisions_{page}">here</a> for more information */
    GET_WIKI_REVISIONS_PAGE("GET", OPTIONAL_SUBREDDIT + "/wiki/revisions/{page}", "wikiread"),

    /**
     * Represents the endpoint {@code GET /wiki/{page}}. Requires OAuth scope 'wikiread'. See <a href="https://www.reddit.com/dev/api/oauth#GET_wiki_{page}">here</a> for more information */
    GET_WIKI_PAGE("GET", OPTIONAL_SUBREDDIT + "/wiki/{page}", "wikiread");

    private final String method;

    private final String path;

    private final String scope;

    Endpoint(final String method, final String path, final String scope) {
        this.method = method;
        this.path = path;
        this.scope = scope;
    }

    /**
     * Gets this Endpoint's path, e.g. {@code /api/comment} */
    public String getPath() {
        return this.path;
    }

    /**
     * Gets this Endpoint's HTTP method ("GET", "POST", etc.) */
    public String getMethod() {
        return this.method;
    }

    /**
     * Gets the OAuth2 scope required to use this endpoint */
    public String getScope() {
        return this.scope;
    }

    public static class Constant {
        public static final String OPTIONAL_SUBREDDIT = "[/r/{subreddit}]";
    }
}
