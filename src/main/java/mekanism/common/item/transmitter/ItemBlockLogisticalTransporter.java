package mekanism.common.item.transmitter;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcmultipart.api.multipart.IMultipart;
import mekanism.api.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.Mekanism;
import mekanism.common.block.transmitter.BlockLogisticalTransporter;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.integration.multipart.MultipartMekanism;
import mekanism.common.item.ITieredItem;
import mekanism.common.item.ItemBlockMultipartAble;
import mekanism.common.tier.BaseTier;
import mekanism.common.tier.TransporterTier;
import mekanism.common.tile.transmitter.TileEntitySidedPipe;
import mekanism.common.util.LangUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockLogisticalTransporter extends ItemBlockMultipartAble implements ITieredItem<TransporterTier> {

    public ItemBlockLogisticalTransporter(BlockLogisticalTransporter block) {
        super(block);
    }

    @Nullable
    @Override
    public TransporterTier getTier(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlockLogisticalTransporter) {
            BlockLogisticalTransporter block = (BlockLogisticalTransporter) ((ItemBlockLogisticalTransporter) item).block;
            return block.getTier();
        }
        return null;
    }

    @Override
    public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY,
          float hitZ, @Nonnull IBlockState state) {
        boolean place = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state);
        if (place) {
            BaseTier baseTier = getBaseTier(stack);
            if (baseTier != null) {
                //TODO: Make the TileEntity tier be set on creation as the Block knows it now
                TileEntitySidedPipe tileEntity = (TileEntitySidedPipe) world.getTileEntity(pos);
                tileEntity.setBaseTier(baseTier);
                if (!world.isRemote) {
                    Mekanism.packetHandler.sendUpdatePacket(tileEntity);
                }
            }
        }
        return place;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack itemstack, World world, @Nonnull List<String> list, @Nonnull ITooltipFlag flag) {
        if (!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey)) {
            TransporterTier tier = getTier(itemstack);
            if (tier != null) {
                list.add(EnumColor.INDIGO + LangUtils.localize("tooltip.speed") + ": " + EnumColor.GREY + (tier.getSpeed() / (100 / 20)) + " m/s");
                list.add(EnumColor.INDIGO + LangUtils.localize("tooltip.pumpRate") + ": " + EnumColor.GREY + tier.getPullAmount() * 2 + "/s");
            }
            list.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.AQUA + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) +
                     EnumColor.GREY + " " + LangUtils.localize("tooltip.forDetails"));
        } else {
            list.add(EnumColor.DARK_GREY + LangUtils.localize("tooltip.capableTrans") + ":");
            list.add("- " + EnumColor.PURPLE + LangUtils.localize("tooltip.items") + " (" + LangUtils.localize("tooltip.universal") + ")");
            list.add("- " + EnumColor.PURPLE + LangUtils.localize("tooltip.blocks") + " (" + LangUtils.localize("tooltip.universal") + ")");
        }
    }

    @Override
    @Optional.Method(modid = MekanismHooks.MCMULTIPART_MOD_ID)
    protected IMultipart getMultiPart() {
        //TODO
        return MultipartMekanism.TRANSMITTER_MP;
    }
}