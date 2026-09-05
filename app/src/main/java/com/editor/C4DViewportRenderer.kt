package com.editor

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun createFloatBuffer(array: FloatArray): FloatBuffer {
    // 4 bytes per float
    val buffer = ByteBuffer.allocateDirect(array.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer()
    }
    buffer.put(array)
    buffer.position(0)
    return buffer
}

class C4DViewportRenderer : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // C4D Default Dark Grey Background
        GLES20.glClearColor(0.25f, 0.25f, 0.25f, 1.0f)
        
        // Enable Depth Testing for 3D Objects
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        // Enable Blending for half-transparent wireframe grid
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        // Setup Perspective Matrix (Projection) here
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        // 1. Draw Viewport Grid (Half-transparent lines)
        // 2. Apply Camera View Matrix (Pan, Zoom, Orbit)
        // 3. Draw Scene Objects (Cube, Sphere, .obj files)
    }
}
