package dev.luna5ama.glwrapper.impl

import dev.luna5ama.glwrapper.api.*
import dev.luna5ama.kmogus.Ptr

sealed class BufferObject : IGLObject, IGLTargetBinding {
    override val id = glCreateBuffers()

    var size = 0L; private set

    open fun allocate(size: Long, flags: Int): BufferObject {
        this.size = size
        return this
    }

    open fun allocate(size: Long, data: Ptr, flags: Int): BufferObject {
        this.size = size
        return this
    }

    override fun destroy() {
        glDeleteBuffers(id)
    }

    override fun bind(target: Int) {
        glBindBuffer(target, id)
    }

    override fun unbind(target: Int) {
        glBindBuffer(target, 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BufferObject) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    class Immutable : BufferObject() {
        override fun allocate(size: Long, flags: Int): Immutable {
            super.allocate(size, flags)
            glNamedBufferStorage(id, size, Ptr.NULL, flags)
            return this
        }

        override fun allocate(size: Long, data: Ptr, flags: Int): Immutable {
            super.allocate(size, data, flags)
            glNamedBufferStorage(id, size, data, flags)
            return this
        }
    }

    class Mutable : BufferObject() {
        override fun allocate(size: Long, flags: Int): Mutable {
            super.allocate(size, flags)
            glNamedBufferData(id, size, Ptr.NULL, flags)
            return this
        }

        override fun allocate(size: Long, data: Ptr, flags: Int): Mutable {
            super.allocate(size, data, flags)
            glNamedBufferData(id, size, data, flags)
            return this
        }
    }
}