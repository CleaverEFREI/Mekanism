package mekanism.client.render.lib;

import java.util.function.Consumer;
import mekanism.common.lib.Color;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class Quad {

    private static final VertexFormat FORMAT = DefaultVertexFormats.BLOCK;
    private static final int SIZE = DefaultVertexFormats.BLOCK.getElements().size();

    private Vertex[] vertices;
    private boolean applyDiffuseLighting;
    private Direction side;
    private int tintIndex = -1;
    private TextureAtlasSprite sprite;

    private Quad(TextureAtlasSprite sprite, Direction side) {
        this.sprite = sprite;
        this.side = side;
    }

    public Quad(TextureAtlasSprite sprite, Direction side, Vertex[] vertices) {
        this(sprite, side);
        this.vertices = vertices;
    }

    public Quad(BakedQuad quad) {
        vertices = new Vertex[4];
        quad.pipe(new BakedQuadUnpacker());
    }

    public TextureAtlasSprite getTexture() {
        return sprite;
    }

    public void transform(Consumer<Vertex> transformation) {
        for (Vertex v : vertices) {
            transformation.accept(v);
        }
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public BakedQuad bake() {
        int[] ret = new int[FORMAT.getIntegerSize() * 4];
        for (int v = 0; v < vertices.length; v++) {
            float[][] packed = vertices[v].pack(FORMAT);
            for (int e = 0; e < SIZE; e++) {
                LightUtil.pack(packed[e], ret, DefaultVertexFormats.BLOCK, v, e);
            }
        }
        return new BakedQuad(ret, tintIndex, side, sprite, applyDiffuseLighting);
    }

    private class BakedQuadUnpacker implements IVertexConsumer {

        private Vertex vertex = new Vertex();
        private int vertexIndex = 0;

        @Override
        public VertexFormat getVertexFormat() {
            return FORMAT;
        }

        @Override
        public void setQuadTint(int tint) {
            tintIndex = tint;
        }

        @Override
        public void setQuadOrientation(Direction orientation) {
            side = orientation;
        }

        @Override
        public void setApplyDiffuseLighting(boolean diffuse) {
            applyDiffuseLighting = diffuse;
        }

        @Override
        public void setTexture(TextureAtlasSprite texture) {
            sprite = texture;
        }

        @Override
        public void put(int elementIndex, float... data) {
            VertexFormatElement element = FORMAT.getElements().get(elementIndex);
            float f0 = data.length >= 1 ? data[0] : 0;
            float f1 = data.length >= 2 ? data[1] : 0;
            float f2 = data.length >= 3 ? data[2] : 0;
            float f3 = data.length >= 4 ? data[3] : 0;
            switch (element.getUsage()) {
                case POSITION:
                    vertex.pos(new Vec3d(f0, f1, f2));
                    break;
                case NORMAL:
                    vertex.normal(new Vec3d(f0, f1, f2));
                    break;
                case COLOR:
                    vertex.color(Color.rgba(f0, f1, f2, f3));
                    break;
                case UV: {
                    switch (element.getIndex()) {
                        case 0:
                            vertex.texRaw(f0, f1);
                            break;
                        case 2:
                            vertex.lightRaw(f0, f1);
                            break;
                        default:
                            break;
                    }
                    break;
                }
                default:
                    break;
            }
            if (elementIndex == SIZE - 1) {
                vertices[vertexIndex++] = vertex;
                vertex = new Vertex();
            }
        }
    }

    public static class Builder {

        private TextureAtlasSprite texture;
        private Direction side;

        private Vec3d vec1, vec2, vec3, vec4;

        private float minU, minV, maxU, maxV;
        private float lightU, lightV;

        private int tintIndex = -1;
        private boolean contractUVs = true;

        public Builder(TextureAtlasSprite texture, Direction side) {
            this.texture = texture;
            this.side = side;
        }

        public Builder light(float u, float v) {
            this.lightU = u;
            this.lightV = v;
            return this;
        }

        public Builder uv(float minU, float minV, float maxU, float maxV) {
            this.minU = minU;
            this.minV = minV;
            this.maxU = maxU;
            this.maxV = maxV;
            return this;
        }

        public Builder tex(TextureAtlasSprite texture) {
            this.texture = texture;
            return this;
        }

        public Builder tint(int tintIndex) {
            this.tintIndex = tintIndex;
            return this;
        }

        public Builder contractUVs() {
            this.contractUVs = true;
            return this;
        }

        public Builder pos(Vec3d tl, Vec3d bl, Vec3d br, Vec3d tr) {
            this.vec1 = tl;
            this.vec2 = bl;
            this.vec3 = br;
            this.vec4 = tr;
            return this;
        }

        public Builder rect(Vec3d start, double width, double height) {
            return rect(start, width, height, 1F / 16F); // default to 1/16 scale
        }
        // start = bottom left
        public Builder rect(Vec3d start, double width, double height, double scale) {
            start = start.scale(scale);
            return pos(start.add(0, height * scale, 0), start, start.add(width * scale, 0, 0), start.add(width * scale, height * scale, 0));
        }

        public Quad build() {
            Vertex[] vertices = new Vertex[4];
            Vec3d normal = vec3.subtract(vec2).crossProduct(vec1.subtract(vec2)).normalize();
            vertices[0] = Vertex.create(vec1, normal, texture, minU, minV).light(lightU, lightV);
            vertices[1] = Vertex.create(vec2, normal, texture, minU, maxV).light(lightU, lightV);
            vertices[2] = Vertex.create(vec3, normal, texture, maxU, maxV).light(lightU, lightV);
            vertices[3] = Vertex.create(vec4, normal, texture, maxU, minV).light(lightU, lightV);
            Quad quad = new Quad(texture, side, vertices);
            quad.tintIndex = tintIndex;
            if (contractUVs) {
                QuadUtils.contractUVs(quad);
            }
            return quad;
        }
    }
}