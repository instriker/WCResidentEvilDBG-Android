package com.instriker.wcre.services

import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException

class PackageService(private val _context: Activity) {

    val versionName: String
        get() {
            var versionName: String
            try {
                val packageInfo = _context.packageManager.getPackageInfo(_context.packageName, 0)
                versionName = packageInfo.versionName
            } catch (ex: NameNotFoundException) {
                versionName = "99.9.9.9999"
            }

            return versionName
        }

    val versionCode: Int
        get() {
            var versionCode: Int
            try {
                val packageInfo = _context.packageManager.getPackageInfo(_context.packageName, 0)
                versionCode = packageInfo.versionCode
            } catch (ex: NameNotFoundException) {
                versionCode = -1
            }

            return versionCode
        }
}
