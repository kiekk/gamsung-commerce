package com.loopers.event.handler

interface EventHandler<T> {
    fun handle(event: T)
}
