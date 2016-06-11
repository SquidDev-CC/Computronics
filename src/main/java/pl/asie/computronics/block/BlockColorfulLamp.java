package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.item.block.IBlockWithSpecialText;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileColorfulLamp;

import java.util.List;
//import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
//import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode", modid = Mods.MFR)
})*/
public class BlockColorfulLamp extends BlockPeripheral implements IBlockWithSpecialText/*implements IRedNetInputNode*/ {

	//public static final PropertyInteger BRIGHTNESS = PropertyInteger.create("brightness", 0, 15);
	public static final PropertyBool LIGHT = PropertyBool.create("light");
	public static final PropertyBool CTM = PropertyBool.create("ctm");

	public BlockColorfulLamp() {
		super("colorful_lamp", Rotation.NONE);
		this.setUnlocalizedName("computronics.colorfulLamp");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileColorfulLamp();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		/*if(!world.isRemote && Mods.isLoaded(Mods.MFR) && player.isSneaking()) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileColorfulLamp) {
				ItemStack held = player.getCurrentEquippedItem();
				if(held != null && held.getItem() != null && Integration.isTool(held, player, pos) && Integration.useTool(held, player, pos)) {
					TileColorfulLamp lamp = (TileColorfulLamp) tile;
					lamp.setBinaryMode(!lamp.isBinaryMode());
					player.addChatMessage(new ChatComponentTranslation("chat.computronics.lamp.binary." + (lamp.isBinaryMode() ? "on" : "off")));
					return true;
				}
			}
		}*/
		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Override
	protected BlockStateContainer createActualBlockState() {
		return new BlockStateContainer(this, BUNDLED, LIGHT, CTM);
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(LIGHT, (meta & 1) == 1).withProperty(CTM, ((meta >> 3) & 1) == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) | (state.getValue(LIGHT) ? 1 : 0) | ((state.getValue(CTM) ? 1 : 0) << 3);
	}

	@Override
	protected IBlockState createDefaultState() {
		return super.createDefaultState().withProperty(LIGHT, false).withProperty(CTM, false);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return super.damageDropped(state) | ((state.getValue(CTM) ? 1 : 0) << 3);
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		/*TileEntity tile = world.getTileEntity(x, y, z);
		if(Mods.isLoaded(Mods.ProjectRed) && tile instanceof TileColorfulLamp) {
			((TileColorfulLamp) tile).onProjectRedBundledInputChanged();
		}*/
		super.neighborChanged(state, world, pos, block);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		/*TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileColorfulLamp) {
			return this.lightValue = ((TileColorfulLamp) tile).getLampColor() != 0 ? 15 : 0;
			int color = ((TileColorfulLamp) tile).getLampColor();
			//this.lightValue = world.getBlockState(pos).getValue(BRIGHTNESS);
			this.lightValue = color == 0 ? 0 : 15;
			if(world instanceof World) {
				((World) world).notifyLightSet(pos);
				((World) world).markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
			}
			return this.lightValue;
		}*/
		return this.lightValue = state.getValue(LIGHT) ? 15 : 0;
	}

	@Override
	@Deprecated
	public int getLightOpacity(IBlockState state) {
		return super.getLightOpacity(state);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileColorfulLamp) {
			return state.withProperty(LIGHT, ((TileColorfulLamp) tile).getLampColor() != 0);
		} else {
			return state;
		}
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT_MIPPED || super.canRenderInLayer(state, layer);
	}

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int pass) {
		if(pass != 0) {
			return super.colorMultiplier(state, world, pos, pass);
		}
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileColorfulLamp) {
			int color = ((TileColorfulLamp) tile).getLampColor();
			return (color & (0x1F << 10)) << 9 | (color & (0x1F << 5)) << 6 | ((color & 0x1F) << 3);
		}
		return super.colorMultiplier(state, world, pos, pass);
	}

	@Override
	public boolean supportsBundledRedstone() {
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if(((stack.getMetadata() >> 3) & 1) == 1) {
			tooltip.add("It connects!");
		}
		super.addInformation(stack, player, tooltip, advanced);
	}
/*@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return LampRender.id();
	}*/

	/*@Override
	@Optional.Method(modid = Mods.MFR)
	public RedNetConnectionType getConnectionType(World world, int x, int y,
		int z, ForgeDirection side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileColorfulLamp && ((TileColorfulLamp) tile).isBinaryMode()) {
			return RedNetConnectionType.CableAll;
		}
		return RedNetConnectionType.CableSingle;
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public void onInputsChanged(World world, int x, int y, int z,
		ForgeDirection side, int[] inputValues) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileColorfulLamp && ((TileColorfulLamp) tile).isBinaryMode()) {
			int c = 0;
			for(int i = 0; i < 15; i++) {
				if(inputValues[i] != 0) {
					c |= (1 << i);
				}
			}
			((TileColorfulLamp) tile).setLampColor(c);
		}
	}

	@Override
	@Optional.Method(modid = Mods.MFR)
	public void onInputChanged(World world, int x, int y, int z,
		ForgeDirection side, int inputValue) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileColorfulLamp && !((TileColorfulLamp) tile).isBinaryMode()) {
			((TileColorfulLamp) tile).setLampColor(inputValue & 0x7FFF);
		}
	}*/

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileColorfulLamp.class;
	}

	@Override
	public boolean hasSubTypes() {
		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName();
	}
}
