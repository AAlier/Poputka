package neobis.alier.poputchik.util

import neobis.alier.poputchik.model.Info
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ForumService {

    @GET("drivers/")
    fun getListDrivers(): Call<MutableList<Info>>

    @GET("riders/")
    fun getListRiders(): Call<MutableList<Info>>

    @POST("all/")
    fun postData(@Body data: Info): Call<Info>

    @GET("filter/")
    fun filter(@Query("start") start: String,
               @Query("end") end: String,
               @Query("type") type: String): Call<MutableList<Info>>
}