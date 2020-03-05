package com.sherazkhilji.videffects.filter;

import com.sherazkhilji.videffects.Constants;
import com.sherazkhilji.videffects.interfaces.Filter;

public class LookupFilter implements Filter {

    private float intensity = 0.0F;

    @Override
    public void setIntensity(float strength) {
        this.intensity = strength;
    }

    @Override
    public String getVertexShader() {
        return  "uniform mat4 uSTMatrix;\n"
                + "attribute vec4 aPosition;\n"
                        + "attribute vec4 aTextureCoordinate;\n"
                        + "attribute vec4 aTextureCoordinate2;\n"
                        + "varying vec2 vTextureCoordinate;\n"
                        + "varying vec2 vTextureCoordinate2;\n"
                        + " \n"
                        + "void main()\n"
                        + "{\n"
                        + "    gl_Position = aPosition;\n"
                        + "    vTextureCoordinate = (uSTMatrix * aTextureCoordinate).xy;\n"
                        + "    vTextureCoordinate2 = (uSTMatrix * aTextureCoordinate2).xy;\n"
                        + "}";
    }

    @Override
    public String getFragmentShader() {
        return "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "varying vec2 vTextureCoordinate;\n"
                + "varying vec2 vTextureCoordinate2;\n"
                + "uniform samplerExternalOES sTexture;\n"
                + "uniform sampler2D sTexture2;\n"
                + "void main() {\n"
                + "  vec4 overlay = texture2D(sTexture2, vTextureCoordinate2);\n"
                + "  vec4 base = texture2D(sTexture, vTextureCoordinate);\n"
                + "  gl_FragColor = base;\n"
                + "}\n";
    }
}