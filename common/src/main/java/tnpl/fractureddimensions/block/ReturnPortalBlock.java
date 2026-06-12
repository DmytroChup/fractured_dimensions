package tnpl.fractureddimensions.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ReturnPortalBlock extends Block {

    private static final int TELEPORT_COOLDOWN_TICKS = 60;
    private static final Map<UUID, Long> RECENT_TELEPORTS = new ConcurrentHashMap<>();

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14, 16);

    public ReturnPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level,
                                  BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level,
                                           BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void entityInside(BlockState state, Level level,
                                BlockPos pos, Entity entity,
                                InsideBlockEffectApplier applier, boolean isWalkedOn) {
        if (level.isClientSide()) return;
        if (!(entity instanceof ServerPlayer player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        long currentTick = serverLevel.getGameTime();
        Long lastTeleport = RECENT_TELEPORTS.get(player.getUUID());
        if (lastTeleport != null && currentTick - lastTeleport < TELEPORT_COOLDOWN_TICKS) return;

        RECENT_TELEPORTS.put(player.getUUID(), currentTick);
        tnpl.fractureddimensions.worldgen.IslandManager manager = tnpl.fractureddimensions.worldgen.IslandManager.getVoidInstance();
        if (manager == null) manager = tnpl.fractureddimensions.worldgen.IslandManager.get(serverLevel); // fallback

        var entry = manager.findIslandAt(pos.getX(), pos.getZ());
        
        ServerLevel targetLevel = null;
        BlockPos targetPos = null;

        if (entry != null) {
            tnpl.fractureddimensions.worldgen.IslandManager.ActiveIsland island = entry.getValue();
            if (island.returnDimension() != null) {
                net.minecraft.resources.ResourceKey<Level> dimKey = net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION,
                        net.minecraft.resources.Identifier.parse(island.returnDimension())
                );
                targetLevel = serverLevel.getServer().getLevel(dimKey);
            }
            targetPos = island.returnPos();
        }

        // Fallback if island data is missing
        if (targetLevel == null || targetPos == null || targetPos.equals(BlockPos.ZERO)) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            targetLevel = overworld;
            if (overworld != null) {
                var respawnConfig = player.getRespawnConfig();
                if (respawnConfig != null && Level.OVERWORLD.equals(respawnConfig.respawnData().dimension())) {
                    targetPos = respawnConfig.respawnData().pos();
                } else {
                    targetPos = overworld.getRespawnData().pos();
                }
            }
        }

        if (targetLevel == null) return;

        level.playSound(null, pos, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.5F, 1.2F);

        player.teleportTo(
                targetLevel,
                targetPos.getX() + 0.5,
                targetPos.getY() + 1.0,
                targetPos.getZ() + 0.5,
                Set.of(),
                player.getYRot(),
                player.getXRot(),
                true
        );

        player.addEffect(new MobEffectInstance(
                MobEffects.SLOW_FALLING,
                100, // 5 seconds
                0,
                false,
                false
        ));

        player.sendSystemMessage(
                Component.translatable("message.fractured_dimensions.return_portal.used")
                        .withStyle(ChatFormatting.GREEN)
        );

        targetLevel.playSound(null, targetPos, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void animateTick(BlockState state, Level level,
                            BlockPos pos, RandomSource random) {
        for (int i = 0; i < 6; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.2 + random.nextDouble() * 0.3;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double y = pos.getY() + random.nextDouble() * 0.8;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;

            level.addParticle(ParticleTypes.REVERSE_PORTAL, x, y, z, 0, 0.08, 0);
        }

        if (random.nextInt(4) == 0) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double y = pos.getY() + 0.5 + random.nextDouble() * 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            level.addParticle(ParticleTypes.PORTAL, x, y, z,
                    (random.nextDouble() - 0.5) * 0.3, 0.1, (random.nextDouble() - 0.5) * 0.3);
        }

        if (random.nextInt(6) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble() * 0.6;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.ENCHANT, x, y, z, 0, 0.2, 0);
        }
    }
}
