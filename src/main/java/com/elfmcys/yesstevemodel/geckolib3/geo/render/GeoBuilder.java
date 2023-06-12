package com.elfmcys.yesstevemodel.geckolib3.geo.render;

import com.elfmcys.yesstevemodel.geckolib3.geo.raw.pojo.Bone;
import com.elfmcys.yesstevemodel.geckolib3.geo.raw.pojo.Cube;
import com.elfmcys.yesstevemodel.geckolib3.geo.raw.pojo.ModelProperties;
import com.elfmcys.yesstevemodel.geckolib3.geo.raw.tree.RawBoneGroup;
import com.elfmcys.yesstevemodel.geckolib3.geo.raw.tree.RawGeometryTree;
import com.elfmcys.yesstevemodel.geckolib3.geo.render.built.GeoBone;
import com.elfmcys.yesstevemodel.geckolib3.geo.render.built.GeoCube;
import com.elfmcys.yesstevemodel.geckolib3.geo.render.built.GeoModel;
import com.elfmcys.yesstevemodel.geckolib3.util.VectorUtils;
import com.elfmcys.yesstevemodel.util.Keep;
import org.joml.Vector3f;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;

public class GeoBuilder implements IGeoBuilder {
    private static final Map<String, IGeoBuilder> MODDED_GEO_BUILDERS = new Object2ObjectOpenHashMap<>();
    private static final IGeoBuilder DEFAULT_BUILDER = new GeoBuilder();

    public static void registerGeoBuilder(String modid, IGeoBuilder builder) {
        MODDED_GEO_BUILDERS.put(modid, builder);
    }

    public static IGeoBuilder getGeoBuilder(String modid) {
        IGeoBuilder builder = MODDED_GEO_BUILDERS.get(modid);
        return builder == null ? DEFAULT_BUILDER : builder;
    }

    @Override
    @Keep
    public GeoModel constructGeoModel(RawGeometryTree geometryTree) {
        GeoModel model = new GeoModel();
        model.properties = geometryTree.properties;
        for (RawBoneGroup rawBone : geometryTree.topLevelBones.values()) {
            model.topLevelBones.add(this.constructBone(rawBone, geometryTree.properties, null));
        }
        return model;
    }

    @Override
    @Keep
    public GeoBone constructBone(RawBoneGroup bone, ModelProperties properties, GeoBone parent) {
        GeoBone geoBone = new GeoBone();

        Bone rawBone = bone.selfBone;
        Vector3f rotation = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(rawBone.getRotation()));
        Vector3f pivot = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(rawBone.getPivot()));
        rotation.mul(-1, -1, 1);

        geoBone.mirror = rawBone.getMirror();
        geoBone.dontRender = rawBone.getNeverRender();
        geoBone.reset = rawBone.getReset();
        geoBone.inflate = rawBone.getInflate();
        geoBone.parent = parent;
        geoBone.setModelRendererName(rawBone.getName());

        geoBone.setRotationX((float) Math.toRadians(rotation.x()));
        geoBone.setRotationY((float) Math.toRadians(rotation.y()));
        geoBone.setRotationZ((float) Math.toRadians(rotation.z()));

        geoBone.rotationPointX = -pivot.x();
        geoBone.rotationPointY = pivot.y();
        geoBone.rotationPointZ = pivot.z();

        if (!ArrayUtils.isEmpty(rawBone.getCubes())) {
            for (Cube cube : rawBone.getCubes()) {
                geoBone.childCubes.add(GeoCube.createFromPojoCube(cube, properties,
                        geoBone.inflate == null ? null : geoBone.inflate / 16, geoBone.mirror));
            }
        }

        for (RawBoneGroup child : bone.children.values()) {
            geoBone.childBones.add(constructBone(child, properties, geoBone));
        }

        return geoBone;
    }
}
