package com.github.qq44920040;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Plugin(id= "chatreplace",name="Chat Replace",version = "1.0.0")
public class ChatReplace {
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    private Map<String,String > hashmao = new HashMap<>();

    @Inject
    private Logger logger;


    @Listener
    public void PlayerChat(MessageChannelEvent.Chat event){
        //System.out.println("join");
        if (!event.getCause().first(Player.class).isPresent()) {
            return;
        }
        Text message = event.getMessage().toText();
        String serialize = TextSerializers.JSON.serialize(message);
        Set<String> strings = hashmao.keySet();
        for (String biaoqing:strings){
            serialize = serialize.replace(biaoqing,hashmao.get(biaoqing));
        }
        event.setMessage(TextSerializers.JSON.deserialize(serialize));
    }


    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("ChatReplace Start");
        Path potentialFile = this.defaultConfig;
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(potentialFile).build();
        try {
            ConfigurationNode rootNode = loader.load();
            if (rootNode.getChildrenMap().size() == 0) {
                URL jarConfigFile = Sponge.getAssetManager().getAsset(this, "chatreplace.conf").get().getUrl();
                ConfigurationLoader<CommentedConfigurationNode> loader2 = HoconConfigurationLoader.builder().setURL(jarConfigFile).build();
                rootNode = loader2.load();
                loader.save(rootNode);
            }
            List<String> banedmsg = rootNode.getNode(new Object[] { "msg" }).getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
            for (String s:banedmsg){
                System.out.println(s);
                String[] split = s.split(":");
                hashmao.put(split[0],split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onGameStop(GameStoppedServerEvent event){
        logger.info("ChatReplace Stop");
    }
}
