// ... inside C4DViewportRenderer class ...

var viewportWidth = 0f
var viewportHeight = 0f

override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    GLES20.glViewport(0, 0, width, height)
    viewportWidth = width.toFloat()
    viewportHeight = height.toFloat()
    camera.updateProjectionMatrix(width, height)
}

fun handleTap(x: Float, y: Float) {
    // 1. Generate the Ray
    val rayDir = Raycaster.getRayDirection(
        x, y, viewportWidth, viewportHeight, 
        camera.viewMatrix, camera.projectionMatrix
    )
    val rayOrigin = floatArrayOf(camera.eyeX, camera.eyeY, camera.eyeZ)

    // 2. Test against all objects
    var closestObject: SceneObject? = null
    var minDistance = Float.MAX_VALUE

    for (obj in sceneObjects) {
        obj.isSelected = false // Deselect everything first
        
        val hitDistance = Raycaster.rayIntersectsSphere(
            rayOrigin, rayDir, 
            obj.getWorldPosition(), obj.boundingRadius
        )

        if (hitDistance != null && hitDistance < minDistance) {
            minDistance = hitDistance
            closestObject = obj
        }
    }

    // 3. Mark the closest hit as selected
    closestObject?.isSelected = true
}
