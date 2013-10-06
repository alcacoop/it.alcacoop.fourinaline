#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords0;

uniform sampler2D u_texture; 
uniform sampler2D u_wood; 

void main(void) {
  vec4 pixel_wood = texture2D(u_wood, v_texCoords0);
  vec4 pixel_mask = texture2D(u_texture, v_texCoords0);

  vec4 pixel = vec4(1.0);

  pixel.r = pixel_wood.r;
  pixel.b = pixel_wood.b;
  pixel.g = pixel_wood.g;
  pixel.a = pixel_mask.a;

  gl_FragColor = pixel;
}
