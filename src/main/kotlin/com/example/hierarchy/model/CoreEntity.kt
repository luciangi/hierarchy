package com.example.hierarchy.model

import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
class CoreEntity(@Id @GeneratedValue val id: Long? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (javaClass != other?.javaClass) return false
        if (id != (other as CoreEntity).id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
