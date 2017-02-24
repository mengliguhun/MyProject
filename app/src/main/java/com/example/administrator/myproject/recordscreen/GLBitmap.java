package com.example.administrator.myproject.recordscreen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.example.administrator.myproject.recordscreen.gles.GlUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Administrator on 2017/2/20.
 */

public class GLBitmap {
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_texCoord = a_texCoord;" +
            "}";
    private static final String FRAGMENT_SHADER = "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D s_texture;" +
            "void main() {" +
            "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
            "}";
    private static final float[] VERTEX = {   // in counterclockwise order:
            1, 1, 0,   // top right
            -1, 1, 0,  // top left
            -1, -1, 0, // bottom left
            1, -1, 0,  // bottom right
    };
    private static final short[] VERTEX_INDEX = { 0, 1, 2, 2, 0, 3 };
    private static final float[] UV_TEX_VERTEX = {   // in clockwise order:
            1, 0,  // bottom right
            0, 0,  // bottom left
            0, 1,  // top left
            1, 1,  // top right
    };

    private final FloatBuffer mVertexBuffer;
    private final ShortBuffer mVertexIndexBuffer;
    private final FloatBuffer mUvTexVertexBuffer;

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mCameraMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private int mProgram;
    private int mPositionHandle;
    private int mMatrixHandle;
    private int mTexCoordHandle;
    private int mTexSamplerHandle;

    private final Resources mResources;

    private int[] mTexNames;

    public GLBitmap(Context context) {
        mResources = context.getResources();
//        mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(VERTEX);
//        mVertexBuffer.position(0);
        mVertexBuffer = GlUtil.createFloatBuffer(VERTEX);


        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);

//        mVertexIndexBuffer = GlUtil.createFloatBuffer(VERTEX_INDEX);

//        mUvTexVertexBuffer = ByteBuffer.allocateDirect(UV_TEX_VERTEX.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(UV_TEX_VERTEX);
//        mUvTexVertexBuffer.position(0);
        mUvTexVertexBuffer = GlUtil.createFloatBuffer(UV_TEX_VERTEX);
    }


    public void onDrawFrame(Bitmap bitmap) {
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0,
                mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
                mUvTexVertexBuffer);

        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1i(mTexSamplerHandle, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);

    }

    static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public int[] createTexture(int[] mTexNames ,Bitmap bitmap,int width, int height) {

        mProgram = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");

        this.mTexNames = mTexNames;
//        GLES20.glGenTextures(1, mTexNames, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexNames[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        float ratio = 1;
//        if (height>width){
//            ratio = (float) height / width;
//        }
//        else {
//            ratio = (float) width/ height;
//        }

        Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -ratio, ratio, 3, 7);
        Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mCameraMatrix, 0);

        return mTexNames;
    }
}
