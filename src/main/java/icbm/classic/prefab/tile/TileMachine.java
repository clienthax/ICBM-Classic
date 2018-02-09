package icbm.classic.prefab.tile;

import com.builtbroken.jlib.data.network.IByteBufWriter;
import icbm.classic.api.IWorldPosition;
import icbm.classic.lib.network.IPacket;
import icbm.classic.prefab.gui.IPlayerUsing;
import icbm.classic.lib.IGuiTile;
import icbm.classic.lib.network.IPacketIDReceiver;
import icbm.classic.lib.network.packet.PacketTile;
import icbm.classic.ICBMClassic;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/9/2017.
 */
public abstract class TileMachine extends TileEntity implements IPacketIDReceiver, IWorldPosition, IPlayerUsing, ITickable, IByteBufWriter, IGuiTile
{
    public static final int DESC_PACKET_ID = -1;
    /**
     * Toggle to send a {@link #getUpdatePacket()} on the next tick, keep in mind only do this for render data.
     * if the data is not used by the renderer then send it at the time it is needed. For example, GUI data
     * should be sent to only GUI users and not everyone.
     */
    protected boolean updateClient = false;

    protected int ticks = 0;

    // Cache until block state can be updated
    public EnumTier _tier = EnumTier.ONE;

    List<EntityPlayer> playersWithGUI = new ArrayList();

    @Override
    public void update()
    {
        ticks++;
        if (ticks >= Integer.MAX_VALUE - 1)
        {
            ticks = 0;
        }

        if (isServer())
        {
            //Sync client(s) if needed
            if (updateClient)
            {
                updateClient = false;
                sendDescPacket();
            }
            //Sync GUI data to client(s)
            if (ticks % 3 == 0 && getPlayersUsing().size() > 0)
            {
                PacketTile packet = getGUIPacket();
                if (packet != null)
                {
                    sendPacketToGuiUsers(packet);
                }
            }
        }
    }

    public void sendDescPacket()
    {
        PacketTile packetTile = getDescPacket();
        if (packetTile != null)
        {
            ICBMClassic.packetHandler.sendToAllAround(packetTile, this);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        _tier = EnumTier.get(compound.getInteger("tier"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("tier", _tier.ordinal());
        return super.writeToNBT(compound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    public PacketTile getDescPacket()
    {
        PacketTile packetTile = new PacketTile("desc", DESC_PACKET_ID, this);
        packetTile.addData(this); //Should call back to IByteBufWriter
        return packetTile;
    }

    public void sendPacketToGuiUsers(IPacket packet)
    {
        if (packet != null)
        {
            Iterator<EntityPlayer> it = getPlayersUsing().iterator();
            while (it.hasNext())
            {
                final EntityPlayer player = it.next();
                if (player instanceof EntityPlayerMP && isValidGuiUser(player))
                {
                    ICBMClassic.packetHandler.sendToPlayer(packet, (EntityPlayerMP) player);
                }
                else
                {
                    it.remove();
                }
            }
        }
    }

    protected boolean isValidGuiUser(EntityPlayer player)
    {
        return player.openContainer != null;
    }

    @Override
    public final List<EntityPlayer> getPlayersUsing()
    {
        return playersWithGUI;
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, IPacket type)
    {
        if (isClient())
        {
            if (id == DESC_PACKET_ID)
            {
                readDescPacket(buf);
                return true;
            }
        }
        return false;
    }

    @Override
    public final ByteBuf writeBytes(ByteBuf var1)
    {
        //Using this as a redirect for the desc packet
        writeDescPacket(var1);
        return var1;
    }

    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeInt(_tier.ordinal());
    }

    public void readDescPacket(ByteBuf buf)
    {
        _tier = EnumTier.get(buf.readInt());
    }

    /**
     * Packet sent to GUI users
     *
     * @return
     */
    protected PacketTile getGUIPacket()
    {
        return getDescPacket();
    }

    public boolean isServer()
    {
        return world != null && !world.isRemote;
    }

    public boolean isClient()
    {
        return world != null && world.isRemote;
    }

    public EnumFacing getRotation()
    {
        IBlockState state = getBlockState();
        if (state.getProperties().containsKey(BlockICBM.ROTATION_PROP))
        {
            return state.getValue(BlockICBM.ROTATION_PROP);
        }
        return EnumFacing.NORTH;
    }

    public void setRotation(EnumFacing facingDirection)
    {
        //Only update if state has changed
        if (facingDirection != getRotation())
        {
            //Update block state
            world.setBlockState(pos, getBlockState().withProperty(BlockICBM.ROTATION_PROP, facingDirection));
        }
    }

    public EnumTier getTier()
    {
        return _tier;
    }

    public void setTier(EnumTier tier)
    {
        if (tier != getTier())
        {
            this._tier = tier;
            world.setBlockState(pos, getBlockState().withProperty(BlockICBM.TIER_PROP, tier));
        }
    }

    public IBlockState getBlockState()
    {
        return world.getBlockState(getPos());
    }

    @Override
    public World world()
    {
        return getWorld();
    }

    @Override
    public double z()
    {
        return getPos().getZ();
    }

    @Override
    public double x()
    {
        return getPos().getX();
    }

    @Override
    public double y()
    {
        return getPos().getY();
    }

    public boolean hasPower()
    {
        return true;
    }

    public void setEnergy(int energy)
    {

    }

    public int getEnergy()
    {
        return 0;
    }

    public int getEnergyConsumption()
    {
        return 100000;
    }

    public int getEnergyBufferSize()
    {
        return getEnergyConsumption() * 2;
    }

    public boolean checkExtract()
    {
        return getEnergy() >= getEnergyConsumption();
    }

    public void extractEnergy()
    {

    }

    @Override
    public boolean openGui(EntityPlayer player, int requestedID)
    {
        player.openGui(ICBMClassic.INSTANCE, requestedID, world, xi(), yi(), zi());
        return true;
    }
}
