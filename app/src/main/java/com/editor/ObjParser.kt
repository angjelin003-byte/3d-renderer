package com.editor

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

data class MeshData(
    val vertices: FloatArray,
    val normals: FloatArray   
)

class ObjParser {
    companion object {
        fun parse(inputStream: InputStream): MeshData {
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            val tempVertices = mutableListOf<Float>()
            val tempNormals = mutableListOf<Float>()
            
            val finalVertices = mutableListOf<Float>()
            val finalNormals = mutableListOf<Float>()

            reader.forEachLine { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEachLine

                when {
                    trimmed.startsWith("v ") -> {
                        val parts = fastSplit(trimmed, ' ', 3)
                        tempVertices.add(parts[1].toFloat())
                        tempVertices.add(parts[2].toFloat())
                        tempVertices.add(parts[3].toFloat())
                    }
                    trimmed.startsWith("vn ") -> {
                        val parts = fastSplit(trimmed, ' ', 3)
                        tempNormals.add(parts[1].toFloat())
                        tempNormals.add(parts[2].toFloat())
                        tempNormals.add(parts[3].toFloat())
                    }
                    trimmed.startsWith("f ") -> {
                        val faceIndices = fastSplit(trimmed, ' ', 3)
                        for (i in 1..3) {
                            val vertexData = fastSplit(faceIndices[i], '/', 2)
                            
                            val vIndex = (vertexData[0].toInt() - 1) * 3
                            val nIndex = if (vertexData.size > 2 && vertexData[2].isNotEmpty()) {
                                (vertexData[2].toInt() - 1) * 3
                            } else -1

                            finalVertices.add(tempVertices[vIndex])
                            finalVertices.add(tempVertices[vIndex + 1])
                            finalVertices.add(tempVertices[vIndex + 2])

                            if (nIndex >= 0) {
                                finalNormals.add(tempNormals[nIndex])
                                finalNormals.add(tempNormals[nIndex + 1])
                                finalNormals.add(tempNormals[nIndex + 2])
                            } else {
                                finalNormals.addAll(listOf(0f, 1f, 0f))
                            }
                        }
                    }
                }
            }
            
            return MeshData(finalVertices.toFloatArray(), finalNormals.toFloatArray())
        }

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
