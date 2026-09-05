package com.editor

import android.opengl.Matrix
import kotlin.math.sqrt

data class Ray(val origin: FloatArray, val direction: FloatArray)

object Raycaster {
    
    fun calculateRay(
To pick an object in 3D space, you have to reverse the OpenGL rendering pipeline. You take the 2D screen coordinate where you tapped, normalize it, and multiply it by the **Inverse Projection Matrix** and **Inverse View Matrix** to shoot a mathematical 3D line (a Ray) out of the camera into the virtual world. 

<Image src="image_agent_tag_7325756434660550540" alt="Diagram showing the 3D transformation pipeline from Local Space to Viewport Space" caption="Reversing the pipeline: Viewport to World Space" />

Here are the specific scripts to implement Raycasting, object bounding boundaries, and tap detection in your Kotlin architecture.

### 1. The Raycasting Engine
This script handles the "un-projection" math to generate the ray and tests if it intersects with an object's bounding sphere.

**`app/src/main/java/com/editor/Raycaster.kt`**
```kotlin
package com.editor

import android.opengl.Matrix
import kotlin.math.sqrt

object Raycaster {
    
    // Calculates the 3D ray direction from a 2D screen tap
    fun getRayDirection(
        touchX: Float, touchY: Float, 
        screenWidth: Float, screenHeight: Float, 
        viewMatrix: FloatArray, projMatrix: FloatArray
    ): FloatArray {
        // 1. Convert screen coordinates to Normalized Device Coordinates (NDC) [-1 to 1]
        val ndcX = (2.0f * touchX) / screenWidth - 1.0f
        val ndcY = 1.0f - (2.0f * touchY) / screenHeight // Invert Y (OpenGL Y goes up)

        // 2. Clip Space coordinates (Z = -1 means pointing forward into the screen)
        val clipCoords = floatArrayOf(ndcX, ndcY, -1f, 1f)

        // 3. Eye Space coordinates (Undo the projection)
        val invProj = FloatArray(16)
        Matrix.invertM(invProj, 0, projMatrix, 0)
        
        val eyeCoords = FloatArray(4)
        Matrix.multiplyMV(eyeCoords, 0, invProj, 0, clipCoords, 0)
        eyeCoords[2] = -1f // Force it to point forward
        eyeCoords[3] = 0f  // 0 means it's a direction vector, not a point

        // 4. World Space coordinates (Undo the camera view)
        val invView = FloatArray(16)
        Matrix.invertM(invView, 0, viewMatrix, 0)
        
        val rayWorld = FloatArray(4)
        Matrix.multiplyMV(rayWorld, 0, invView, 0, eyeCoords, 0)

        // 5. Normalize the final direction vector
        val length = sqrt(rayWorld[0] * rayWorld[0] + rayWorld[1] * rayWorld[1] + rayWorld[2] * rayWorld[2])
        return floatArrayOf(rayWorld[0] / length, rayWorld[1] / length, rayWorld[2] / length)
    }

    // Mathematical Line-Sphere Intersection
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

        if (discriminant < 0) return null // Ray missed the sphere completely

        // Calculate distance to the closest hit point
        val distance = (-b - sqrt(discriminant)) / (2.0f * a)
        return if (distance > 0) distance else null
    }
      }
      
