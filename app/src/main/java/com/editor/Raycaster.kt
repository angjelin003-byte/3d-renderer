package com.editor

import android.opengl.Matrix
import kotlin.math.sqrt

object Raycaster {
    
    fun getRayDirection(
        touchX: Float, touchY: Float, 
        screenWidth: Float, screenHeight: Float, 
        viewMatrix: FloatArray, projMatrix: FloatArray
    ): FloatArray {
        val ndcX = (2.0f * touchX) / screenWidth - 1.0f
        val ndcY = 1.0f - (2.0f * touchY) / screenHeight 

        val clipCoords = floatArrayOf(ndcX, ndcY, -1f, 1f)

        val invProj = FloatArray(16)
        Matrix.invertM(invProj, 0, projMatrix, 0)
        
        val eyeCoords = FloatArray(4)
        Matrix.multiplyMV(eyeCoords, 0, invProj, 0, clipCoords, 0)
        eyeCoords[2] = -1f 
        eyeCoords[3] = 0f  

        val invView = FloatArray(16)
        Matrix.invertM(invView, 0, viewMatrix, 0)
        
        val rayWorld = FloatArray(4)
        Matrix.multiplyMV(rayWorld, 0, invView, 0, eyeCoords, 0)

        val length = sqrt(rayWorld[0] * rayWorld[0] + rayWorld[1] * rayWorld[1] + rayWorld[2] * rayWorld[2])
        return floatArrayOf(rayWorld[0] / length, rayWorld[1] / length, rayWorld[2] / length)
    }

    fun rayIntersectsSphere(
        rayOrigin: FloatArray, rayDir: FloatArray, 
        sphereCenter: FloatArray, sphereRadius: Float
    ): Float? {
        val oc = floatArrayOf(
            rayOrigin[0] - sphereCenter[0],
            rayOrigin[1] - sphereCenter[1],
            rayOrigin[2] - sphereCenter[2]
        )

        val a = rayDir[0]*rayDir[0] + rayDir[1]*rayDir[1] + rayDir[2]*rayDir[2]
        val b = 2.0f * (oc[0]*rayDir[0] + oc[1]*rayDir[1] + oc[2]*rayDir[2])
        val c = (oc[0]*oc[0] + oc[1]*oc[1] + oc[2]*oc[2]) - (sphereRadius * sphereRadius)

        val discriminant = b * b - 4 * a * c

        if (discriminant < 0) return null 

        val distance = (-b - sqrt(discriminant)) / (2.0f * a)
        return if (distance > 0) distance else null
    }
}
