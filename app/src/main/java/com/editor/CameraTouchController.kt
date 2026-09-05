package com.editor

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class CameraTouchController(private val camera: Camera) : View.OnTouchListener {
    private var previousX = 0f
    private var previousY = 0f

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
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                azimuth -= dx * rotationSpeed
                elevation -= dy * rotationSpeed
                elevation = elevation.coerceIn(0.01f, Math.PI.toFloat() - 0.01f)

                updateCameraPosition()
                
                // Instruct the system to draw the updated camera angle
                if (v is GLSurfaceView) {
                    v.requestRender()
                }
            }
        }
        previousX = x
        previousY = y
        return true
    }

    private fun updateCameraPosition() {
        camera.eyeX = camera.targetX + radius * sin(elevation) * cos(azimuth)
        camera.eyeY = camera.targetY + radius * cos(elevation)
        camera.eyeZ = camera.targetZ + radius * sin(elevation) * sin(azimuth)
        camera.updateViewMatrix()
    }
}
