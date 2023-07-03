@file:JvmName("GL14")

package dev.luna5ama.glwrapper.api

import dev.luna5ama.kmogus.Ptr

interface IGL14 : GLBase {
    companion object {
        internal const val GL_CONSTANT_COLOR = 0x8001
        internal const val GL_ONE_MINUS_CONSTANT_COLOR = 0x8002
        internal const val GL_CONSTANT_ALPHA = 0x8003
        internal const val GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004

        internal const val GL_FUNC_ADD = 0x8006
        internal const val GL_MIN = 0x8007
        internal const val GL_MAX = 0x8008

        internal const val GL_FUNC_SUBTRACT = 0x800A
        internal const val GL_FUNC_REVERSE_SUBTRACT = 0x800B

        internal const val GL_DEPTH_COMPONENT16 = 0x81A5
        internal const val GL_DEPTH_COMPONENT24 = 0x81A6
        internal const val GL_DEPTH_COMPONENT32 = 0x81A7

        internal const val GL_TEXTURE_DEPTH_SIZE = 0x884A

        internal const val GL_TEXTURE_COMPARE_MODE = 0x884C
        internal const val GL_TEXTURE_COMPARE_FUNC = 0x884D

        internal const val GL_POINT_FADE_THRESHOLD_SIZE = 0x8128

        internal const val GL_BLEND_DST_RGB = 0x80C8
        internal const val GL_BLEND_SRC_RGB = 0x80C9
        internal const val GL_BLEND_DST_ALPHA = 0x80CA
        internal const val GL_BLEND_SRC_ALPHA = 0x80CB

        internal const val GL_INCR_WRAP = 0x8507
        internal const val GL_DECR_WRAP = 0x8508

        internal const val GL_TEXTURE_LOD_BIAS = 0x8501

        internal const val GL_MAX_TEXTURE_LOD_BIAS = 0x84FD

        internal const val GL_MIRRORED_REPEAT = 0x8370
    }

    fun glBlendColor(red: Float, green: Float, blue: Float, alpha: Float)

    fun glBlendEquation(mode: Int)

    @Unsafe
    fun glMultiDrawArrays(mode: Int, first: Long, count: Long, drawcount: Int)

    @Unsafe
    fun glMultiDrawElements(mode: Int, count: Long, type: Int, indices: Long, drawcount: Int)

    @Unsafe
    fun glPointParameterfv(pname: Int, params: Long)

    @Unsafe
    fun glPointParameteriv(pname: Int, params: Long)

    fun glBlendFuncSeparate(sfactorRGB: Int, dfactorRGB: Int, sfactorAlpha: Int, dfactorAlpha: Int)

    fun glMultiDrawArrays(mode: Int, first: Ptr, count: Ptr, drawcount: Int) {
        glMultiDrawArrays(mode, first.address, count.address, drawcount)
    }

    fun glMultiDrawElements(mode: Int, count: Ptr, type: Int, indices: Ptr, drawcount: Int) {
        glMultiDrawElements(mode, count.address, type, indices.address, drawcount)
    }

    fun glPointParameterfv(pname: Int, params: Ptr) {
        glPointParameterfv(pname, params.address)
    }

    fun glPointParameterf(pname: Int, param: Float) {
        val ptr = tempArr.ptr
        ptr.setFloat(param)
        glPointParameterfv(pname, ptr)
    }

    fun glPointParameteriv(pname: Int, params: Ptr) {
        glPointParameteriv(pname, params.address)
    }

    fun glPointParameteri(pname: Int, param: Int) {
        val ptr = tempArr.ptr
        ptr.setInt(param)
        glPointParameteriv(pname, ptr)
    }
}