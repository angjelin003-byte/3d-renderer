package com.editor

import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class CameraTouchController(private val camera: Camera) : View.OnTouchListener {
    private var previousX = 0f
    private var previousY = 0f

    // Spherical coordinates for orbit
    private var radius = 5f
    private var azimuth = 0f // Yaw
    private var elevation = Math.PI.toFloat() / 4f // Pitch (45 degrees up)

    private val rotationSpeed = 0.01f
    private val panSpeed = 0.01f
    private val zoomSpeed = 0.05f

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

                // Right click or 2-finger touch usually pans/zooms, but for mobile 
                // we'll use a modifier toggle or split screen halves. 
                // Defaulting to ORBIT for standard 1-finger drag:
                
                azimuth -= dx * rotationSpeed
                elevation -= dy * rotationSpeed

                // Clamp elevation to avoid flipping upside down
                elevation = elevation.coerceIn(0.01f, Math.PI.toFloat() - 0.01f)

                updateCameraPosition()
                v.requestRender() // Tell OpenGL to redraw
            }
        }
        previousX = x
        previousY = y
        return true
    }

    private fun updateCameraPosition() {
        // Convert Spherical to Cartesian
        camera.eyeX = camera.targetX + radius * sin(elevation) * cos(azimuth)
        camera.eyeY = camera.targetY + radius * cos(elevation)
        camera.eyeZ = camera.targetZ + radius * sin(elevation) * sin(azimuth)
        camera.updateViewMatrix()
    }

    fun zoom(amount: Float) {
        radius += amount * zoomSpeed
        radius = radius.coerceAtLeast(0.5f) // Don't zoom past the target
        updateCameraPosition()
    }
}
