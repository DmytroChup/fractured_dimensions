package tnpl.fractureddimensions.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
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

public record DimensionData(String dimensionId) implements TooltipProvider {

    public static final Codec<DimensionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("dimensionId").forGetter(DimensionData::dimensionId)
    ).apply(instance, DimensionData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            DimensionData::dimensionId,
            DimensionData::new
    );

    @Override
    public void addToTooltip(
            Item.@NonNull TooltipContext context,
            Consumer<Component> tooltipAdder,
            @NonNull TooltipFlag tooltipFlag,
            @NonNull DataComponentGetter dataComponentGetter)
    {
        tooltipAdder.accept(Component.translatable("tooltip.fractured_dimensions.shard_receptacle.contains_data").withStyle(ChatFormatting.AQUA));
        tooltipAdder.accept(Component.literal("ID: " + dimensionId).withStyle(ChatFormatting.GRAY));
    }
}
