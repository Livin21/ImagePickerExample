package me.livinmathew.image_picker_example.listener

/**
 *
 * Generic Class To Listen Async Result
 *
 * @author Dhaval Patel
 * @version 1.0
 * @since 04 January 2018
 */
internal interface ResultListener<T> {

    fun onResult(t: T?)
}
