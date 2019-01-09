package de.iteratec.iteraOfficeMap.persistence

import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AbstractJpaPersistable<T : Serializable> {

    companion object {
        @Suppress("unused")
        private const val serialVersionUID = -5554308939380869754L
    }

    @Id
    @GeneratedValue
    var id: T? = null

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this === other) {
            return true
        }

        if (javaClass != ProxyUtils.getUserClass(other)) {
            return false
        }

        other as AbstractJpaPersistable<*>

        return when (id) {
            null -> false
            else -> id == other.id
        }
    }

    override fun hashCode() = 31

    override fun toString() = "Entity of type ${this.javaClass.name} with id: $id"

}
