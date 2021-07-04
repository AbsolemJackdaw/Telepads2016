package subaraki.telepads.network.client;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.utility.ClientReferences;

public class CPacketRequestNamingScreen implements IPacketBase {

    public CPacketRequestNamingScreen() {

    }

    private BlockPos pos;

    public CPacketRequestNamingScreen(BlockPos pos) {

        this.pos = pos;
    }

    public CPacketRequestNamingScreen(PacketBuffer buf) {

        this.decode(buf);
    }

    @Override
    public void encode(PacketBuffer buf)
    {

        buf.writeBlockPos(pos);
    }

    @Override
    public void decode(PacketBuffer buf)
    {

        pos = buf.readBlockPos();
    }

    @Override
    public void handle(Supplier<Context> context)
    {

        context.get().enqueueWork(() -> {
            // use a layer of indirection when subscribing client events to avoid
            // classloading client classes on server
            if (FMLEnvironment.dist == Dist.CLIENT)
                ClientReferences.openNamingScreen(pos);
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id)
    {

        NetworkHandler.NETWORK.registerMessage(id, CPacketRequestNamingScreen.class, CPacketRequestNamingScreen::encode, CPacketRequestNamingScreen::new,
                CPacketRequestNamingScreen::handle);
    }

}
