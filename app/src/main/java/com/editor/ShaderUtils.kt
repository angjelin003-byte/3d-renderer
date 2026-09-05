package com.editor

import android.opengl.GLES20
import android.util.Log

object ShaderUtils {
    // 1. VERTEX SHADER: Handles position and normal transformations
    const val VERTEX_SHADER_CODE = """
        uniform mat4 u_MVPMatrix;   // Model-View-Projection matrix (for screen position)
        uniform mat4 u_MVMatrix;    // Model-View matrix (for lighting calculation)

        attribute vec4 a_Position;  // Unrolled vertex positions from ObjParser
        attribute vec3 a_Normal;    // Unrolled normals from ObjParser

        varying vec3 v_Position;    // Passed to fragment shader
        varying vec3 v_Normal;      // Passed to fragment shader

        void main() {
            // Transform vertex into eye space for lighting
            v_Position = vec3(u_MVMatrix * a_Position);
            
            // Transform normal into eye space (must rotate with the object)
            v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
            
            // Calculate final 2D screen position
            gl_Position = u_MVPMatrix * a_Position;
        }
    """

    // 2. FRAGMENT SHADER: Calculates pixel color and lighting
    const val FRAGMENT_SHADER_CODE = """
        precision mediump float;    // Mobile optimization: medium precision is enough for colors

        uniform vec3 u_LightPos;    // Light position in eye space (e.g., attached to camera)
        uniform vec4 u_Color;       // Base object color (e.g., C4D default grey)

        varying vec3 v_Position;
        varying vec3 v_Normal;

        void main() {
            // Normalize vectors for accurate dot product calculation
            vec3 normal = normalize(v_Normal);
            vec3 lightVector = normalize(u_LightPos - v_Position);
            
            // Calculate Diffuse lighting (Lambertian reflection)
            // max() prevents negative light on the dark side of the object
            float diffuse = max(dot(normal, lightVector), 0.0);
            
            // Add a constant Ambient light so the dark side isn't pitch black
            float ambient = 0.2; 
            
            // Combine lighting and apply to base color
            float totalLight = diffuse + ambient;
            gl_FragColor = vec4(u_Color.rgb * totalLight, u_Color.a);
        }
    """

    // 3. COMPILER UTILITY: Turns GLSL strings into a GPU program
    fun createProgram(): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)

        return GLES20.glCreateProgram().also { program ->
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
            
            // Clean up shaders once linked
            GLES20.glDeleteShader(vertexShader)
            GLES20.glDeleteShader(fragmentShader)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)

            // Debugging: Check if compilation failed (crucial for typos in GLSL)
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                Log.e("ShaderUtils", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
            }
        }
    }
}
