package com.editor

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GridPlane {
    private val vertexBuffer: FloatBuffer
    private var shaderProgram = 0
    private var vertexCount = 0
    val modelMatrix = FloatArray(16)

    // Flat shader just for grid lines
    private val vertexShaderCode = """
        uniform mat4 u_MVPMatrix;
        attribute vec4 a_Position;
        void main() {
            gl_Position = u_MVPMatrix * a_Position;
        }
    """
    
    private val fragmentShaderCode = """
        precision mediump float;
        void main() {
            // Half-transparent dark grey line
            gl_FragColor = vec4(0.4, 0.4, 0.4, 0.5); 
        }
    """

    init {
        Matrix.setIdentityM(modelMatrix, 0)
        
        // Generate a 20x20 grid (-10 to 10)
        val vertices = mutableListOf<Float>()
        val size = 10
        for (i in -size..size) {
            // Lines parallel to Z axis
            vertices.addAll(listOf(i.toFloat(), 0f, -size.toFloat()))
            vertices.addAll(listOf(i.toFloat(), 0f, size.toFloat()))
            // Lines parallel to X axis
            vertices.addAll(listOf(-size.toFloat(), 0f, i.toFloat()))
            vertices.addAll(listOf(size.toFloat(), 0f, i.toFloat()))
        }
        
        vertexCount = vertices.size / 3
        
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(vertices.toFloatArray()); position(0) }
        }

        val vs = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER).also {
            GLES20.glShaderSource(it, vertexShaderCode)
            GLES20.glCompileShader(it)
        }
        val fs = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER).also {
            GLES20.glShaderSource(it, fragmentShaderCode)
            GLES20.glCompileShader(it)
        }

        shaderProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vs)
            GLES20.glAttachShader(it, fs)
            GLES20.glLinkProgram(it)
        }
    }

    fun draw(camera: Camera) {
        GLES20.glUseProgram(shaderProgram)
        
        // Enable blending for the transparency
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        val mvpMatrix = camera.getMVPMatrix(modelMatrix)
        val mvpHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix")
        val posHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Position")

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
        
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // Draw as lines, not triangles
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(posHandle)
        GLES20.glDisable(GLES20.GL_BLEND)
    }
}
