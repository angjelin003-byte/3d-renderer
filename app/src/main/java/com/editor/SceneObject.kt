// ... existing imports ...
class SceneObject(val name: String, meshData: MeshData, private val shaderProgram: Int) {
    // ... existing variables ...

    var isSelected = false
    
    // A sphere covering a 1x1x1 cube from center (0,0,0) has a radius of roughly 1.732 (sqrt(3))
    val boundingRadius = 1.732f 
    
    // Extracted from the model matrix (indices 12, 13, 14 hold the XYZ translation)
    fun getWorldPosition(): FloatArray {
        return floatArrayOf(modelMatrix[12], modelMatrix[13], modelMatrix[14])
    }

    fun draw(camera: Camera, lightPosInEyeSpace: FloatArray, defaultColor: FloatArray) {
        GLES20.glUseProgram(shaderProgram)

        // Highlight orange like Cinema 4D if selected
        val renderColor = if (isSelected) floatArrayOf(1.0f, 0.6f, 0.0f, 1.0f) else defaultColor
        
        // ... rest of the existing draw calls (pass renderColor instead of defaultColor) ...
    }
}
