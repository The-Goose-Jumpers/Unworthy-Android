#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_alpha;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    gl_FragColor = texColor * v_color * vec4(1.0, 1.0, 1.0, u_alpha);
}
