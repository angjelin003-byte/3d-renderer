package com.editor

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class EditorGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: EditorRenderer
    private var previousX = 0f
    private var previousY = 0f

    init {
        setEGLContextClientVersion(2)
        renderer = EditorRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                renderer.angleY += dx * 0.5f
                renderer.angleX += dy * 0.5f
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }
}
