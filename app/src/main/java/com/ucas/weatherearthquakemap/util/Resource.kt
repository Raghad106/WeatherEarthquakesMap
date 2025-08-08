package com.ucas.weatherearthquakemap.util
/*
out: This makes T a covariant type, meaning you can use Resource<T> as Resource<Any>
if needed. It’s safe because T is only used as an output (you only return it,
not modify it)
 */
sealed class Resource<out T> {
    /*
    This subclass holds the successful result of a data request.
    It's still generic like the parent Resource<T>.
    : Resource<T>() This is the actual response data returned from the API call
    */
    data class Success<out T>(val data:T): Resource<T>()

    /*
    Represents the error state when something went wrong.
    val message: String:
    This holds the error message — for example, "Network error" or
    "500 Internal Server Error".
    : Resource<Nothing>():
    Nothing means this subclass will never return a value
    (there is no data when an error happens).
    It’s Kotlin's way of saying “this doesn’t produce a usable result.”
     */
    data class Error(val message:String): Resource<Nothing>()

    /*
    Represents a singleton — only one instance of Loading will exist in the entire app.
    Loading:
    Represents a loading state, like when you're waiting for the API to return data.
    : Resource<Nothing>():
    No result yet, because loading is in progress.
     */
    object Loading: Resource<Nothing>()
}