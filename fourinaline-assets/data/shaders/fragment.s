#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords0;
varying vec2 v_texCoords1;

uniform sampler2D u_texture; 
uniform sampler2D u_wood; 

void main(void) {
  vec4 texColor0 = texture2D(u_texture, v_texCoords0);
  vec4 texColor1 = texture2D(u_wood, v_texCoords0);

  vec4 pixel = vec4(1.0);
  pixel.r = texColor0.r;
  pixel.b = texColor0.b;
  pixel.g = texColor0.g;
  pixel.a = texColor1.a;

  gl_FragColor = pixel;
}
