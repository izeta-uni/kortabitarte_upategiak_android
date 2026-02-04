package com.example.erronka4

import at.favre.lib.crypto.bcrypt.BCrypt

object Hasher {

    fun hash(password: CharArray, cost: Int = 12): String {
        return BCrypt.withDefaults().hashToString(cost, password)
    }

    fun verify(password: CharArray, hash: String): Boolean {
        return BCrypt.verifyer().verify(password, hash).verified
    }

}