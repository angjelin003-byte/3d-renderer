package com.editor

import android.opengl.GLES20
import android.util.Log

object ShaderUtils {
    
    const val VERTEX_SHADER_CODE = """
        uniform mat4 u_MVPMatrix;
        uniform mat4 u_MVMatrix;

        attribute vec4 a_Position;
        attribute vec3 a_Normal;

        varying vec3 v_Position;
        varying vec3 v_Normal;

        void main() {
            v_Position = vec3(u_MVMatrix * a_Position);
            v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
            gl_Position = u_MVPMatrix * a_Position;
        }
    """

    const val FRAGMENT_SHADER_CODE = """
        precision mediump float;

        uniform vec3 u_LightPos;
        uniform vec4 u_Color;

        varying vec3 v_Position;
        varying vec3 v_Normal;

        void main() {
            vec3 normal = normalize(v_Normal);
            vec3 lightVector = normalize(u_LightPos - v_Position);
            
            float diffuse = max(dot(normal, lightVector), 0.0);
            float ambient = 0.2; 
            
            float totalLight = diffuse + ambient;
            gl_FragColor = vec4(u_Color.rgb * totalLight, u_Color.a);
        }
    """

    fun createProgram(): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)

        return GLES20.glCreateProgram().also { program ->
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
            
            GLES20.glDeleteShader(vertexShader)
            GLES20.glDeleteShader(fragmentShader)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)

            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                Log.e("ShaderUtils", GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
            }
        }
    }
}
