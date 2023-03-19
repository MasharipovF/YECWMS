package com.example.yecwms.util


/**
 * Интерфейс, указывающий что объект может быть трансформирован в объект типа T
 *
 * @param <T>
</T> */
interface Transformable<T> {
    fun transform(): T
}