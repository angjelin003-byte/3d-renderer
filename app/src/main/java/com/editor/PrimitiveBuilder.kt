package com.editor

object PrimitiveBuilder {
    
    fun createCube(): MeshData {
        // A cube has 6 faces, 2 triangles per face = 36 vertices
        val v = floatArrayOf(
            // Front face
            -1f, -1f,  1f,   1f, -1f,  1f,   1f,  1f,  1f,
            -1f, -1f,  1f,   1f,  1f,  1f,  -1f,  1f,  1f,
            // Right face
             1f, -1f,  1f,   1f, -1f, -1f,   1f,  1f, -1f,
             1f, -1f,  1f,   1f,  1f, -1f,   1f,  1f,  1f,
            // Back face
             1f, -1f, -1f,  -1f, -1f, -1f,  -1f,  1f, -1f,
             1f, -1f, -1f,  -1f,  1f, -1f,   1f,  1f, -1f,
            // Left face
            -1f, -1f, -1f,  -1f, -1f,  1f,  -1f,  1f,  1f,
            -1f, -1f, -1f,  -1f,  1f,  1f,  -1f,  1f, -1f,
            // Top face
            -1f,  1f,  1f,   1f,  1f,  1f,   1f,  1f, -1f,
            -1f,  1f,  1f,   1f,  1f, -1f,  -1f,  1f, -1f,
            // Bottom face
            -1f, -1f, -1f,   1f, -1f, -1f,   1f, -1f,  1f,
            -1f, -1f, -1f,   1f, -1f,  1f,  -1f, -1f,  1f
        )

        val n = floatArrayOf(
            // Front face normals (Z+)
             0f, 0f, 1f,   0f, 0f, 1f,   0f, 0f, 1f,
             0f, 0f, 1f,   0f, 0f, 1f,   0f, 0f, 1f,
            // Right face normals (X+)
             1f, 0f, 0f,   1f, 0f, 0f,   1f, 0f, 0f,
             1f, 0f, 0f,   1f, 0f, 0f,   1f, 0f, 0f,
            // Back face normals (Z-)
             0f, 0f, -1f,  0f, 0f, -1f,  0f, 0f, -1f,
             0f, 0f, -1f,  0f, 0f, -1f,  0f, 0f, -1f,
            // Left face normals (X-)
            -1f, 0f, 0f,  -1f, 0f, 0f,  -1f, 0f, 0f,
            -1f, 0f, 0f,  -1f, 0f, 0f,  -1f, 0f, 0f,
            // Top face normals (Y+)
             0f, 1f, 0f,   0f, 1f, 0f,   0f, 1f, 0f,
             0f, 1f, 0f,   0f, 1f, 0f,   0f, 1f, 0f,
            // Bottom face normals (Y-)
             0f, -1f, 0f,  0f, -1f, 0f,  0f, -1f, 0f,
             0f, -1f, 0f,  0f, -1f, 0f,  0f, -1f, 0f
        )
        return MeshData(v, n)
    }
}
