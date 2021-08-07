package subaraki.telepads.network.client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.utility.ClientReferences;

import java.util.function.Supplier;

public class CPacketRequestNamingScreen implements IPacketBase {

    private BlockPos pos;

    public CPacketRequestNamingScreen() {

    }

    public CPacketRequestNamingScreen(BlockPos pos) {

        this.pos = pos;
    }

    public CPacketRequestNamingScreen(FriendlyByteBuf buf) {

        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            // use a layer of indirection when subscribing client events to avoid
            // classloading client classes on server
            if (FMLEnvironment.dist == Dist.CLIENT)
                ClientReferences.openNamingScreen(pos);
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, CPacketRequestNamingScreen.class, CPacketRequestNamingScreen::encode, CPacketRequestNamingScreen::new,
                CPacketRequestNamingScreen::handle);
    }

}
