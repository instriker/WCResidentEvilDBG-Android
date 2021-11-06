package com.instriker.wcre.repositories

import java.util.HashMap

class GameExtension(val id: Int, var name: String) {
    companion object {
        fun toMap(extensions: Array<GameExtension>): HashMap<Int, GameExtension> {
            val extensionsById = HashMap<Int, GameExtension>()
            for (extension in extensions) {
                extensionsById.put(extension.id, extension)
            }
            return extensionsById
        }
    }
}
