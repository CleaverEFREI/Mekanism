package mekanism.common.multipart;

import java.io.IOException;
import java.util.List;

import static mekanism.client.ClientProxy.CUSTOM_RENDERS;
import static mekanism.common.block.states.BlockStateMachine.MachineBlock.MACHINE_BLOCK_1;
import static mekanism.common.block.states.BlockStateMachine.MachineBlock.MACHINE_BLOCK_2;

import mcmultipart.client.multipart.ISmartMultipartModel;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IPartFactory;
import mcmultipart.multipart.MultipartRegistry;
import mekanism.common.MekanismBlocks;
import mekanism.common.Tier;
import mekanism.common.block.states.BlockStateMachine.MachineType;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.MultipartGenerator;
import codechicken.multipart.TMultiPart;
*/

public class MultipartMekanism implements IPartFactory
{
	public MultipartMekanism()
	{
		init();
	}

	public void init()
	{
		MultipartRegistry.registerPartFactory(this, "mekanism:universal_cable_basic",
				"mekanism:universal_cable_advanced", "mekanism:universal_cable_elite",
				"mekanism:universal_cable_ultimate", "mekanism:mechanical_pipe",
				"mekanism:mechanical_pipe_basic", "mekanism:mechanical_pipe_advanced",
				"mekanism:mechanical_pipe_elite", "mekanism:mechanical_pipe_ultimate",
				"mekanism:pressurized_tube_basic", "mekanism:pressurized_tube_advanced",
				"mekanism:pressurized_tube_elite", "mekanism:pressurized_tube_ultimate",
				"mekanism:logistical_transporter_basic", "mekanism:logistical_transporter_advanced", 
				"mekanism:logistical_transporter_elite", "mekanism:logistical_transporter_ultimate", 
				"mekanism:restrictive_transporter", "mekanism:diversion_transporter", 
				"mekanism:thermodynamic_conductor_basic", "mekanism:thermodynamic_conductor_advanced",
				"mekanism:thermodynamic_conductor_elite", "mekanism:thermodynamic_conductor_ultimate",
				"mekanism:glow_panel");

		registerMicroMaterials();
	}

	@Override
	public IMultipart createPart(String name, boolean client)
	{
		if(name.equals("mekanism:universal_cable"))
		{
			return new PartUniversalCable(Tier.CableTier.BASIC);
		}
		else if(name.equals("mekanism:universal_cable_basic"))
		{
			return new PartUniversalCable(Tier.CableTier.BASIC);
		}
		else if(name.equals("mekanism:universal_cable_advanced"))
		{
			return new PartUniversalCable(Tier.CableTier.ADVANCED);
		}
		else if(name.equals("mekanism:universal_cable_elite"))
		{
			return new PartUniversalCable(Tier.CableTier.ELITE);
		}
		else if(name.equals("mekanism:universal_cable_ultimate"))
		{
			return new PartUniversalCable(Tier.CableTier.ULTIMATE);
		}
		else if(name.equals("mekanism:mechanical_pipe"))
		{
			return new PartMechanicalPipe(Tier.PipeTier.BASIC);
		}
		else if(name.equals("mekanism:mechanical_pipe_basic"))
		{
			return new PartMechanicalPipe(Tier.PipeTier.BASIC);
		}
		else if(name.equals("mekanism:mechanical_pipe_advanced"))
		{
			return new PartMechanicalPipe(Tier.PipeTier.ADVANCED);
		}
		else if(name.equals("mekanism:mechanical_pipe_elite"))
		{
			return new PartMechanicalPipe(Tier.PipeTier.ELITE);
		}
		else if(name.equals("mekanism:mechanical_pipe_ultimate"))
		{
			return new PartMechanicalPipe(Tier.PipeTier.ULTIMATE);
		}
		else if(name.equals("mekanism:pressurized_tube_basic") || name.equals("mekanism:pressurized_tube"))
		{
			return new PartPressurizedTube(Tier.TubeTier.BASIC);
		}
		else if(name.equals("mekanism:pressurized_tube_advanced"))
		{
			return new PartPressurizedTube(Tier.TubeTier.ADVANCED);
		}
		else if(name.equals("mekanism:pressurized_tube_elite"))
		{
			return new PartPressurizedTube(Tier.TubeTier.ELITE);
		}
		else if(name.equals("mekanism:pressurized_tube_ultimate"))
		{
			return new PartPressurizedTube(Tier.TubeTier.ULTIMATE);
		}
		else if(name.equals("mekanism:logistical_transporter_basic") || name.equals("mekanism:logistical_transporter"))
		{
			return new PartLogisticalTransporter(Tier.TransporterTier.BASIC);
		}
		else if(name.equals("mekanism:logistical_transporter_advanced"))
		{
			return new PartLogisticalTransporter(Tier.TransporterTier.ADVANCED);
		}
		else if(name.equals("mekanism:logistical_transporter_elite"))
		{
			return new PartLogisticalTransporter(Tier.TransporterTier.ELITE);
		}
		else if(name.equals("mekanism:logistical_transporter_ultimate"))
		{
			return new PartLogisticalTransporter(Tier.TransporterTier.ULTIMATE);
		}
		else if(name.equals("mekanism:restrictive_transporter"))
		{
			return new PartRestrictiveTransporter();
		}
		else if(name.equals("mekanism:diversion_transporter"))
		{
			return new PartDiversionTransporter();
		}
		else if(name.equals("mekanism:thermodynamic_conductor_basic"))
		{
			return new PartThermodynamicConductor(Tier.ConductorTier.BASIC);
		}
		else if(name.equals("mekanism:thermodynamic_conductor_advanced"))
		{
			return new PartThermodynamicConductor(Tier.ConductorTier.ADVANCED);
		}
		else if(name.equals("mekanism:thermodynamic_conductor_elite"))
		{
			return new PartThermodynamicConductor(Tier.ConductorTier.ELITE);
		}
		else if(name.equals("mekanism:thermodynamic_conductor_ultimate"))
		{
			return new PartThermodynamicConductor(Tier.ConductorTier.ULTIMATE);
		}
		else if(name.equals("mekanism:glow_panel"))
		{
			return new PartGlowPanel();
		}

		return null;
	}

