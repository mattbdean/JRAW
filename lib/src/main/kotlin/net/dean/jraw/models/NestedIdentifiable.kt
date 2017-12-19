package net.dean.jraw.models

/** An identifiable model that exists in a tree structure and has a parent node. */
interface NestedIdentifiable : Identifiable {
    /** The full name of this model's parent. Something like `t3_xxxxx` (a submission) or `t1_xxxxx` (a comment). */
    val parentFullName: String
}
