@file:JvmName("GL20")

package dev.luna5ama.glwrapper.api

import dev.luna5ama.kmogus.Ptr
import dev.luna5ama.kmogus.ensureCapacity
import dev.luna5ama.kmogus.memcpy

interface IGL20 : GLBase {
    companion object {
        internal const val GL_SHADING_LANGUAGE_VERSION = 0x8B8C

        internal const val GL_CURRENT_PROGRAM = 0x8B8D

        internal const val GL_SHADER_TYPE = 0x8B4F
        internal const val GL_DELETE_STATUS = 0x8B80
        internal const val GL_COMPILE_STATUS = 0x8B81
        internal const val GL_LINK_STATUS = 0x8B82
        internal const val GL_VALIDATE_STATUS = 0x8B83
        internal const val GL_INFO_LOG_LENGTH = 0x8B84
        internal const val GL_ATTACHED_SHADERS = 0x8B85
        internal const val GL_ACTIVE_UNIFORMS = 0x8B86
        internal const val GL_ACTIVE_UNIFORM_MAX_LENGTH = 0x8B87
        internal const val GL_ACTIVE_ATTRIBUTES = 0x8B89
        internal const val GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 0x8B8A
        internal const val GL_SHADER_SOURCE_LENGTH = 0x8B88

        internal const val GL_FLOAT_VEC2 = 0x8B50
        internal const val GL_FLOAT_VEC3 = 0x8B51
        internal const val GL_FLOAT_VEC4 = 0x8B52
        internal const val GL_INT_VEC2 = 0x8B53
        internal const val GL_INT_VEC3 = 0x8B54
        internal const val GL_INT_VEC4 = 0x8B55
        internal const val GL_BOOL = 0x8B56
        internal const val GL_BOOL_VEC2 = 0x8B57
        internal const val GL_BOOL_VEC3 = 0x8B58
        internal const val GL_BOOL_VEC4 = 0x8B59
        internal const val GL_FLOAT_MAT2 = 0x8B5A
        internal const val GL_FLOAT_MAT3 = 0x8B5B
        internal const val GL_FLOAT_MAT4 = 0x8B5C
        internal const val GL_SAMPLER_1D = 0x8B5D
        internal const val GL_SAMPLER_2D = 0x8B5E
        internal const val GL_SAMPLER_3D = 0x8B5F
        internal const val GL_SAMPLER_CUBE = 0x8B60
        internal const val GL_SAMPLER_1D_SHADOW = 0x8B61
        internal const val GL_SAMPLER_2D_SHADOW = 0x8B62

        internal const val GL_VERTEX_SHADER = 0x8B31

        internal const val GL_MAX_VERTEX_UNIFORM_COMPONENTS = 0x8B4A
        internal const val GL_MAX_VARYING_FLOATS = 0x8B4B
        internal const val GL_MAX_VERTEX_ATTRIBS = 0x8869
        internal const val GL_MAX_TEXTURE_IMAGE_UNITS = 0x8872
        internal const val GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C
        internal const val GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D

        internal const val GL_VERTEX_PROGRAM_POINT_SIZE = 0x8642

        internal const val GL_VERTEX_ATTRIB_ARRAY_ENABLED = 0x8622
        internal const val GL_VERTEX_ATTRIB_ARRAY_SIZE = 0x8623
        internal const val GL_VERTEX_ATTRIB_ARRAY_STRIDE = 0x8624
        internal const val GL_VERTEX_ATTRIB_ARRAY_TYPE = 0x8625
        internal const val GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 0x886A
        internal const val GL_CURRENT_VERTEX_ATTRIB = 0x8626

        internal const val GL_VERTEX_ATTRIB_ARRAY_POINTER = 0x8645

        internal const val GL_FRAGMENT_SHADER = 0x8B30

        internal const val GL_MAX_FRAGMENT_UNIFORM_COMPONENTS = 0x8B49

        internal const val GL_FRAGMENT_SHADER_DERIVATIVE_HINT = 0x8B8B

        internal const val GL_MAX_DRAW_BUFFERS = 0x8824
        internal const val GL_DRAW_BUFFER0 = 0x8825
        internal const val GL_DRAW_BUFFER1 = 0x8826
        internal const val GL_DRAW_BUFFER2 = 0x8827
        internal const val GL_DRAW_BUFFER3 = 0x8828
        internal const val GL_DRAW_BUFFER4 = 0x8829
        internal const val GL_DRAW_BUFFER5 = 0x882A
        internal const val GL_DRAW_BUFFER6 = 0x882B
        internal const val GL_DRAW_BUFFER7 = 0x882C
        internal const val GL_DRAW_BUFFER8 = 0x882D
        internal const val GL_DRAW_BUFFER9 = 0x882E
        internal const val GL_DRAW_BUFFER10 = 0x882F
        internal const val GL_DRAW_BUFFER11 = 0x8830
        internal const val GL_DRAW_BUFFER12 = 0x8831
        internal const val GL_DRAW_BUFFER13 = 0x8832
        internal const val GL_DRAW_BUFFER14 = 0x8833
        internal const val GL_DRAW_BUFFER15 = 0x8834

        internal const val GL_POINT_SPRITE_COORD_ORIGIN = 0x8CA0

        internal const val GL_LOWER_LEFT = 0x8CA1
        internal const val GL_UPPER_LEFT = 0x8CA2

        internal const val GL_BLEND_EQUATION_RGB = 0x8009
        internal const val GL_BLEND_EQUATION_ALPHA = 0x883D

        internal const val GL_STENCIL_BACK_FUNC = 0x8800
        internal const val GL_STENCIL_BACK_FAIL = 0x8801
        internal const val GL_STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802
        internal const val GL_STENCIL_BACK_PASS_DEPTH_PASS = 0x8803
        internal const val GL_STENCIL_BACK_REF = 0x8CA3
        internal const val GL_STENCIL_BACK_VALUE_MASK = 0x8CA4
        internal const val GL_STENCIL_BACK_WRITEMASK = 0x8CA5
    }

