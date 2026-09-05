package com.editor

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

data class MeshData(
    val vertices: FloatArray, // Unrolled, aligned vertices (x,y,z)
    val normals: FloatArray   // Unrolled, aligned normals (nx,ny,nz)
)

class ObjParser {
    companion object {
        fun parse(inputStream: InputStream): MeshData {
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            // Temporary lists for raw data
            val tempVertices = mutableListOf<Float>()
            val tempNormals = mutableListOf<Float>()
            
            // Final unrolled data for OpenGL
            val finalVertices = mutableListOf<Float>()
            val finalNormals = mutableListOf<Float>()

            reader.forEachLine { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEachLine

                when {
                    trimmed.startsWith("v ") -> {
                        val parts = fastSplit(trimmed, ' ', 3)
                        tempVertices.add(parts[1].toFloat()) // x
                        tempVertices.add(parts[2].toFloat()) // y
                        tempVertices.add(parts[3].toFloat()) // z
                    }
                    trimmed.startsWith("vn ") -> {
                        val parts = fastSplit(trimmed, ' ', 3)
                        tempNormals.add(parts[1].toFloat())
                        tempNormals.add(parts[2].toFloat())
                        tempNormals.add(parts[3].toFloat())
                    }
                    trimmed.startsWith("f ") -> {
                        // Assuming triangulated faces (3 vertices per face)
                        val faceIndices = fastSplit(trimmed, ' ', 3)
                        for (i in 1..3) {
                            // Extract the vertex and normal indices (Format: v/vt/vn)
                            val vertexData = fastSplit(faceIndices[i], '/', 2)
                            
                            // .obj indices are 1-based, Kotlin arrays are 0-based
                            val vIndex = (vertexData[0].toInt() - 1) * 3
                            val nIndex = if (vertexData.size > 2 && vertexData[2].isNotEmpty()) {
                                (vertexData[2].toInt() - 1) * 3
                            } else -1

                            // Add to final flat arrays
                            finalVertices.add(tempVertices[vIndex])
                            finalVertices.add(tempVertices[vIndex + 1])
                            finalVertices.add(tempVertices[vIndex + 2])

                            if (nIndex >= 0) {
                                finalNormals.add(tempNormals[nIndex])
                                finalNormals.add(tempNormals[nIndex + 1])
                                finalNormals.add(tempNormals[nIndex + 2])
                            } else {
                                finalNormals.addAll(listOf(0f, 1f, 0f)) // Fallback normal
                            }
                        }
                    }
                }
            }
            
            return MeshData(finalVertices.toFloatArray(), finalNormals.toFloatArray())
        }

        // Custom fast split without Regex or massive allocations
        private fun fastSplit(str: String, delimiter: Char, maxParts: Int): List<String> {
            val result = mutableListOf<String>()
            var start = 0
            var i = 0
            while (i < str.length && result.size < maxParts) {
                if (str[i] == delimiter) {
                    if (i > start) result.add(str.substring(start, i))
                    start = i + 1
                }
                i++
            }
            if (start < str.length) result.add(str.substring(start))
            return result
        }
    }
}
