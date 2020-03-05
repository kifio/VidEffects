package com.sherazkhilji.videffects;

public final class Constants {

    public static final int FLOAT_SIZE_BYTES = 4;

    public static final String DEFAULT_VERTEX_SHADER = "uniform mat4 uSTMatrix;\n"
            + "attribute vec4 aPosition;\n"
            + "attribute vec4 aTextureCoordinate;\n"
            + "varying vec2 vTextureCoordinate;\n"
            + "void main() {\n"
            + "  gl_Position = aPosition;\n"
            + "  vTextureCoordinate = (uSTMatrix * aTextureCoordinate).xy;\n"
            + "}\n";
}
