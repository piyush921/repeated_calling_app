package com.first.project

data class DessertModel(
    val list: List<Model>
)

data class Model(
    val id: String,
    val member: String,
    val boat: String,
    val date: String,
    val start: String,
    val end: String,
    val location: String,
    val type: String,
    val sp: String,
    val c: String,
    val u: String,
    val rdf: String,
    val checked_out: String,
    val checked_in: String,
    val released: String,
    val gas: String,
    val notes: String,
    val fee: String,
    val oner: String,
    val overrides: String
)
