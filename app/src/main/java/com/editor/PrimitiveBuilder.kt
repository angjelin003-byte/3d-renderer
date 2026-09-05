package com.editor

object PrimitiveBuilder {
    
    fun createCube(): MeshData {
        val v = floatArrayOf(
            -1f, -1f,  1f,   1f, -1f,  1f,   1f,  1f,  1f,
            -1f, -1f,  1f,   1f,  1f,  1f,  -1f,  1f,  1f,
             1f, -1f,  1f,   1f, -1f, -1f,   1f,  1f, -1f,
             1f, -1f,  1f,   1f,  1f, -1f,   1f,  1f,  1f,
             1f, -1f, -1f,  -1f, -1f, -1f,  -1f,  1f, -1f,
             1f, -1f, -1f,  -1f,  1f, -1f,   1f,  1f, -1f,
            -1f, -1f, -1f,  -1f, -1f,  1f,  -1f,  1f,  1f,
            -1f, -1f, -1f,  -1f,  1f,  1f,  -1f,  1f, -1f,
            -1f,  1f,  1f,   1f,  1f,  1f,   1f,  1f, -1f,
            -1f,  1f,  1f,   1f,  1f, -1f,  -1f,  1f, -1f,
            -1f, -1f, -1f,   1f, -1f, -1f,   1f, -1f,  1f,
            -1f, -1f, -1f,   1f, -1f,  1f,  -1f, -1f,  1f
        )

        val n = floatArrayOf(
             0f, 0f, 1f,   0f, 0f, 1f,   0f, 0f, 1f,
             0f, 0f, 1f,   0f, 0f, 1f,   0f, 0f, 1f,
             1f, 0f, 0f,   1f, 0f, 0f,   1f, 0f, 0f,
             1f, 0f, 0f,   1f, 0f, 0f,   1f, 0f, 0f,
             0f, 0f, -1f,  0f, 0f, -1f,  0f, 0f, -1f,
             0f, 0f, -1f,  0f, 0f, -1f,  0f, 0f, -1f,
            -1f, 0f, 0f,  -1f, 0f, 0f,  -1f, 0f, 0f,
            -1f, 0f, 0f,  -1f, 0f, 0f,  -1f, 0f, 0f,
             0f, 1f, 0f,   0f, 1f, 0f,   0f, 1f, 0f,
             0f, 1f, 0f,   0f, 1f, 0f,   0f, 1f, 0f,
             0f, -1f, 0f,  0f, -1f, 0f,  0f, -1f, 0f,
             0f, -1f, 0f,  0f, -1f, 0f,  0f, -1f, 0f
        )
        return MeshData(v, n)
    }
}