    fun glCreateProgram(): Int
    fun glDeleteProgram(program: Int)
    fun glIsProgram(program: Int): Boolean

    fun glCreateShader(type: Int): Int
    fun glDeleteShader(shader: Int)
    fun glIsShader(shader: Int): Boolean

    fun glAttachShader(program: Int, shader: Int)
    fun glDetachShader(program: Int, shader: Int)
    fun glShaderSource(shader: Int, string: CharSequence)
    fun glShaderSource(shader: Int, vararg string: CharSequence)
    fun glCompileShader(shader: Int)

    fun glLinkProgram(program: Int)
    fun glUseProgram(program: Int)
    fun glValidateProgram(program: Int)

    @Unsafe
    fun glGetShaderiv(shader: Int, pname: Int, params: Long)

    @Unsafe
    fun glGetProgramiv(program: Int, pname: Int, params: Long)

    fun glGetShaderInfoLog(shader: Int, maxLength: Int): String
    fun glGetProgramInfoLog(program: Int, maxLength: Int): String

    fun glGetUniformLocation(program: Int, name: CharSequence): Int

    @Unsafe
    fun glDrawBuffers(n: Int, bufs: Long)

    fun glBlendEquationSeparate(modeRGB: Int, modeAlpha: Int)
    fun glStencilOpSeparate(face: Int, sfail: Int, dpfail: Int, dppass: Int)
    fun glStencilFuncSeparate(face: Int, func: Int, ref: Int, mask: Int)
    fun glStencilMaskSeparate(face: Int, mask: Int)


    fun glGetShaderiv(shader: Int, pname: Int, params: Ptr) {
        glGetShaderiv(shader, pname, params.address)
    }

    fun glGetShaderi(shader: Int, pname: Int): Int {
        val ptr = tempArr.ptr
        glGetShaderiv(shader, pname, ptr)
        return ptr.getInt()
    }

    fun glGetProgramiv(program: Int, pname: Int, params: Ptr) {
        glGetProgramiv(program, pname, params.address)
    }

    fun glGetProgrami(program: Int, pname: Int): Int {
        val ptr = tempArr.ptr
        glGetProgramiv(program, pname, ptr)
        return ptr.getInt()
    }

    fun glDrawBuffers(n: Int, bufs: Ptr) {
        glDrawBuffers(n, bufs.address)
    }

    fun glDrawBuffers(buffer: Int) {
        val ptr = tempArr.ptr
        ptr.setInt(buffer)
        glDrawBuffers(1, ptr)
    }

    fun glDrawBuffers(buffer1: Int, buffer2: Int) {
        val ptr = tempArr.ptr
        ptr.setInt(buffer1)
        ptr.setInt(4, buffer2)
        glDrawBuffers(2, ptr)
    }

    fun glDrawBuffers(buffer1: Int, buffer2: Int, buffer3: Int) {
        val ptr = tempArr.ptr
        ptr.setInt(buffer1)
        ptr.setInt(4, buffer2)
        ptr.setInt(8, buffer3)
        glDrawBuffers(3, ptr)
    }

    fun glDrawBuffers(buffer1: Int, buffer2: Int, buffer3: Int, buffer4: Int) {
        val ptr = tempArr.ptr
        ptr.setInt(buffer1)
        ptr.setInt(4, buffer2)
        ptr.setInt(8, buffer3)
        ptr.setInt(12, buffer4)
        glDrawBuffers(4, ptr)
    }

    fun glDrawBuffers(vararg buffers: Int) {
        tempArr.ensureCapacity(buffers.size * 4L, false)
        val ptr = tempArr.ptr
        memcpy(buffers, ptr, buffers.size * 4L)
    }

    fun glDrawBuffers(buffers: IntArray, offset: Int, length: Int) {
        tempArr.ensureCapacity(length * 4L, false)
        val ptr = tempArr.ptr
        memcpy(buffers, offset * 4L, ptr, 0L, length * 4L)
        glDrawBuffers(length, ptr)
    }
}