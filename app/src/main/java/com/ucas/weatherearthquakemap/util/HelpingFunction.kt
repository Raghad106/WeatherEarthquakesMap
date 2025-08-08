package com.ucas.weatherearthquakemap.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
object HelpingFunction {


    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    @SuppressLint("UseKtx")
    fun getColoredMarkerIcon(
        context: Context,
        @DrawableRes vectorId: Int,
        @ColorRes colorId: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorId)!!
        val color = ContextCompat.getColor(context, colorId)
        vectorDrawable.setTint(color)

        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun getStartOfDay(): String {
        val todayStart = LocalDate.now(ZoneId.systemDefault())
        val instant = todayStart.atStartOfDay(ZoneId.systemDefault()).toInstant()
        return formatter.format(Date.from(instant))
    }

    fun getEndOfDay(): String {
        val todayEnd = LocalDate.now(ZoneId.systemDefault()).plusDays(1)
        val instant = todayEnd.atStartOfDay(ZoneId.systemDefault()).toInstant()
        return formatter.format(Date.from(instant))
    }

    fun getStartOfYesterday(): String {
        val yesterday = LocalDate.now(ZoneId.systemDefault()).minusDays(1)
        val instant = yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant()
        return formatter.format(Date.from(instant))
    }

    fun getEndOfYesterday(): String {
        val today = LocalDate.now(ZoneId.systemDefault())
        val instant = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
        return formatter.format(Date.from(instant))
    }
    fun getStartOfThisWeek(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        return formatter.format(cal.time)
    }

    fun getStartOfMonth(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -30)
        return formatter.format(cal.time)
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }


    fun hasLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            ContextCompat.checkSelfPermission(context, Constants.FINE_LOCATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Constants.COARSE_LOCATION_PERMISSION
                    ) == PackageManager.PERMISSION_GRANTED
        else
            ContextCompat.checkSelfPermission(context, Constants.BACKGROUND_LOCATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Constants.FINE_LOCATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Constants.COARSE_LOCATION_PERMISSION) ==
                    PackageManager.PERMISSION_GRANTED
}