	public void registerMicroMaterials()
	{
		for(int i = 0; i < 16; i++)
		{
//			MicroblockRegistry.registerMaterial(new PlasticMicroMaterial(MekanismBlocks.PlasticBlock, i), BlockMicroMaterial.materialKey(MekanismBlocks.PlasticBlock, i));
//			MicroMaterialRegistry.registerMaterial(new PlasticMicroMaterial(MekanismBlocks.GlowPlasticBlock, i), BlockMicroMaterial.materialKey(MekanismBlocks.GlowPlasticBlock, i));
//			MicroMaterialRegistry.registerMaterial(new PlasticMicroMaterial(MekanismBlocks.SlickPlasticBlock, i), BlockMicroMaterial.materialKey(MekanismBlocks.SlickPlasticBlock, i));
//			MicroMaterialRegistry.registerMaterial(new PlasticMicroMaterial(MekanismBlocks.ReinforcedPlasticBlock, i), BlockMicroMaterial.materialKey(MekanismBlocks.ReinforcedPlasticBlock, i));
//			MicroMaterialRegistry.registerMaterial(new PlasticMicroMaterial(MekanismBlocks.RoadPlasticBlock, i), BlockMicroMaterial.materialKey(MekanismBlocks.RoadPlasticBlock, i));

			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(MekanismBlocks.BasicBlock, 1, i));
			
			if(!MachineType.get(MACHINE_BLOCK_1, i).hasModel)
			{
				FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(MekanismBlocks.MachineBlock, 1, i));
			}
			
			if(!MachineType.get(MACHINE_BLOCK_2, i).hasModel)
			{
				FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(MekanismBlocks.MachineBlock2, 1, i));
			}
		}
		
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(MekanismBlocks.BasicBlock2, 1, 0));
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", new ItemStack(MekanismBlocks.CardboardBox));
	}
	
	public static void dropItem(ItemStack stack, World world, Vec3 pos)
	{
		EntityItem item = new EntityItem(world, pos.xCoord, pos.yCoord, pos.zCoord, stack);
        item.motionX = world.rand.nextGaussian() * 0.05;
        item.motionY = world.rand.nextGaussian() * 0.05 + 0.2;
        item.motionZ = world.rand.nextGaussian() * 0.05;
        item.setDefaultPickupDelay();
        world.spawnEntityInWorld(item);
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event)
	{
		String[] names = new String[] {"mekanism:universal_cable_basic",
				"mekanism:universal_cable_advanced", "mekanism:universal_cable_elite",
				"mekanism:universal_cable_ultimate", "mekanism:mechanical_pipe",
				"mekanism:mechanical_pipe_basic", "mekanism:mechanical_pipe_advanced",
				"mekanism:mechanical_pipe_elite", "mekanism:mechanical_pipe_ultimate",
				"mekanism:pressurized_tube_basic", "mekanism:pressurized_tube_advanced",
				"mekanism:pressurized_tube_elite", "mekanism:pressurized_tube_ultimate",
				"mekanism:logistical_transporter_basic", "mekanism:logistical_transporter_advanced",
				"mekanism:logistical_transporter_elite", "mekanism:logistical_transporter_ultimate",
				"mekanism:restrictive_transporter", "mekanism:diversion_transporter",
				"mekanism:thermodynamic_conductor_basic", "mekanism:thermodynamic_conductor_advanced",
				"mekanism:thermodynamic_conductor_elite", "mekanism:thermodynamic_conductor_ultimate"};

		for(String s : names)
		{
			ModelResourceLocation mrl = new ModelResourceLocation(s, "multipart");
			IBakedModel model = event.modelRegistry.getObject(mrl);
			if(model instanceof ISmartBlockModel)
			{
				event.modelRegistry.putObject(mrl, new BlockToMultipartModel((ISmartBlockModel)model));
			}
		}
	}

}
