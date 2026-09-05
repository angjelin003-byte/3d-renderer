package com.editor

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class CameraTouchController(
    private val renderer: C4DViewportRenderer, 
    private val glView: GLSurfaceView
) : View.OnTouchListener {
    
    private var previousX = 0f
    private var previousY = 0f
    
    private var touchDownTime = 0L
    private var touchDownX = 0f
    private var touchDownY = 0f

    private var radius = 5f
    private var azimuth = 0f 
    private var elevation = Math.PI.toFloat() / 4f 

    private val rotationSpeed = 0.01f

    init {
        updateCameraPosition()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = x
                previousY = y
                touchDownX = x
                touchDownY = y
                touchDownTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                if (Math.abs(x - touchDownX) > 10f || Math.abs(y - touchDownY) > 10f) {
                    azimuth -= dx * rotationSpeed
                    elevation -= dy * rotationSpeed
                    elevation = elevation.coerceIn(0.01f, Math.PI.toFloat() - 0.01f)

                    updateCameraPosition()
                    glView.requestRender()
                }
                previousX = x
                previousY = y
            }
            MotionEvent.ACTION_UP -> {
                val touchDuration = System.currentTimeMillis() - touchDownTime
                val distanceMoved = Math.max(Math.abs(x - touchDownX), Math.abs(y - touchDownY))

                if (touchDuration < 200 && distanceMoved < 10f) {
                    glView.queueEvent {
                        renderer.handleTap(x, y)
                        glView.requestRender()
                    }
                }
            }
        }
        return true
    }

    private fun updateCameraPosition() {
        renderer.camera.eyeX = renderer.camera.targetX + radius * sin(elevation) * cos(azimuth)
        renderer.camera.eyeY = renderer.camera.targetY + radius * cos(elevation)
        renderer.camera.eyeZ = renderer.camera.targetZ + radius * sin(elevation) * sin(azimuth)
        renderer.camera.updateViewMatrix()
    }
}
