package tnpl.fractureddimensions.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public record DimensionData(String name, int distance, int difficulty, int survivalTime, int sizeType, int type, int variant) implements TooltipProvider {

    public static final Codec<DimensionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(DimensionData::name),
            Codec.INT.fieldOf("distance").forGetter(DimensionData::distance),
            Codec.INT.fieldOf("difficulty").forGetter(DimensionData::difficulty),
            Codec.INT.fieldOf("survivalTime").forGetter(DimensionData::survivalTime),
            Codec.INT.fieldOf("sizeType").forGetter(DimensionData::sizeType),
            Codec.INT.fieldOf("type").forGetter(DimensionData::type),
            Codec.INT.fieldOf("variant").forGetter(DimensionData::variant)
    ).apply(instance, DimensionData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DimensionData::name,
            ByteBufCodecs.INT, DimensionData::distance,
            ByteBufCodecs.INT, DimensionData::difficulty,
            ByteBufCodecs.INT, DimensionData::survivalTime,
            ByteBufCodecs.INT, DimensionData::sizeType,
            ByteBufCodecs.INT, DimensionData::type,
            ByteBufCodecs.INT, DimensionData::variant,
            DimensionData::new
    );

    @Override
    public void addToTooltip(
            Item.@NonNull TooltipContext context,
            @NonNull Consumer<Component> tooltipAdder,
            @NonNull TooltipFlag tooltipFlag,
            @NonNull DataComponentGetter dataComponentGetter)
    {
        // Handled via LORE component directly
    }

    public int getBaseRadius() {
        return switch (this.sizeType) {
            case 0 -> 170;
            case 1 -> 250;
            default -> 350;
        };
    }
}
