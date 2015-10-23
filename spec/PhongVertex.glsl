#version 130

out vec4 C;
out vec3 N; 
out vec4 v; 

in vec4 vertexCol;
in vec4 vertexPos;


void main (void) {	
    v = gl_ModelViewMatrix * gl_Vertex;
    N = vec3(normalize(gl_NormalMatrix * normalize(gl_Normal)));
	gl_Position = gl_ModelViewProjectionMatrix * vertexPos;
	C = vertexCol;
}

