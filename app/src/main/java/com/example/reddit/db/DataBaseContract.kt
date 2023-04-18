package com.example.reddit.db

object DataBaseContract {
    object RedditPostContract {
        const val TABLE_NAME = "reddit_post"
        object Columns {
            const val NAME = "name"
            const val AUTHOR = "author"
            const val TITLE = "title"
            const val NUM_COMMENTS = "num_comments"
            const val CREATED_AT = "created_at"
            const val SUBREDDIT_SELF_TEXT = "subreddit_self_text"
            const val SCORE = "score"
            const val URL = "url"
            const val SUBREDDIT_ID = "subreddit_id"
            const val IS_EXPANDABLE = "is_expandable"
            const val IS_SUBSCRIBED = "is_subscribed"
            const val CATEGORY = "post_category"
        }
    }

    object RedditorContract {
        const val TABLE_NAME = "reddit_user"
        object Columns {
            const val NAME = "name"
            const val IS_FRIEND = "is_friend"
        }

        object RedditorSub {
            const val TABLE_NAME = "redditor_subreddit"
            object Columns {
                const val ID = "id"
                const val ID_SUBREDDIT = "subreddit_id"
                const val NAME_PREFIXED = "name_prefixed"
                const val AVATAR_IMG = "avatar_img"
            }
        }
    }
}