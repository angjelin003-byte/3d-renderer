package com.editor

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Model(meshData: MeshData) {
    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val vertexCount: Int = meshData.vertices.size / 3
    private var shaderProgram = 0
    val modelMatrix = FloatArray(16)

    init {
        Matrix.setIdentityM(modelMatrix, 0)

        vertexBuffer = ByteBuffer.allocateDirect(meshData.vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(meshData.vertices); position(0) }
        }

        normalBuffer = ByteBuffer.allocateDirect(meshData.normals.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(meshData.normals); position(0) }
        }
    }

    fun initShader() {
        shaderProgram = ShaderUtils.createProgram()
    }

    fun draw(camera: Camera, lightPos: FloatArray) {
        GLES20.glUseProgram(shaderProgram)

        val mvpMatrix = camera.getMVPMatrix(modelMatrix)
        val mvMatrix = FloatArray(16)
        Matrix.multiplyMM(mvMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0)

        val mvpHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix")
        val mvHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVMatrix")
        val lightPosHandle = GLES20.glGetUniformLocation(shaderProgram, "u_LightPos")
        val colorHandle = GLES20.glGetUniformLocation(shaderProgram, "u_Color")
        
        val posHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Position")
        val normalHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Normal")

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(mvHandle, 1, false, mvMatrix, 0)
        GLES20.glUniform3fv(lightPosHandle, 1, lightPos, 0)
        GLES20.glUniform4f(colorHandle, 0.2f, 0.6f, 1.0f, 1.0f) // Blue color

        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(posHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
    }
}
