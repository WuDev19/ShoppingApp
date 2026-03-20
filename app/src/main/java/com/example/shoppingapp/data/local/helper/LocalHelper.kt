package com.example.shoppingapp.data.local.helper

import com.example.shoppingapp.ui.state.StateListener

object LocalHelper {

    suspend fun <T, R> loadFromLocal(
        loadFromLocal: suspend () -> T,
        mapper: (T) -> R
    ) : StateListener<R> {
        return try {
            val data = loadFromLocal()
            if(data != null){
                StateListener.Success(mapper(data))
            }
            else {
                StateListener.Error(NullPointerException("No Data"))
            }
        }
        catch (e: Exception){
            StateListener.Error(e)
        }
    }


}