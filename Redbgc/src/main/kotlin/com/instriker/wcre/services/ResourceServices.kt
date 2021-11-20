package com.instriker.wcre.services

import com.instriker.wcre.config.Constants

import android.content.Context
import android.graphics.drawable.Drawable

class ResourceServices(private val _context: Context) {

    fun getStringArray(id: Int): Array<String> {
        return this._context.resources.getStringArray(id)
    }

    fun getColor(id: Int): Int {
        return this._context.resources.getColor(id)
    }

    fun getString(id: Int): String {
        return this._context.resources.getString(id)
    }

    fun getDrawable(id: Int): Drawable {
        return this._context.resources.getDrawable(id)
    }

    fun getDrawableId(name: String): Int {
        return this._context.resources.getIdentifier(name, "drawable", Constants.PackageName)
    }
}
