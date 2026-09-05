package com.editor

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.util.concurrent.CopyOnWriteArrayList
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class C4DViewportRenderer : GLSurfaceView.Renderer {

    val camera = Camera()
    private var shaderProgram = 0
    
    private val sceneObjects = CopyOnWriteArrayList<SceneObject>()
    private var gridPlane: GridPlane? = null
    
    private val defaultObjectColor = floatArrayOf(0.6f, 0.6f, 0.6f, 1.0f)
    private val lightPos = floatArrayOf(2.0f, 3.0f, 5.0f)
    
    var viewportWidth = 0f
    var viewportHeight = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.25f, 0.25f, 0.25f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        shaderProgram = ShaderUtils.createProgram()
        gridPlane = GridPlane()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        viewportWidth = width.toFloat()
        viewportHeight = height.toFloat()
        camera.updateProjectionMatrix(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        gridPlane?.draw(camera)

        for (obj in sceneObjects) {
            obj.draw(camera, lightPos, defaultObjectColor)
        }
    }

    fun addCube(name: String) {
        if (shaderProgram == 0) return 
        val mesh = PrimitiveBuilder.createCube()
        val newObject = SceneObject(name, mesh, shaderProgram)
        sceneObjects.add(newObject)
    }

    fun handleTap(x: Float, y: Float) {
        val rayDir = Raycaster.getRayDirection(
            x, y, viewportWidth, viewportHeight, 
            camera.viewMatrix, camera.projectionMatrix
        )
        val rayOrigin = floatArrayOf(camera.eyeX, camera.eyeY, camera.eyeZ)

        var closestObject: SceneObject? = null
        var minDistance = Float.MAX_VALUE

        for (obj in sceneObjects) {
            obj.isSelected = false 
            
            val hitDistance = Raycaster.rayIntersectsSphere(
                rayOrigin, rayDir, 
                obj.getWorldPosition(), obj.boundingRadius
            )

            if (hitDistance != null && hitDistance < minDistance) {
                minDistance = hitDistance
                closestObject = obj
            }
        }

        closestObject?.isSelected = true
    }
}
