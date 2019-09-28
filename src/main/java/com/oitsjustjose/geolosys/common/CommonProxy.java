package com.oitsjustjose.geolosys.common;

import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.common.network.NetworkManager;
import com.oitsjustjose.geolosys.common.network.PacketStackSurface;
import com.oitsjustjose.geolosys.common.network.PacketStackUnderground;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.network.PacketDistributor;

import java.io.File;

public class CommonProxy
{
    public static NetworkManager networkManager = new NetworkManager();
    public static int discriminator = 0;

    public void init()
    {
        networkManager.networkWrapper.registerMessage(CommonProxy.discriminator++, PacketStackSurface.class, PacketStackSurface::encode, PacketStackSurface::decode, PacketStackSurface::handleServer);
        networkManager.networkWrapper.registerMessage(CommonProxy.discriminator++, PacketStackUnderground.class, PacketStackUnderground::encode, PacketStackUnderground::decode, PacketStackUnderground::handleServer);
    }

    public void throwDownloadError(File jsonFile)
    {
        Geolosys.getInstance().LOGGER.error("File " + jsonFile.getAbsolutePath()
                + " could neither be found nor downloaded. "
                + "You can download the file at https://raw.githubusercontent.com/oitsjustjose/Geolosys/1.12.x/geolosys_ores.json "
                + "and put it in your config folder manually if you wish (it will need to be renamed \"geolosys.json\").");
    }

    public void sendProspectingMessage(PlayerEntity player, ItemStack stack, Direction direction)
    {
        if (!(player instanceof ServerPlayerEntity))
        {
            return;
        }
        if (direction != null)
        {
            PacketStackUnderground msg = new PacketStackUnderground(stack, direction.getName());
            networkManager.networkWrapper.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), msg);
        }
        else
        {
            PacketStackSurface msg = new PacketStackSurface(stack);
            networkManager.networkWrapper.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), msg);
        }
    }
}
