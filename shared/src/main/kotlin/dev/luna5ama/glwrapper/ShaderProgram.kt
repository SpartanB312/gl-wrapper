package dev.luna5ama.glwrapper

import dev.luna5ama.glwrapper.base.*
import dev.luna5ama.glwrapper.enums.BufferTarget
import dev.luna5ama.glwrapper.enums.GLObjectType
import dev.luna5ama.glwrapper.enums.GLSLDataType
import dev.luna5ama.glwrapper.enums.ShaderStage
import dev.luna5ama.glwrapper.objects.IGLBinding
import dev.luna5ama.glwrapper.objects.IGLObject
import dev.luna5ama.kmogus.MemoryStack
import dev.luna5ama.kmogus.Ptr
import dev.luna5ama.kmogus.memcpy
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.*

open class ShaderProgram private constructor(
    val shaderSources: List<ShaderSource>,
) : IGLObject, IGLBinding {

    constructor(vararg shaderSources: ShaderSource) : this(shaderSources.toList())

    final override var id = 0; private set
    override val type: GLObjectType
        get() = GLObjectType.Program

    final override var label: String = ""
        set(value) {
            if (field != value) {
                if (value.isNotEmpty()) {
                    glObjectLabel(type.identifier, id, value)
                } else {
                    glObjectLabel(type.identifier, id, 0, Ptr.NULL)
                }
            }
            field = value
        }

    private val shaderStages: Set<ShaderStage> = shaderSources.mapTo(EnumSet.noneOf(ShaderStage::class.java)) {
        it.shaderStage ?: throw IllegalArgumentException("Shader type is not specified $it")
    }

    private lateinit var resources: Resources

    init {
        initialize()
    }

    fun reload() {
        destroy()
        initialize()
    }

    private fun initialize() {
        fun createShader(source: ShaderSource): Int {
            val id = glCreateShader(source.shaderStage!!.value)

            val codeSrc = source.resolveCodeSrc()
            glShaderSource(id, codeSrc)
            glCompileShader(id)

            val compiled = glGetShaderi(id, GL_COMPILE_STATUS)
            if (compiled == 0) {
                System.err.print(buildString {
                    append(glGetShaderInfoLog(id, glGetShaderi(id, GL_INFO_LOG_LENGTH)))
                    appendLine("Shader source:")
                    codeSrc.lineSequence().forEachIndexed { i, it ->
                        append(i + 1)
                        append('\t')
                        appendLine(it)
                    }
                })
                System.err.flush()
                glDeleteShader(id)
                throw IllegalStateException("Failed to compile shader: $source")
            }

            return id
        }

        val programID = glCreateProgram()
        if (label != null) {
            glObjectLabel(type.identifier, programID, label!!)
        }
        val shaderIDs = IntArray(shaderSources.size) { i ->
            createShader(shaderSources[i]).also {
                glAttachShader(programID, it)
            }
        }

        glLinkProgram(programID)
        val linked = glGetProgrami(programID, GL_LINK_STATUS)
        if (linked == 0) {
            System.err.print(glGetProgramInfoLog(programID, glGetProgrami(programID, GL_INFO_LOG_LENGTH)))
            glDeleteProgram(programID)
            throw IllegalStateException("Shader program failed to link")
        }
        this.id = programID

        shaderIDs.forEach {
            glDetachShader(programID, it)
            glDeleteShader(it)
        }

        resources = Resources(programID)
    }

    fun applyBinding(spec: ShaderBindingSpecs) {
        runCatching {
            resources.samplerBindings.apply(spec)
            resources.imageBindings.apply(spec)
            resources.bufferBindings.apply(spec)
        }.onFailure {
            it.printStackTrace()
        }
    }

    inline fun applyBinding(block: ShaderBindingSpecs.Builder.() -> Unit) {
        applyBinding(ShaderBindingSpecs.Builder().apply(block).build())
    }

    override fun create() {
        throw UnsupportedOperationException("Shader program cannot be created manually")
    }

    override fun tryCreate() {
        throw UnsupportedOperationException("Shader program cannot be created manually")
    }

    override fun checkCreated() {
        // do nothing
    }

    override fun resetID() {
        throw UnsupportedOperationException("Shader program cannot be reset manually")
    }

    fun uniform1i(name: String, value: Int) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform1i(id, entry, value)
    }

    fun uniform1iv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform1iv(id, entry, count, value)
    }

    fun uniform2i(name: String, value1: Int, value2: Int) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform2i(id, entry, value1, value2)
    }

    fun uniform2iv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform2iv(id, entry, count, value)
    }

    fun uniform3i(name: String, value1: Int, value2: Int, value3: Int) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform3i(id, entry, value1, value2, value3)
    }

    fun uniform3iv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform3iv(id, entry, count, value)
    }

    fun uniform4i(name: String, value1: Int, value2: Int, value3: Int, value4: Int) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform4i(id, entry, value1, value2, value3, value4)
    }

    fun uniform4iv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform4iv(id, entry, count, value)
    }

    fun uniform1ui(name: String, value: Int) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform1ui(id, entry, value)
    }

    fun uniform1uiv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform1uiv(id, entry, count, value)
    }

    fun uniform2ui(name: String, value1: Int, value2: Int) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform2ui(id, entry, value1, value2)
    }

    fun uniform2uiv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform2uiv(id, entry, count, value)
    }

    fun uniform3ui(name: String, value1: Int, value2: Int, value3: Int) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform3ui(id, entry, value1, value2, value3)
    }

    fun uniform3uiv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform3uiv(id, entry, count, value)
    }

    fun uniform4ui(name: String, value1: Int, value2: Int, value3: Int, value4: Int) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform4ui(id, entry, value1, value2, value3, value4)
    }

    fun uniform4uiv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform4uiv(id, entry, count, value)
    }

    fun uniformHandle(name: String, value: Long) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformHandleui64ARB(id, entry, value)
    }

    fun uniformHandle(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformHandleui64vARB(id, entry, count, value)
    }

    fun uniform1f(name: String, value: Float) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform1f(id, entry, value)
    }

    fun uniform1fv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform1fv(id, entry, count, value)
    }

    fun uniform2f(name: String, value1: Float, value2: Float) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform2f(id, entry, value1, value2)
    }

    fun uniform2fv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform2fv(id, entry, count, value)
    }

    fun uniform3f(name: String, value1: Float, value2: Float, value3: Float) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform3f(id, entry, value1, value2, value3)
    }

    fun uniform3fv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform3fv(id, entry, count, value)
    }

    fun uniform4f(name: String, value1: Float, value2: Float, value3: Float, value4: Float) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform4f(id, entry, value1, value2, value3, value4)
    }

    fun uniform4fv(name: String, count: Int, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniform4fv(id, entry, count, value)
    }

    fun uniformMatrix2fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix2fv(id, entry, count, transpose, value)
    }

    fun uniformMatrix3fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix3fv(id, entry, count, transpose, value)
    }

    fun uniformMatrix4fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix4fv(id, entry, count, transpose, value)
    }

    fun uniformMatrix2x3fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix2x3fv(id, entry, count, transpose, value)
    }

    fun uniformMatrix3x2fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix3x2fv(id, entry, count, transpose, value)
    }

    fun uniformMatrix2x4fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix2x4fv(id, entry, count, transpose, value)
    }

    fun uniformMatrix4x2fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix4x2fv(id, entry, count, transpose, value)
    }

    fun uniformMatrix3x4fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix3x4fv(id, entry, count, transpose, value)
    }

    fun uniformMatrix4x3fv(name: String, count: Int, transpose: Boolean, value: Ptr) {
        val entry = resources.locateUniform(name)?.location ?: return
        glProgramUniformMatrix4x3fv(id, entry, count, transpose, value)
    }

    override fun bind() {
        glUseProgram(id)
    }

    override fun unbind() {
        glUseProgram(0)
    }

    override fun destroy() {
        glDeleteProgram(id)
    }

    internal inner class Resources(val programID: Int) {
        private var uniformLookUpCacheName: String? = null
        private var uniformLookUpCache: ResourceInterface.Uniform.Entry? = null

        fun locateUniform(name: String): ResourceInterface.Uniform.Entry? {
            if (uniformLookUpCacheName == name) {
                return uniformLookUpCache
            }

            val entry = resources.uniformResource.nameToEntryMap[name]
            require(!Config.checks || entry != null) { "Uniform not found: $name" }

            uniformLookUpCacheName = name
            uniformLookUpCache = entry

            return entry
        }

        val uniformResource = ResourceInterface.Uniform(this)
        val uniformBlockResource = ResourceInterface.UniformBlock(this)
        val shaderStorageBlockResource = ResourceInterface.ShaderStorageBlock(this)

        val samplerBindings = BindingManager.Samplers(this)
        val imageBindings = BindingManager.Images(this)
        val bufferBindings = BindingManager.Buffers(this)
    }

    sealed class ResourceInterface<T : ResourceInterface.Entry> {
        abstract val entries: Int2ObjectMap<T>

        operator fun get(index: Int): T = entries[index]

        sealed interface Entry {
            val index: Int
        }

        class Uniform internal constructor(resources: Resources) : ResourceInterface<Uniform.Entry>() {
            data class Entry(
                override val index: Int,
                val name: String,
                val location: Int,
                val blockIndex: Int,
                val type: GLSLDataType,
                val arraySize: Int,
                val arrayStride: Int,
                val matrixStride: Int,
            ) : ResourceInterface.Entry

            override val entries: Int2ObjectMap<Entry>
            val nameToEntryMap: Object2ObjectMap<String, Entry>

            init {
                val entries = Int2ObjectOpenHashMap<Entry>()
                val nameToEntryMap = Object2ObjectOpenHashMap<String, Entry>()
                MemoryStack {
                    val propCount = 6
                    val properties = malloc(propCount * 4L).ptr
                    properties.setIntInc(GL_BLOCK_INDEX)
                        .setIntInc(GL_LOCATION)
                        .setIntInc(GL_TYPE)
                        .setIntInc(GL_ARRAY_SIZE)
                        .setIntInc(GL_ARRAY_STRIDE)
                        .setIntInc(GL_MATRIX_STRIDE)

                    val values = malloc(propCount * 4L).ptr

                    iterateResourceNamedIndex(resources.programID, GL_UNIFORM) { (i, name) ->
                        glGetProgramResourceiv(
                            resources.programID,
                            GL_UNIFORM,
                            i,
                            propCount,
                            properties,
                            propCount,
                            Ptr.NULL,
                            values
                        )
                        val blockIndex = values.getInt(0)
                        val location = values.getInt(4)
                        val type = GLSLDataType[values.getInt(8)]
                        val arraySize = values.getInt(12)
                        val arrayStride = values.getInt(16)
                        val matrixStride = values.getInt(20)

                        val entry = Entry(
                            index = i,
                            name = name,
                            location = location,
                            blockIndex = blockIndex,
                            type = type,
                            arraySize = arraySize,
                            arrayStride = arrayStride,
                            matrixStride = matrixStride
                        )
                        nameToEntryMap.put(name, entry)
                        entries.put(i, entry)
                    }
                }
                this.entries = Int2ObjectMaps.unmodifiable(entries)
                this.nameToEntryMap = Object2ObjectMaps.unmodifiable(nameToEntryMap)
            }
        }

        class UniformBlock internal constructor(resources: Resources) : ResourceInterface<UniformBlock.Entry>() {
            data class Entry(
                override val index: Int,
                val name: String,
                val bindingIndex: Int,
                val dataSize: Int,
                val numActiveVariables: Int,
                val activeVariableIndices: List<Int>,
            ) : ResourceInterface.Entry

            override val entries: Int2ObjectMap<Entry>

            init {
                val entries = Int2ObjectOpenHashMap<Entry>()
                MemoryStack {
                    val propCount = 2
                    val properties = malloc(propCount * 4L).ptr
                    properties.setIntInc(GL_BUFFER_DATA_SIZE)
                        .setIntInc(GL_NUM_ACTIVE_VARIABLES)
                    val values = malloc(propCount * 4L).ptr

                    val temp = malloc(1 * 4L).ptr
                    glGetProgramInterfaceiv(resources.programID, GL_UNIFORM_BLOCK, GL_MAX_NUM_ACTIVE_VARIABLES, temp)
                    val maxNumActiveVariables = temp.getInt()

                    val activeVariableIndicesPtr = malloc(maxNumActiveVariables * 4L).ptr
                    var bindingIndex = 0

                    iterateResourceNamedIndex(resources.programID, GL_UNIFORM_BLOCK) { (i, name) ->
                        glGetProgramResourceiv(
                            resources.programID, GL_UNIFORM_BLOCK, i,
                            propCount, properties,
                            propCount, Ptr.NULL, values
                        )
                        val dataSize = values.getInt(0)
                        val numActiveVariables = values.getInt(4)
                        glGetProgramResourceiv(
                            resources.programID,
                            GL_UNIFORM_BLOCK,
                            i,
                            1,
                            properties,
                            1,
                            Ptr.NULL,
                            activeVariableIndicesPtr
                        )

                        val activeVariableIndices = List(numActiveVariables) { j ->
                            activeVariableIndicesPtr.getInt(j * 4L)
                        }

                        glUniformBlockBinding(resources.programID, i, bindingIndex)
                        entries.put(
                            i, Entry(
                                index = i,
                                name = name,
                                bindingIndex = bindingIndex,
                                dataSize = dataSize,
                                numActiveVariables = numActiveVariables,
                                activeVariableIndices = activeVariableIndices
                            )
                        )
                        bindingIndex++
                    }
                }
                this.entries = Int2ObjectMaps.unmodifiable(entries)
            }
        }

        class ShaderStorageBlock internal constructor(resources: Resources) :
            ResourceInterface<ShaderStorageBlock.Entry>() {
            data class Entry(
                override val index: Int,
                val name: String,
                val bindingIndex: Int,
                val dataSize: Int,
                val numActiveVariables: Int,
                val activeVariableIndices: List<Int>,
            ) : ResourceInterface.Entry

            override val entries: Int2ObjectMap<Entry>

            init {
                val entries = Int2ObjectOpenHashMap<Entry>()
                MemoryStack {
                    val propN = 2
                    val properties = malloc(propN * 4L).ptr
                    properties.setIntInc(GL_BUFFER_DATA_SIZE)
                        .setIntInc(GL_NUM_ACTIVE_VARIABLES)
                    val values = malloc(propN * 4L).ptr

                    val temp = malloc(1 * 4L).ptr
                    glGetProgramInterfaceiv(
                        resources.programID,
                        GL_SHADER_STORAGE_BLOCK,
                        GL_MAX_NUM_ACTIVE_VARIABLES,
                        temp
                    )
                    val maxNumActiveVariables = temp.getInt()

                    val activeVariableIndicesPtr = calloc(maxNumActiveVariables * 4L).ptr
                    var bindingIndex = 0

                    iterateResourceNamedIndex(resources.programID, GL_SHADER_STORAGE_BLOCK) { (i, name) ->
                        glGetProgramResourceiv(
                            resources.programID,
                            GL_SHADER_STORAGE_BLOCK,
                            i,
                            propN,
                            properties,
                            propN,
                            Ptr.NULL,
                            values
                        )
                        val dataSize = values.getInt(0)
                        val numActiveVariables = values.getInt(4)
                        glGetProgramResourceiv(
                            resources.programID,
                            GL_SHADER_STORAGE_BLOCK,
                            i,
                            1,
                            properties,
                            1,
                            Ptr.NULL,
                            activeVariableIndicesPtr
                        )

                        val activeVariableIndices = List(numActiveVariables) { j ->
                            activeVariableIndicesPtr.getInt(j * 4L)
                        }


                        glShaderStorageBlockBinding(resources.programID, i, bindingIndex)
                        entries.put(
                            i, Entry(
                                index = i,
                                name = name,
                                bindingIndex = bindingIndex,
                                dataSize = dataSize,
                                numActiveVariables = numActiveVariables,
                                activeVariableIndices = activeVariableIndices
                            )
                        )
                        bindingIndex++
                    }
                }
                this.entries = Int2ObjectMaps.unmodifiable(entries)
            }
        }

        class Subroutine internal constructor(resources: Resources, stage: ShaderStage) :
            ResourceInterface<Subroutine.Entry>() {
            data class Entry(
                override val index: Int,
                val name: String
            ) : ResourceInterface.Entry

            override val entries: Int2ObjectMap<Entry>

            init {
                val entries = Int2ObjectOpenHashMap<Entry>()
                MemoryStack {
                    iterateResourceNamedIndex(resources.programID, stage.subroutine.value) { (i, name) ->
                        entries.put(i, Entry(i, name))
                    }
                }
                this.entries = Int2ObjectMaps.unmodifiable(entries)
            }
        }

        private companion object {
            fun iterateResourceNamedIndex(
                program: Int,
                resourceType: Int,
                block: (IndexedValue<String>) -> Unit,
            ) {
                MemoryStack {
                    val properties = malloc(1 * 4L).ptr
                    val values = malloc(1 * 4L).ptr

                    properties.setIntInc(GL_NAME_LENGTH)
                    val singleBuffer = malloc(4L).ptr
                    glGetProgramInterfaceiv(program, resourceType, GL_ACTIVE_RESOURCES, singleBuffer)
                    val numUniforms = singleBuffer.getInt()
                    glGetProgramInterfaceiv(program, resourceType, GL_MAX_NAME_LENGTH, singleBuffer)
                    val maxNameLength = singleBuffer.getInt()
                    val nameBuffer = malloc(maxNameLength.toLong()).ptr
                    val byteArray = ByteArray(maxNameLength)

                    for (i in 0 until numUniforms) {
                        glGetProgramResourceiv(program, resourceType, i, 1, properties, 1, Ptr.NULL, values)
                        val nameLength = values.getInt(0)

                        glGetProgramResourceName(program, resourceType, i, maxNameLength, Ptr.NULL, nameBuffer)
                        memcpy(nameBuffer, 0L, byteArray, 0L, nameLength.toLong())
                        val name = String(byteArray, 0, nameLength - 1)

                        block(IndexedValue(i, name))
                    }
                }
            }

            fun iterateResourceIndex(
                program: Int,
                resourceType: Int,
                block: (Int) -> Unit,
            ) {
                MemoryStack {
                    val singleBuffer = malloc(4L).ptr
                    glGetProgramInterfaceiv(program, resourceType, GL_ACTIVE_RESOURCES, singleBuffer)
                    val numUniforms = singleBuffer.getInt()
                    for (i in 0 until numUniforms) {
                        block(i)
                    }
                }
            }
        }
    }

    sealed class BindingManager {
        internal abstract fun apply(specs: ShaderBindingSpecs)

        data class BindingPoint(val name: String, val index: Int)

        class Samplers internal constructor(resources: Resources) : BindingManager() {
            private val bindingPoints = mutableListOf<BindingPoint>()

            init {
                var bindingIndex = 0
                for (entry in resources.uniformResource.entries.values) {
                    if (entry.type !is GLSLDataType.Opaque.Sampler) continue
                    if (entry.blockIndex != -1) continue
                    glProgramUniform1i(resources.programID, entry.location, bindingIndex)
                    bindingPoints.add(BindingPoint(entry.name, bindingIndex))
                    bindingIndex++
                }
            }

            override fun apply(specs: ShaderBindingSpecs) {
                MemoryStack {
                    val bindings = specs.samplers
                    val count = bindingPoints.size
                    val textures = malloc(count * 4L).ptr
                    val samplers = malloc(count * 4L).ptr
                    for ((name, index) in bindingPoints) {
                        val binding = bindings[name]
                        require(binding != null) { "Missing binding for sampler unit: $name" }
                        textures.setInt(index * 4L, binding.texture)
                        samplers.setInt(index * 4L, binding.sampler?.id ?: 0)
                    }
                    glBindTextures(0, count, textures)
                    glBindSamplers(0, count, samplers)
                }
            }
        }

        class Images internal constructor(resources: Resources) : BindingManager() {
            private val bindingPoints = mutableListOf<BindingPoint>()

            init {
                var bindingIndex = 0
                for (entry in resources.uniformResource.entries.values) {
                    if (entry.type !is GLSLDataType.Opaque.Image) continue
                    glProgramUniform1i(resources.programID, entry.location, bindingIndex)
                    bindingPoints.add(BindingPoint(entry.name, bindingIndex))
                    bindingIndex++
                }
            }

            override fun apply(specs: ShaderBindingSpecs) {
                MemoryStack {
                    val bindings = specs.images
                    val count = bindingPoints.size
                    val textures = malloc(count * 4L).ptr
                    for ((name, index) in bindingPoints) {
                        val binding = bindings[name]
                        require(binding != null) { "Missing binding for image unit: $name" }
                        textures.setInt(index * 4L, binding.texture)
                    }
                    glBindImageTextures(0, count, textures)
                }
            }
        }

        class Buffers internal constructor(resources: Resources) : BindingManager() {
            private val bindingPointMap = Object2ObjectOpenHashMap<BufferTarget.Shader, MutableList<BindingPoint>>()

            init {
                for (entry in resources.shaderStorageBlockResource.entries.values) {
                    bindingPointMap.getOrPut(BufferTarget.ShaderStorage, ::mutableListOf)
                        .add(BindingPoint(entry.name, entry.bindingIndex))
                }
                for (entry in resources.uniformBlockResource.entries.values) {
                    bindingPointMap.getOrPut(BufferTarget.Uniform, ::mutableListOf)
                        .add(BindingPoint(entry.name, entry.bindingIndex))
                }
            }

            override fun apply(specs: ShaderBindingSpecs) {
                val bindingMap = specs.buffers
                for ((target, bindingPoints) in bindingPointMap) {
                    val bindings = bindingMap[target]
                    require(bindings != null) { "Missing bindings for targer: $target" }
                    MemoryStack {
                        val targetBindingCount = bindingPoints.size
                        val buffers = malloc(targetBindingCount * 4L).ptr
                        val offsets = malloc(targetBindingCount * 8L).ptr
                        val sizes = malloc(targetBindingCount * 8L).ptr
                        for ((name, index) in bindingPoints) {
                            val binding = bindings[name]
                            require(binding != null) { "Missing binding for $target buffer block: $name" }
                            check(index < targetBindingCount)
                            check(index >= 0)
                            val bufferView = binding.bufferView
                            val bindingSize = if (bufferView.viewSize == -1L) {
                                bufferView.viewBuffer.size - bufferView.viewOffset
                            } else {
                                bufferView.viewSize
                            }
                            check(bufferView.viewBuffer.id != 0)
                            check(bufferView.viewOffset < bufferView.viewBuffer.size)
                            check(bufferView.viewOffset + bindingSize <= bufferView.viewBuffer.size)
                            buffers.setInt(index * 4L, bufferView.viewBuffer.id)
                            offsets.setLong(index * 8L, bufferView.viewOffset)
                            sizes.setLong(index * 8L, bindingSize)
                        }
                        glBindBuffersRange(target.value, 0, targetBindingCount, buffers, offsets, sizes)
                    }
                }
            }
        }
    }

    abstract class Variants<K> {
        private val map = Object2ObjectOpenHashMap<K, ShaderProgram>()

        abstract fun create(key: K): ShaderProgram

        operator fun get(key: K): ShaderProgram {
            return map.computeIfAbsent(key) { create(key) }
        }
    }
}