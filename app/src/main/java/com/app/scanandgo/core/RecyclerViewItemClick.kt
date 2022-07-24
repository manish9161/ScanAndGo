package com.app.scanandgo.core

interface RecyclerViewItemClick<T> {

    fun itemRemove(item: T, position: Int)
}