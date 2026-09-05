package com.editor

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EditorRenderer : GLSurfaceView.Renderer {
    private lateinit var camera: Camera
    private lateinit var gridPlane: GridPlane
    private lateinit var cubeModel: Model

    private val lightPos = floatArrayOf(2f, 5f, 3f)
    var angleX = 0f
    var angleY = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.15f, 0.15f, 0.15f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        camera = Camera()
        gridPlane = GridPlane()
        
        cubeModel = Model(PrimitiveBuilder.createCube())
        cubeModel.initShader()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        camera.updateProjectionMatrix(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Apply rotation to the cube
        Matrix.setIdentityM(cubeModel.modelMatrix, 0)
        Matrix.translateM(cubeModel.modelMatrix, 0, 0f, 1f, 0f)
        Matrix.rotateM(cubeModel.modelMatrix, 0, angleX, 1f, 0f, 0f)
        Matrix.rotateM(cubeModel.modelMatrix, 0, angleY, 0f, 1f, 0f)

        gridPlane.draw(camera)
        cubeModel.draw(camera, lightPos)
    }
}
