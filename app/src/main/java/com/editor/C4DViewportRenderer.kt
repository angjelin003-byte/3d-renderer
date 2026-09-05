package com.editor

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class C4DViewportRenderer : GLSurfaceView.Renderer {

    val camera = Camera()
    private var shaderProgram = 0
    private var testObject: SceneObject? = null
    
    // Default C4D Grey material
    private val defaultObjectColor = floatArrayOf(0.6f, 0.6f, 0.6f, 1.0f)
    
    // Light positioned slightly above and to the right of the camera
    private val lightPos = floatArrayOf(2.0f, 3.0f, 5.0f)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.25f, 0.25f, 0.25f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        shaderProgram = ShaderUtils.createProgram()

        // TODO: Replace this dummy data with context.assets.open("model.obj")
        // For testing, here is a hardcoded triangle mesh
        val dummyMesh = MeshData(
            vertices = floatArrayOf(0f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f),
            normals = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f)
        )
        testObject = SceneObject(dummyMesh, shaderProgram)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        camera.updateProjectionMatrix(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        // Draw the 3D Object
        testObject?.draw(camera, lightPos, defaultObjectColor)
    }
}
