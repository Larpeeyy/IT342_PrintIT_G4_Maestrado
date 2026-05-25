package com.printit.mobile.core.network

import com.printit.mobile.features.admin.dashboard.AdminDashboardResponse
import com.printit.mobile.features.admin.orders.AdminOrderResponse
import com.printit.mobile.features.admin.payments.AdminPaymentResponse
import com.printit.mobile.features.admin.users.AdminUserResponse
import com.printit.mobile.features.auth.LoginRequest
import com.printit.mobile.features.auth.RegisterRequest
import com.printit.mobile.features.notifications.NotificationResponse
import com.printit.mobile.features.notifications.UnreadNotificationCountResponse
import com.printit.mobile.features.profile.ChangePasswordRequest
import com.printit.mobile.features.profile.ProfileResponse
import com.printit.mobile.features.profile.UpdateProfileRequest
import com.printit.mobile.features.staff.dashboard.StaffDashboardResponse
import com.printit.mobile.features.staff.orders.StaffOrderDetailsResponse
import com.printit.mobile.features.staff.orders.StaffOrderResponse
import com.printit.mobile.features.staff.orders.UpdateStaffOrderStatusRequest
import com.printit.mobile.features.student.dashboard.StudentDashboardResponse
import com.printit.mobile.features.student.neworder.CreateStudentOrderRequest
import com.printit.mobile.features.student.neworder.OrderFileUploadResponse
import com.printit.mobile.features.student.orders.StudentOrderResponse
import com.printit.mobile.features.student.payments.StudentPaymentResponse
import com.printit.mobile.shared.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/register")
    fun registerUser(
        @Body request: RegisterRequest
    ): Call<UserResponse>

    @POST("api/auth/login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<UserResponse>

    @GET("api/student/dashboard")
    fun getStudentDashboard(
        @Query("email") email: String
    ): Call<StudentDashboardResponse>

    @GET("api/student/orders")
    fun getStudentOrders(
        @Query("email") email: String
    ): Call<List<StudentOrderResponse>>

    @Multipart
    @POST("api/student/orders/upload")
    fun uploadStudentOrderFile(
        @Part file: MultipartBody.Part
    ): Call<OrderFileUploadResponse>

    @POST("api/student/orders")
    fun createStudentOrder(
        @Body request: CreateStudentOrderRequest
    ): Call<StudentOrderResponse>

    @GET("api/student/payments")
    fun getStudentPayments(
        @Query("email") email: String
    ): Call<List<StudentPaymentResponse>>

    @GET("api/staff/dashboard")
    fun getStaffDashboard(): Call<StaffDashboardResponse>

    @GET("api/staff/orders")
    fun getStaffOrders(): Call<List<StaffOrderResponse>>

    @GET("api/staff/orders/{orderId}")
    fun getStaffOrderDetails(
        @Path("orderId") orderId: Long
    ): Call<StaffOrderDetailsResponse>

    @GET("api/staff/orders/{orderId}/download")
    fun downloadStaffOrderFile(
        @Path("orderId") orderId: Long
    ): Call<ResponseBody>

    @PUT("api/staff/orders/{orderId}/status")
    fun updateStaffOrderStatus(
        @Path("orderId") orderId: Long,
        @Body request: UpdateStaffOrderStatusRequest
    ): Call<StaffOrderDetailsResponse>

    @GET("api/admin/dashboard")
    fun getAdminDashboard(): Call<AdminDashboardResponse>

    @GET("api/admin/users")
    fun getAdminUsers(): Call<List<AdminUserResponse>>

    @GET("api/admin/payments")
    fun getAdminPayments(): Call<List<AdminPaymentResponse>>

    @GET("api/admin/orders")
    fun getAdminOrders(): Call<List<AdminOrderResponse>>

    @PUT("api/admin/staff/{userId}/approve")
    fun approveStaffRequest(
        @Path("userId") userId: Long
    ): Call<ResponseBody>

    @PUT("api/admin/staff/{userId}/reject")
    fun rejectStaffRequest(
        @Path("userId") userId: Long
    ): Call<ResponseBody>

    @GET("api/notifications")
    fun getNotifications(
        @Query("email") email: String
    ): Call<List<NotificationResponse>>

    @GET("api/notifications/unread-count")
    fun getUnreadNotificationCount(
        @Query("email") email: String
    ): Call<UnreadNotificationCountResponse>

    @PUT("api/notifications/{notificationId}/read")
    fun markNotificationAsRead(
        @Path("notificationId") notificationId: Long
    ): Call<ResponseBody>

    @GET("api/profile")
    fun getProfile(
        @Query("email") email: String
    ): Call<ProfileResponse>

    @PUT("api/profile")
    fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Call<ProfileResponse>

    @PUT("api/profile/change-password")
    fun changePassword(
        @Body request: ChangePasswordRequest
    ): Call<ResponseBody>
}