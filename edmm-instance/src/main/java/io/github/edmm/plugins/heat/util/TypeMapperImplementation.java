package io.github.edmm.plugins.heat.util;

import io.github.edmm.core.plugin.TypeMapper;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.plugins.heat.model.types.AodhType;
import io.github.edmm.plugins.heat.model.types.BarbicanType;
import io.github.edmm.plugins.heat.model.types.BlazarType;
import io.github.edmm.plugins.heat.model.types.CinderType;
import io.github.edmm.plugins.heat.model.types.DesignateType;
import io.github.edmm.plugins.heat.model.types.GlanceType;
import io.github.edmm.plugins.heat.model.types.HeatType;
import io.github.edmm.plugins.heat.model.types.IronicType;
import io.github.edmm.plugins.heat.model.types.KeystoneType;
import io.github.edmm.plugins.heat.model.types.LBaaSType;
import io.github.edmm.plugins.heat.model.types.MagnumType;
import io.github.edmm.plugins.heat.model.types.ManilaType;
import io.github.edmm.plugins.heat.model.types.MistralType;
import io.github.edmm.plugins.heat.model.types.MonascaType;
import io.github.edmm.plugins.heat.model.types.NeutronType;
import io.github.edmm.plugins.heat.model.types.NovaType;
import io.github.edmm.plugins.heat.model.types.OctaviaType;
import io.github.edmm.plugins.heat.model.types.SaharaType;
import io.github.edmm.plugins.heat.model.types.SenlinType;
import io.github.edmm.plugins.heat.model.types.SwiftType;
import io.github.edmm.plugins.heat.model.types.TaaSType;
import io.github.edmm.plugins.heat.model.types.TroveType;
import io.github.edmm.plugins.heat.model.types.ZaqarType;
import io.github.edmm.plugins.heat.model.types.ZunType;

public class TypeMapperImplementation implements TypeMapper {

    @Override
    public ComponentType toComponentType(String type) {
        switch (extractTopLevelType(type)) {
            case HeatConstants.Aodh:
                return AodhType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Barbican:
                return BarbicanType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Blazar:
                return BlazarType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Cinder:
                return CinderType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Designate:
                return DesignateType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Glance:
                return GlanceType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Heat:
                return HeatType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Ironic:
                return IronicType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Keystone:
                return KeystoneType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.LBaaS:
                return LBaaSType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Magnum:
                return MagnumType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Manila:
                return ManilaType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Mistral:
                return MistralType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Monasca:
                return MonascaType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Neutron:
                return NeutronType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Nova:
                return NovaType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Octavia:
                return OctaviaType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Sahara:
                return SaharaType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Senlin:
                return SenlinType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Swift:
                return SwiftType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.TaaS:
                return TaaSType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Trove:
                return TroveType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Zaqar:
                return ZaqarType.valueOf(extractSpecificType(type)).toComponentType();
            case HeatConstants.Zun:
                return ZunType.valueOf(extractSpecificType(type)).toComponentType();
            default:
                return ComponentType.Compute;
        }
    }

    @Override
    public String extractTopLevelType(String type) {
        return type.substring(0, type.lastIndexOf(HeatConstants.DELIMITER) + 2);
    }

    @Override
    public String extractSpecificType(String type) {
        return type.substring(type.lastIndexOf(HeatConstants.DELIMITER) + 2, type.length());
    }
}
