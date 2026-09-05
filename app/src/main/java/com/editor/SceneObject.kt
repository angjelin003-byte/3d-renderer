package com.editor

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class SceneObject(val name: String, meshData: MeshData, private val shaderProgram: Int) {
    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val vertexCount: Int = meshData.vertices.size / 3
    
    val modelMatrix = FloatArray(16)
    var isSelected = false
    val boundingRadius = 1.732f 

    private val positionHandle: Int
    private val normalHandle: Int
    private val mvpMatrixHandle: Int
    private val mvMatrixHandle: Int
    private val lightPosHandle: Int
    private val colorHandle: Int

    init {
        Matrix.setIdentityM(modelMatrix, 0)

        vertexBuffer = allocateBuffer(meshData.vertices)
        normalBuffer = allocateBuffer(meshData.normals)

        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Position")
        normalHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Normal")
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix")
        mvMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVMatrix")
        lightPosHandle = GLES20.glGetUniformLocation(shaderProgram, "u_LightPos")
        colorHandle = GLES20.glGetUniformLocation(shaderProgram, "u_Color")
    }

    private fun allocateBuffer(array: FloatArray): FloatBuffer {
        return ByteBuffer.allocateDirect(array.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply { put(array); position(0) }
        }
    }

    fun getWorldPosition(): FloatArray {
        return floatArrayOf(modelMatrix[12], modelMatrix[13], modelMatrix[14])
    }

    fun draw(camera: Camera, lightPosInEyeSpace: FloatArray, defaultColor: FloatArray) {
        GLES20.glUseProgram(shaderProgram)

        val renderColor = if (isSelected) floatArrayOf(1.0f, 0.6f, 0.0f, 1.0f) else defaultColor

        val mvpMatrix = camera.getMVPMatrix(modelMatrix)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        
        val mvMatrix = FloatArray(16)
        Matrix.multiplyMM(mvMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0)

        GLES20.glUniform3fv(lightPosHandle, 1, lightPosInEyeSpace, 0)
        GLES20.glUniform4fv(colorHandle, 1, renderColor, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
    }
}
