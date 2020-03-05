package com.sherazkhilji.videffects.model;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;


public abstract class BaseRenderer {

    private static final String TAG = "BaseRenderer";
    private final static int POSITION_COUNT = 3;
    private static final int TEXTURE_COUNT = 2;
    private static final int STRIDE = (POSITION_COUNT + TEXTURE_COUNT) * 4;

    private int[] linkStatus = new int[1];

    private FloatBuffer videoVerticesData = Utils.getVertexBuffer();
    private int[] videoTextureHandle = new int[1];

    public void setBitmap(Bitmap bitmap) {
//        GLES20.glGenBuffers(1, presetBufferHandles, 0);
//
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, presetBufferHandles[0]);
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, Utils.VERTICES.length * FLOAT_SIZE_BYTES, presetTextureBuffer, GLES20.GL_DYNAMIC_DRAW);
//
//        GLES20.glGenTextures(1, textureHandles, 1);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandles[1]);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//        bitmap.recycle();
    }

    protected BaseRenderer() {

    }

    protected abstract int getVertexShader();

    protected abstract int getFragmentShader();

    protected abstract boolean isTwoInputFilterSelected();

    protected int getTexture() {
        return videoTextureHandle[0];
    }

    private float[] texMatrix = new float[16];

    protected float[] getTransformMatrix() {
        return texMatrix;
    }

    private int createProgram() {
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, getVertexShader());
            GLES20.glAttachShader(program, getFragmentShader());
            GLES20.glLinkProgram(program);
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    protected void init() {
        // Choose unit
        glActiveTexture(GL_TEXTURE0);

        // Generate texture
        GLES20.glGenTextures(1, videoTextureHandle, 0);

        // Put texture to target of unit
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, videoTextureHandle[0]); // помещаем текстуру в таргет этого юнита

        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    protected void draw() {
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        int program = createProgram();

        int aPositionLocation = glGetAttribLocation(program, "aPosition");
        int aTextureLocation = glGetAttribLocation(program, "aTextureCoordinate");
        int uTextureUnitLocation = glGetUniformLocation(program, "sTexture");
        int uMatrixLocation = glGetUniformLocation(program, "uSTMatrix");

        GLES20.glUseProgram(program);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, texMatrix, 0);

        videoVerticesData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT, false, STRIDE, videoVerticesData);
        glEnableVertexAttribArray(aPositionLocation);

        // координаты текстур
        videoVerticesData.position(POSITION_COUNT);
        glVertexAttribPointer(aTextureLocation, TEXTURE_COUNT, GL_FLOAT, false, STRIDE, videoVerticesData);
        glEnableVertexAttribArray(aTextureLocation);

        glActiveTexture(GL_TEXTURE0); // выбираем юнит
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, videoTextureHandle[0]); // помещаем текстуру в таргет этого юнита

        // юнит текстуры
        glUniform1i(uTextureUnitLocation, 0);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }
}