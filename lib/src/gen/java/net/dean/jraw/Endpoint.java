package net.dean.jraw;

/**
 * This is a dynamically generated enumeration of all reddit API endpoints.
 *
 * For JRAW developers: this class should not be edited by hand. This class can be regenerated through the `:meta:update` Gradle task. */
public enum Endpoint {
    /**
     * Represents the endpoint `POST /api/comment`. Requires OAuth scope 'any'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_comment) for more information */
    POST_COMMENT("POST /api/comment"),

    /**
     * Represents the endpoint `POST /api/friend`. Requires OAuth scope 'any'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_friend) for more information */
    POST_FRIEND("POST /api/friend"),

    /**
     * Represents the endpoint `GET /api/needs_captcha`. Requires OAuth scope 'any'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_needs_captcha) for more information */
    GET_NEEDS_CAPTCHA("GET /api/needs_captcha"),

    /**
     * Represents the endpoint `POST /api/unfriend`. Requires OAuth scope 'any'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unfriend) for more information */
    POST_UNFRIEND("POST /api/unfriend"),

    /**
     * Represents the endpoint `GET /api/username_available`. Requires OAuth scope 'any'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_username_available) for more information */
    GET_USERNAME_AVAILABLE("GET /api/username_available"),

    /**
     * Represents the endpoint `GET /api/v1/scopes`. Requires OAuth scope 'any'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_scopes) for more information */
    GET_SCOPES("GET /api/v1/scopes"),

    /**
     * Represents the endpoint `PATCH /api/v1/me/prefs`. Requires OAuth scope 'account'. See [here](https://www.reddit.com/dev/api/oauth#PATCH_api_v1_me_prefs) for more information */
    PATCH_ME_PREFS("PATCH /api/v1/me/prefs"),

    /**
     * Represents the endpoint `POST /api/v1/gold/gild/{fullname}`. Requires OAuth scope 'creddits'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_gild_{fullname}) for more information */
    POST_GOLD_GILD_FULLNAME("POST /api/v1/gold/gild/{fullname}"),

    /**
     * Represents the endpoint `POST /api/v1/gold/give/{username}`. Requires OAuth scope 'creddits'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_v1_gold_give_{username}) for more information */
    POST_GOLD_GIVE_USERNAME("POST /api/v1/gold/give/{username}"),

    /**
     * Represents the endpoint `POST /api/del`. Requires OAuth scope 'edit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_del) for more information */
    POST_DEL("POST /api/del"),

