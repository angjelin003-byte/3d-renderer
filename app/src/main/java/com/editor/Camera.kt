package com.editor

import android.opengl.Matrix

class Camera {
    val viewMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    val viewProjectionMatrix = FloatArray(16)

    var eyeX = 0f
    var eyeY = 2f
    var eyeZ = 5f
    
    var targetX = 0f
    var targetY = 0f
    var targetZ = 0f

    init {
        updateViewMatrix()
    }

    fun updateViewMatrix() {
        Matrix.setLookAtM(
            viewMatrix, 0,
            eyeX, eyeY, eyeZ,
            targetX, targetY, targetZ,
            0f, 1f, 0f
        )
    }

    fun updateProjectionMatrix(width: Int, height: Int) {
        val ratio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
    }

    fun getMVPMatrix(modelMatrix: FloatArray): FloatArray {
        val mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
        return mvpMatrix
    }
}
