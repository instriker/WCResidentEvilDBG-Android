package com.instriker.wcre.framework

interface IGetterSetter<T> {
    fun get(): T
    fun set(value: T)
}