    /**
     * Represents the endpoint `POST /api/editusertext`. Requires OAuth scope 'edit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_editusertext) for more information */
    POST_EDITUSERTEXT("POST /api/editusertext"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/delete_update`. Requires OAuth scope 'edit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_delete_update) for more information */
    POST_LIVE_THREAD_DELETE_UPDATE("POST /api/live/{thread}/delete_update"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/strike_update`. Requires OAuth scope 'edit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_strike_update) for more information */
    POST_LIVE_THREAD_STRIKE_UPDATE("POST /api/live/{thread}/strike_update"),

    /**
     * Represents the endpoint `POST /api/sendreplies`. Requires OAuth scope 'edit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_sendreplies) for more information */
    POST_SENDREPLIES("POST /api/sendreplies"),

    /**
     * Represents the endpoint `POST /api/flairselector`. Requires OAuth scope 'flair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_flairselector) for more information */
    POST_FLAIRSELECTOR("POST /api/flairselector"),

    /**
     * Represents the endpoint `GET /api/link_flair`. Requires OAuth scope 'flair'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_link_flair) for more information */
    GET_LINK_FLAIR("GET /api/link_flair"),

    /**
     * Represents the endpoint `POST /api/selectflair`. Requires OAuth scope 'flair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_selectflair) for more information */
    POST_SELECTFLAIR("POST /api/selectflair"),

    /**
     * Represents the endpoint `POST /api/setflairenabled`. Requires OAuth scope 'flair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_setflairenabled) for more information */
    POST_SETFLAIRENABLED("POST /api/setflairenabled"),

    /**
     * Represents the endpoint `GET /user/{username}/{where}`. Requires OAuth scope 'history'. See [here](https://www.reddit.com/dev/api/oauth#GET_user_{username}_{where}) for more information */
    GET_USER_USERNAME_WHERE("GET /user/{username}/{where}"),

    /**
     * Represents the endpoint `GET /api/v1/me`. Requires OAuth scope 'identity'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_me) for more information */
    GET_ME("GET /api/v1/me"),

    /**
     * Represents the endpoint `GET /api/v1/me/prefs`. Requires OAuth scope 'identity'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_prefs) for more information */
    GET_ME_PREFS("GET /api/v1/me/prefs"),

    /**
     * Represents the endpoint `GET /api/v1/me/trophies`. Requires OAuth scope 'identity'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_trophies) for more information */
    GET_ME_TROPHIES("GET /api/v1/me/trophies"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/accept_contributor_invite`. Requires OAuth scope 'livemanage'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_accept_contributor_invite) for more information */
    POST_LIVE_THREAD_ACCEPT_CONTRIBUTOR_INVITE("POST /api/live/{thread}/accept_contributor_invite"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/close_{thread}`. Requires OAuth scope 'livemanage'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_close_thread) for more information */
    POST_LIVE_THREAD_CLOSE_THREAD("POST /api/live/{thread}/close_{thread}"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/edit`. Requires OAuth scope 'livemanage'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_edit) for more information */
    POST_LIVE_THREAD_EDIT("POST /api/live/{thread}/edit"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/invite_contributor`. Requires OAuth scope 'livemanage'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_invite_contributor) for more information */
    POST_LIVE_THREAD_INVITE_CONTRIBUTOR("POST /api/live/{thread}/invite_contributor"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/leave_contributor`. Requires OAuth scope 'livemanage'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_leave_contributor) for more information */
    POST_LIVE_THREAD_LEAVE_CONTRIBUTOR("POST /api/live/{thread}/leave_contributor"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/rm_contributor`. Requires OAuth scope 'livemanage'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_rm_contributor) for more information */
    POST_LIVE_THREAD_RM_CONTRIBUTOR("POST /api/live/{thread}/rm_contributor"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/rm_contributor_invite`. Requires OAuth scope 'livemanage'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_rm_contributor_invite) for more information */
    POST_LIVE_THREAD_RM_CONTRIBUTOR_INVITE("POST /api/live/{thread}/rm_contributor_invite"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/set_contributor_permissions`. Requires OAuth scope 'livemanage'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_set_contributor_permissions) for more information */
    POST_LIVE_THREAD_SET_CONTRIBUTOR_PERMISSIONS("POST /api/live/{thread}/set_contributor_permissions"),

    /**
     * Represents the endpoint `POST /api/delete_sr_banner`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_banner) for more information */
    POST_DELETE_SR_BANNER("POST /api/delete_sr_banner"),

    /**
     * Represents the endpoint `POST /api/delete_sr_header`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_header) for more information */
    POST_DELETE_SR_HEADER("POST /api/delete_sr_header"),

    /**
     * Represents the endpoint `POST /api/delete_sr_icon`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_icon) for more information */
    POST_DELETE_SR_ICON("POST /api/delete_sr_icon"),

    /**
     * Represents the endpoint `POST /api/delete_sr_img`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_delete_sr_img) for more information */
    POST_DELETE_SR_IMG("POST /api/delete_sr_img"),

    /**
     * Represents the endpoint `POST /api/site_admin`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_site_admin) for more information */
    POST_SITE_ADMIN("POST /api/site_admin"),

    /**
     * Represents the endpoint `POST /api/subreddit_stylesheet`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_subreddit_stylesheet) for more information */
    POST_SUBREDDIT_STYLESHEET("POST /api/subreddit_stylesheet"),

    /**
     * Represents the endpoint `POST /api/upload_sr_img`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_upload_sr_img) for more information */
    POST_UPLOAD_SR_IMG("POST /api/upload_sr_img"),

    /**
     * Represents the endpoint `GET /r/{subreddit}/about/edit`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about_edit) for more information */
    GET_SUBREDDIT_ABOUT_EDIT("GET /r/{subreddit}/about/edit"),

    /**
     * Represents the endpoint `GET /stylesheet`. Requires OAuth scope 'modconfig'. See [here](https://www.reddit.com/dev/api/oauth#GET_stylesheet) for more information */
    GET_STYLESHEET("GET /stylesheet"),

    /**
     * Represents the endpoint `POST /api/mute_message_author`. Requires OAuth scope 'modcontributors'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mute_message_author) for more information */
    POST_MUTE_MESSAGE_AUTHOR("POST /api/mute_message_author"),

    /**
     * Represents the endpoint `POST /api/unmute_message_author`. Requires OAuth scope 'modcontributors'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unmute_message_author) for more information */
    POST_UNMUTE_MESSAGE_AUTHOR("POST /api/unmute_message_author"),

    /**
     * Represents the endpoint `POST /api/clearflairtemplates`. Requires OAuth scope 'modflair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_clearflairtemplates) for more information */
    POST_CLEARFLAIRTEMPLATES("POST /api/clearflairtemplates"),

    /**
     * Represents the endpoint `POST /api/deleteflair`. Requires OAuth scope 'modflair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_deleteflair) for more information */
    POST_DELETEFLAIR("POST /api/deleteflair"),

    /**
     * Represents the endpoint `POST /api/deleteflairtemplate`. Requires OAuth scope 'modflair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_deleteflairtemplate) for more information */
    POST_DELETEFLAIRTEMPLATE("POST /api/deleteflairtemplate"),

    /**
     * Represents the endpoint `POST /api/flair`. Requires OAuth scope 'modflair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_flair) for more information */
    POST_FLAIR("POST /api/flair"),

    /**
     * Represents the endpoint `POST /api/flairconfig`. Requires OAuth scope 'modflair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_flairconfig) for more information */
    POST_FLAIRCONFIG("POST /api/flairconfig"),

    /**
     * Represents the endpoint `POST /api/flaircsv`. Requires OAuth scope 'modflair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_flaircsv) for more information */
    POST_FLAIRCSV("POST /api/flaircsv"),

    /**
     * Represents the endpoint `GET /api/flairlist`. Requires OAuth scope 'modflair'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_flairlist) for more information */
    GET_FLAIRLIST("GET /api/flairlist"),

    /**
     * Represents the endpoint `POST /api/flairtemplate`. Requires OAuth scope 'modflair'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_flairtemplate) for more information */
    POST_FLAIRTEMPLATE("POST /api/flairtemplate"),

    /**
     * Represents the endpoint `GET /about/log`. Requires OAuth scope 'modlog'. See [here](https://www.reddit.com/dev/api/oauth#GET_about_log) for more information */
    GET_ABOUT_LOG("GET /about/log"),

    /**
     * Represents the endpoint `POST /api/mod/bulk_read`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_bulk_read) for more information */
    POST_MOD_BULK_READ("POST /api/mod/bulk_read"),

    /**
     * Represents the endpoint `GET /api/mod/conversations`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations) for more information */
    GET_MOD_CONVERSATIONS("GET /api/mod/conversations"),

    /**
     * Represents the endpoint `POST /api/mod/conversations`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations) for more information */
    POST_MOD_CONVERSATIONS("POST /api/mod/conversations"),

    /**
     * Represents the endpoint `GET /api/mod/conversations/{conversation_id}`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_:conversation_id) for more information */
    GET_MOD_CONVERSATIONS_CONVERSATION_ID("GET /api/mod/conversations/{conversation_id}"),

    /**
     * Represents the endpoint `POST /api/mod/conversations/{conversation_id}`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id) for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID("POST /api/mod/conversations/{conversation_id}"),

    /**
     * Represents the endpoint `POST /api/mod/conversations/{conversation_id}/archive`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_archive) for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_ARCHIVE("POST /api/mod/conversations/{conversation_id}/archive"),

    /**
     * Represents the endpoint `DELETE /api/mod/conversations/{conversation_id}/highlight`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#DELETE_api_mod_conversations_:conversation_id_highlight) for more information */
    DELETE_MOD_CONVERSATIONS_CONVERSATION_ID_HIGHLIGHT("DELETE /api/mod/conversations/{conversation_id}/highlight"),

    /**
     * Represents the endpoint `POST /api/mod/conversations/{conversation_id}/highlight`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_highlight) for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_HIGHLIGHT("POST /api/mod/conversations/{conversation_id}/highlight"),

    /**
     * Represents the endpoint `POST /api/mod/conversations/{conversation_id}/mute`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_mute) for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_MUTE("POST /api/mod/conversations/{conversation_id}/mute"),

    /**
     * Represents the endpoint `POST /api/mod/conversations/{conversation_id}/unarchive`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_unarchive) for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_UNARCHIVE("POST /api/mod/conversations/{conversation_id}/unarchive"),

    /**
     * Represents the endpoint `POST /api/mod/conversations/{conversation_id}/unmute`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_:conversation_id_unmute) for more information */
    POST_MOD_CONVERSATIONS_CONVERSATION_ID_UNMUTE("POST /api/mod/conversations/{conversation_id}/unmute"),

    /**
     * Represents the endpoint `GET /api/mod/conversations/{conversation_id}/user`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_:conversation_id_user) for more information */
    GET_MOD_CONVERSATIONS_CONVERSATION_ID_USER("GET /api/mod/conversations/{conversation_id}/user"),

    /**
     * Represents the endpoint `POST /api/mod/conversations/read`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_read) for more information */
    POST_MOD_CONVERSATIONS_READ("POST /api/mod/conversations/read"),

    /**
     * Represents the endpoint `GET /api/mod/conversations/subreddits`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_subreddits) for more information */
    GET_MOD_CONVERSATIONS_SUBREDDITS("GET /api/mod/conversations/subreddits"),

    /**
     * Represents the endpoint `POST /api/mod/conversations/unread`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_mod_conversations_unread) for more information */
    POST_MOD_CONVERSATIONS_UNREAD("POST /api/mod/conversations/unread"),

    /**
     * Represents the endpoint `GET /api/mod/conversations/unread/count`. Requires OAuth scope 'modmail'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_mod_conversations_unread_count) for more information */
    GET_MOD_CONVERSATIONS_UNREAD_COUNT("GET /api/mod/conversations/unread/count"),

    /**
     * Represents the endpoint `POST /api/setpermissions`. Requires OAuth scope 'modothers'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_setpermissions) for more information */
    POST_SETPERMISSIONS("POST /api/setpermissions"),

    /**
     * Represents the endpoint `POST /api/approve`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_approve) for more information */
    POST_APPROVE("POST /api/approve"),

    /**
     * Represents the endpoint `POST /api/distinguish`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_distinguish) for more information */
    POST_DISTINGUISH("POST /api/distinguish"),

    /**
     * Represents the endpoint `POST /api/ignore_reports`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_ignore_reports) for more information */
    POST_IGNORE_REPORTS("POST /api/ignore_reports"),

    /**
     * Represents the endpoint `POST /api/lock`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_lock) for more information */
    POST_LOCK("POST /api/lock"),

    /**
     * Represents the endpoint `POST /api/marknsfw`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_marknsfw) for more information */
    POST_MARKNSFW("POST /api/marknsfw"),

    /**
     * Represents the endpoint `POST /api/remove`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_remove) for more information */
    POST_REMOVE("POST /api/remove"),

    /**
     * Represents the endpoint `POST /api/set_contest_mode`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_set_contest_mode) for more information */
    POST_SET_CONTEST_MODE("POST /api/set_contest_mode"),

    /**
     * Represents the endpoint `POST /api/set_subreddit_sticky`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_set_subreddit_sticky) for more information */
    POST_SET_SUBREDDIT_STICKY("POST /api/set_subreddit_sticky"),

    /**
     * Represents the endpoint `POST /api/set_suggested_sort`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_set_suggested_sort) for more information */
    POST_SET_SUGGESTED_SORT("POST /api/set_suggested_sort"),

    /**
     * Represents the endpoint `POST /api/spoiler`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_spoiler) for more information */
    POST_SPOILER("POST /api/spoiler"),

    /**
     * Represents the endpoint `POST /api/unignore_reports`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unignore_reports) for more information */
    POST_UNIGNORE_REPORTS("POST /api/unignore_reports"),

    /**
     * Represents the endpoint `POST /api/unlock`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unlock) for more information */
    POST_UNLOCK("POST /api/unlock"),

    /**
     * Represents the endpoint `POST /api/unmarknsfw`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unmarknsfw) for more information */
    POST_UNMARKNSFW("POST /api/unmarknsfw"),

    /**
     * Represents the endpoint `POST /api/unspoiler`. Requires OAuth scope 'modposts'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unspoiler) for more information */
    POST_UNSPOILER("POST /api/unspoiler"),

    /**
     * Represents the endpoint `POST /api/accept_moderator_invite`. Requires OAuth scope 'modself'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_accept_moderator_invite) for more information */
    POST_ACCEPT_MODERATOR_INVITE("POST /api/accept_moderator_invite"),

    /**
     * Represents the endpoint `POST /api/leavecontributor`. Requires OAuth scope 'modself'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_leavecontributor) for more information */
    POST_LEAVECONTRIBUTOR("POST /api/leavecontributor"),

    /**
     * Represents the endpoint `POST /api/leavemoderator`. Requires OAuth scope 'modself'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_leavemoderator) for more information */
    POST_LEAVEMODERATOR("POST /api/leavemoderator"),

    /**
     * Represents the endpoint `POST /api/wiki/alloweditor/{act}`. Requires OAuth scope 'modwiki'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_wiki_alloweditor_{act}) for more information */
    POST_WIKI_ALLOWEDITOR_ACT("POST /api/wiki/alloweditor/{act}"),

    /**
     * Represents the endpoint `POST /api/wiki/hide`. Requires OAuth scope 'modwiki'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_wiki_hide) for more information */
    POST_WIKI_HIDE("POST /api/wiki/hide"),

    /**
     * Represents the endpoint `POST /api/wiki/revert`. Requires OAuth scope 'modwiki'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_wiki_revert) for more information */
    POST_WIKI_REVERT("POST /api/wiki/revert"),

    /**
     * Represents the endpoint `GET /wiki/settings/{page}`. Requires OAuth scope 'modwiki'. See [here](https://www.reddit.com/dev/api/oauth#GET_wiki_settings_{page}) for more information */
    GET_WIKI_SETTINGS_PAGE("GET /wiki/settings/{page}"),

    /**
     * Represents the endpoint `POST /wiki/settings/{page}`. Requires OAuth scope 'modwiki'. See [here](https://www.reddit.com/dev/api/oauth#POST_wiki_settings_{page}) for more information */
    POST_WIKI_SETTINGS_PAGE("POST /wiki/settings/{page}"),

    /**
     * Represents the endpoint `GET /api/v1/me/friends/{username}`. Requires OAuth scope 'mysubreddits'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_friends_{username}) for more information */
    GET_ME_FRIENDS_USERNAME("GET /api/v1/me/friends/{username}"),

    /**
     * Represents the endpoint `GET /api/v1/me/karma`. Requires OAuth scope 'mysubreddits'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_karma) for more information */
    GET_ME_KARMA("GET /api/v1/me/karma"),

    /**
     * Represents the endpoint `GET /subreddits/mine/{where}`. Requires OAuth scope 'mysubreddits'. See [here](https://www.reddit.com/dev/api/oauth#GET_subreddits_mine_{where}) for more information */
    GET_SUBREDDITS_MINE_WHERE("GET /subreddits/mine/{where}"),

    /**
     * Represents the endpoint `POST /api/block`. Requires OAuth scope 'privatemessages'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_block) for more information */
    POST_BLOCK("POST /api/block"),

    /**
     * Represents the endpoint `POST /api/compose`. Requires OAuth scope 'privatemessages'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_compose) for more information */
    POST_COMPOSE("POST /api/compose"),

    /**
     * Represents the endpoint `POST /api/del_msg`. Requires OAuth scope 'privatemessages'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_del_msg) for more information */
    POST_DEL_MSG("POST /api/del_msg"),

    /**
     * Represents the endpoint `POST /api/read_all_messages`. Requires OAuth scope 'privatemessages'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_read_all_messages) for more information */
    POST_READ_ALL_MESSAGES("POST /api/read_all_messages"),

    /**
     * Represents the endpoint `POST /api/read_message`. Requires OAuth scope 'privatemessages'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_read_message) for more information */
    POST_READ_MESSAGE("POST /api/read_message"),

    /**
     * Represents the endpoint `POST /api/unblock_subreddit`. Requires OAuth scope 'privatemessages'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unblock_subreddit) for more information */
    POST_UNBLOCK_SUBREDDIT("POST /api/unblock_subreddit"),

    /**
     * Represents the endpoint `POST /api/unread_message`. Requires OAuth scope 'privatemessages'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unread_message) for more information */
    POST_UNREAD_MESSAGE("POST /api/unread_message"),

    /**
     * Represents the endpoint `GET /message/{where}`. Requires OAuth scope 'privatemessages'. See [here](https://www.reddit.com/dev/api/oauth#GET_message_{where}) for more information */
    GET_MESSAGE_WHERE("GET /message/{where}"),

    /**
     * Represents the endpoint `GET /about/{location}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_about_{location}) for more information */
    GET_ABOUT_LOCATION("GET /about/{location}"),

    /**
     * Represents the endpoint `GET /about/{where}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_about_{where}) for more information */
    GET_ABOUT_WHERE("GET /about/{where}"),

    /**
     * Represents the endpoint `GET /api/info`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_info) for more information */
    GET_INFO("GET /api/info"),

    /**
     * Represents the endpoint `GET /api/live/by_id/{names}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_live_by_id_{names}) for more information */
    GET_LIVE_BY_ID_NAMES("GET /api/live/by_id/{names}"),

    /**
     * Represents the endpoint `GET /api/live/happening_now`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_live_happening_now) for more information */
    GET_LIVE_HAPPENING_NOW("GET /api/live/happening_now"),

    /**
     * Represents the endpoint `GET /api/morechildren`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_morechildren) for more information */
    GET_MORECHILDREN("GET /api/morechildren"),

    /**
     * Represents the endpoint `GET /api/multi/mine`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_multi_mine) for more information */
    GET_MULTI_MINE("GET /api/multi/mine"),

    /**
     * Represents the endpoint `GET /api/multi/user/{username}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_multi_user_{username}) for more information */
    GET_MULTI_USER_USERNAME("GET /api/multi/user/{username}"),

    /**
     * Represents the endpoint `GET /api/multi/{multipath}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath}) for more information */
    GET_MULTI_MULTIPATH("GET /api/multi/{multipath}"),

    /**
     * Represents the endpoint `GET /api/multi/{multipath}/description`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath}_description) for more information */
    GET_MULTI_MULTIPATH_DESCRIPTION("GET /api/multi/{multipath}/description"),

    /**
     * Represents the endpoint `PUT /api/multi/{multipath}/description`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath}_description) for more information */
    PUT_MULTI_MULTIPATH_DESCRIPTION("PUT /api/multi/{multipath}/description"),

    /**
     * Represents the endpoint `GET /api/multi/{multipath}/r/{srname}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_multi_{multipath}_r_{srname}) for more information */
    GET_MULTI_MULTIPATH_R_SRNAME("GET /api/multi/{multipath}/r/{srname}"),

    /**
     * Represents the endpoint `GET /api/recommend/sr/{srnames}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_recommend_sr_{srnames}) for more information */
    GET_RECOMMEND_SR_SRNAMES("GET /api/recommend/sr/{srnames}"),

    /**
     * Represents the endpoint `POST /api/search_reddit_names`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_search_reddit_names) for more information */
    POST_SEARCH_REDDIT_NAMES("POST /api/search_reddit_names"),

    /**
     * Represents the endpoint `POST /api/search_subreddits`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_search_subreddits) for more information */
    POST_SEARCH_SUBREDDITS("POST /api/search_subreddits"),

    /**
     * Represents the endpoint `GET /api/subreddits_by_topic`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_subreddits_by_topic) for more information */
    GET_SUBREDDITS_BY_TOPIC("GET /api/subreddits_by_topic"),

    /**
     * Represents the endpoint `GET /api/v1/user/{username}/trophies`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_user_{username}_trophies) for more information */
    GET_USER_USERNAME_TROPHIES("GET /api/v1/user/{username}/trophies"),

    /**
     * Represents the endpoint `GET /by_id/{names}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_by_id_{names}) for more information */
    GET_BY_ID_NAMES("GET /by_id/{names}"),

    /**
     * Represents the endpoint `GET /comments/{article}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_comments_{article}) for more information */
    GET_COMMENTS_ARTICLE("GET /comments/{article}"),

    /**
     * Represents the endpoint `GET /duplicates/{article}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_duplicates_{article}) for more information */
    GET_DUPLICATES_ARTICLE("GET /duplicates/{article}"),

    /**
     * Represents the endpoint `GET /hot`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_hot) for more information */
    GET_HOT("GET /hot"),

    /**
     * Represents the endpoint `GET /live/{thread}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_live_{thread}) for more information */
    GET_LIVE_THREAD("GET /live/{thread}"),

    /**
     * Represents the endpoint `GET /live/{thread}/about`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_live_{thread}_about) for more information */
    GET_LIVE_THREAD_ABOUT("GET /live/{thread}/about"),

    /**
     * Represents the endpoint `GET /live/{thread}/contributors`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_live_{thread}_contributors) for more information */
    GET_LIVE_THREAD_CONTRIBUTORS("GET /live/{thread}/contributors"),

    /**
     * Represents the endpoint `GET /live/{thread}/discussions`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_live_{thread}_discussions) for more information */
    GET_LIVE_THREAD_DISCUSSIONS("GET /live/{thread}/discussions"),

    /**
     * Represents the endpoint `GET /new`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_new) for more information */
    GET_NEW("GET /new"),

    /**
     * Represents the endpoint `GET /prefs/{where}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_prefs_{where}) for more information */
    GET_PREFS_WHERE("GET /prefs/{where}"),

    /**
     * Represents the endpoint `GET /r/{subreddit}/about`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about) for more information */
    GET_SUBREDDIT_ABOUT("GET /r/{subreddit}/about"),

    /**
     * Represents the endpoint `GET /r/{subreddit}/about/rules`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_r_{subreddit}_about_rules) for more information */
    GET_SUBREDDIT_ABOUT_RULES("GET /r/{subreddit}/about/rules"),

    /**
     * Represents the endpoint `GET /random`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_random) for more information */
    GET_RANDOM("GET /random"),

    /**
     * Represents the endpoint `GET /rising`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_rising) for more information */
    GET_RISING("GET /rising"),

    /**
     * Represents the endpoint `GET /search`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_search) for more information */
    GET_SEARCH("GET /search"),

    /**
     * Represents the endpoint `GET /sidebar`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_sidebar) for more information */
    GET_SIDEBAR("GET /sidebar"),

    /**
     * Represents the endpoint `GET /sticky`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_sticky) for more information */
    GET_STICKY("GET /sticky"),

    /**
     * Represents the endpoint `GET /subreddits/search`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_subreddits_search) for more information */
    GET_SUBREDDITS_SEARCH("GET /subreddits/search"),

    /**
     * Represents the endpoint `GET /subreddits/{where}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_subreddits_{where}) for more information */
    GET_SUBREDDITS_WHERE("GET /subreddits/{where}"),

    /**
     * Represents the endpoint `GET /user/{username}/about`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_user_{username}_about) for more information */
    GET_USER_USERNAME_ABOUT("GET /user/{username}/about"),

    /**
     * Represents the endpoint `GET /users/{where}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_users_{where}) for more information */
    GET_USERS_WHERE("GET /users/{where}"),

    /**
     * Represents the endpoint `GET /{sort}`. Requires OAuth scope 'read'. See [here](https://www.reddit.com/dev/api/oauth#GET_{sort}) for more information */
    GET_SORT("GET /{sort}"),

    /**
     * Represents the endpoint `POST /api/hide`. Requires OAuth scope 'report'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_hide) for more information */
    POST_HIDE("POST /api/hide"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/report`. Requires OAuth scope 'report'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_report) for more information */
    POST_LIVE_THREAD_REPORT("POST /api/live/{thread}/report"),

    /**
     * Represents the endpoint `POST /api/report`. Requires OAuth scope 'report'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_report) for more information */
    POST_REPORT("POST /api/report"),

    /**
     * Represents the endpoint `POST /api/report_user`. Requires OAuth scope 'report'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_report_user) for more information */
    POST_REPORT_USER("POST /api/report_user"),

    /**
     * Represents the endpoint `POST /api/unhide`. Requires OAuth scope 'report'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unhide) for more information */
    POST_UNHIDE("POST /api/unhide"),

    /**
     * Represents the endpoint `POST /api/save`. Requires OAuth scope 'save'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_save) for more information */
    POST_SAVE("POST /api/save"),

    /**
     * Represents the endpoint `GET /api/saved_categories`. Requires OAuth scope 'save'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_saved_categories) for more information */
    GET_SAVED_CATEGORIES("GET /api/saved_categories"),

    /**
     * Represents the endpoint `POST /api/store_visits`. Requires OAuth scope 'save'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_store_visits) for more information */
    POST_STORE_VISITS("POST /api/store_visits"),

    /**
     * Represents the endpoint `POST /api/unsave`. Requires OAuth scope 'save'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_unsave) for more information */
    POST_UNSAVE("POST /api/unsave"),

    /**
     * Represents the endpoint `POST /api/live/create`. Requires OAuth scope 'submit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_create) for more information */
    POST_LIVE_CREATE("POST /api/live/create"),

    /**
     * Represents the endpoint `POST /api/live/{thread}/update`. Requires OAuth scope 'submit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_live_{thread}_update) for more information */
    POST_LIVE_THREAD_UPDATE("POST /api/live/{thread}/update"),

    /**
     * Represents the endpoint `POST /api/submit`. Requires OAuth scope 'submit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_submit) for more information */
    POST_SUBMIT("POST /api/submit"),

    /**
     * Represents the endpoint `GET /api/submit_text`. Requires OAuth scope 'submit'. See [here](https://www.reddit.com/dev/api/oauth#GET_api_submit_text) for more information */
    GET_SUBMIT_TEXT("GET /api/submit_text"),

    /**
     * Represents the endpoint `POST /api/multi/copy`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_multi_copy) for more information */
    POST_MULTI_COPY("POST /api/multi/copy"),

    /**
     * Represents the endpoint `POST /api/multi/rename`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_multi_rename) for more information */
    POST_MULTI_RENAME("POST /api/multi/rename"),

    /**
     * Represents the endpoint `DELETE /api/multi/{multipath}`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#DELETE_api_multi_{multipath}) for more information */
    DELETE_MULTI_MULTIPATH("DELETE /api/multi/{multipath}"),

    /**
     * Represents the endpoint `POST /api/multi/{multipath}`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_multi_{multipath}) for more information */
    POST_MULTI_MULTIPATH("POST /api/multi/{multipath}"),

    /**
     * Represents the endpoint `PUT /api/multi/{multipath}`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath}) for more information */
    PUT_MULTI_MULTIPATH("PUT /api/multi/{multipath}"),

    /**
     * Represents the endpoint `DELETE /api/multi/{multipath}/r/{srname}`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#DELETE_api_multi_{multipath}_r_{srname}) for more information */
    DELETE_MULTI_MULTIPATH_R_SRNAME("DELETE /api/multi/{multipath}/r/{srname}"),

    /**
     * Represents the endpoint `PUT /api/multi/{multipath}/r/{srname}`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#PUT_api_multi_{multipath}_r_{srname}) for more information */
    PUT_MULTI_MULTIPATH_R_SRNAME("PUT /api/multi/{multipath}/r/{srname}"),

    /**
     * Represents the endpoint `POST /api/subscribe`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_subscribe) for more information */
    POST_SUBSCRIBE("POST /api/subscribe"),

    /**
     * Represents the endpoint `DELETE /api/v1/me/friends/{username}`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#DELETE_api_v1_me_friends_{username}) for more information */
    DELETE_ME_FRIENDS_USERNAME("DELETE /api/v1/me/friends/{username}"),

    /**
     * Represents the endpoint `PUT /api/v1/me/friends/{username}`. Requires OAuth scope 'subscribe'. See [here](https://www.reddit.com/dev/api/oauth#PUT_api_v1_me_friends_{username}) for more information */
    PUT_ME_FRIENDS_USERNAME("PUT /api/v1/me/friends/{username}"),

    /**
     * Represents the endpoint `POST /api/vote`. Requires OAuth scope 'vote'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_vote) for more information */
    POST_VOTE("POST /api/vote"),

    /**
     * Represents the endpoint `POST /api/wiki/edit`. Requires OAuth scope 'wikiedit'. See [here](https://www.reddit.com/dev/api/oauth#POST_api_wiki_edit) for more information */
    POST_WIKI_EDIT("POST /api/wiki/edit"),

    /**
     * Represents the endpoint `GET /wiki/discussions/{page}`. Requires OAuth scope 'wikiread'. See [here](https://www.reddit.com/dev/api/oauth#GET_wiki_discussions_{page}) for more information */
    GET_WIKI_DISCUSSIONS_PAGE("GET /wiki/discussions/{page}"),

    /**
     * Represents the endpoint `GET /wiki/pages`. Requires OAuth scope 'wikiread'. See [here](https://www.reddit.com/dev/api/oauth#GET_wiki_pages) for more information */
    GET_WIKI_PAGES("GET /wiki/pages"),

    /**
     * Represents the endpoint `GET /wiki/revisions`. Requires OAuth scope 'wikiread'. See [here](https://www.reddit.com/dev/api/oauth#GET_wiki_revisions) for more information */
    GET_WIKI_REVISIONS("GET /wiki/revisions"),

    /**
     * Represents the endpoint `GET /wiki/revisions/{page}`. Requires OAuth scope 'wikiread'. See [here](https://www.reddit.com/dev/api/oauth#GET_wiki_revisions_{page}) for more information */
    GET_WIKI_REVISIONS_PAGE("GET /wiki/revisions/{page}"),

    /**
     * Represents the endpoint `GET /wiki/{page}`. Requires OAuth scope 'wikiread'. See [here](https://www.reddit.com/dev/api/oauth#GET_wiki_{page}) for more information */
    GET_WIKI_PAGE("GET /wiki/{page}");

    private final String method;

    private final String path;

    Endpoint(final String identifier) {
        String[] parts = identifier.split(" ");
        this.method = parts[0];
        this.path = parts[1];
    }

    /**
     * Gets this Endpoint's path, e.g. `/api/comment` */
    public String getPath() {
        return this.path;
    }

    /**
     * Gets this Endpoint's HTTP method ("GET", "POST", etc.) */
    public String getMethod() {
        return this.method;
    }
}
