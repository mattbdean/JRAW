package net.dean.jraw.models

interface NestedIdentifiable : Identifiable {
    /** The full name of this model's parent. Something like `t3_xxxxx` (a submission) or `t1_xxxxx` (a comment). */
    val parentFullName: String
}
