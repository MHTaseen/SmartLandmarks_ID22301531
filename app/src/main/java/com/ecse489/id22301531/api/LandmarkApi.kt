package com.ecse489.id22301531.api

import com.ecse489.id22301531.model.Landmark
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface LandmarkApi {
    @GET("api.php")
    suspend fun getLandmarks(
        @Query("action") action: String = "get_landmarks",
        @Query("key") key: String
    ): Response<List<Landmark>>

    @POST("api.php")
    suspend fun visitLandmark(
        @Query("action") action: String = "visit_landmark",
        @Query("key") key: String,
        @Body body: VisitRequest
    ): Response<VisitResponse>

    @Multipart
    @POST("api.php")
    suspend fun createLandmark(
        @Query("action") action: String = "create_landmark",
        @Query("key") key: String,
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<StatusResponse>

    @FormUrlEncoded
    @POST("api.php")
    suspend fun deleteLandmark(
        @Query("action") action: String = "delete_landmark",
        @Query("key") key: String,
        @Field("id") id: Int
    ): Response<StatusResponse>
}

data class VisitRequest(
    val landmark_id: Int,
    val user_lat: Double,
    val user_lon: Double
)

data class VisitResponse(
    val status: String,
    val message: String,
    val distance: Double? = null
)

data class StatusResponse(
    val status: String,
    val message: String
)